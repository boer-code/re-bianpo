package cn.iocoder.yudao.module.iot.gateway.protocol.mqtt.handler.upstream;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.math.Calculator;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.iot.core.biz.IotDeviceCommonApi;
import cn.iocoder.yudao.module.iot.core.biz.dto.IotDeviceAutoRegisterReqDTO;
import cn.iocoder.yudao.module.iot.core.biz.dto.IotDevicePayloadMappingRespDTO;
import cn.iocoder.yudao.module.iot.core.biz.dto.IotDeviceRespDTO;
import cn.iocoder.yudao.module.iot.core.enums.IotDeviceMessageMethodEnum;
import cn.iocoder.yudao.module.iot.core.mq.message.IotDeviceMessage;
import cn.iocoder.yudao.module.iot.core.topic.state.IotDeviceStateUpdateReqDTO;
import cn.iocoder.yudao.module.iot.core.topic.property.IotDevicePropertyPostReqDTO;
import cn.iocoder.yudao.module.iot.core.enums.device.IotDeviceStateEnum;
import cn.iocoder.yudao.module.iot.gateway.protocol.mqtt.IotMqttConfig;
import cn.iocoder.yudao.module.iot.gateway.service.device.message.IotDeviceMessageService;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MQTT 统一原始报文处理器
 */
@Slf4j
public class IotMqttRawUpstreamHandler {

    private final IotDeviceCommonApi deviceApi;
    private final IotDeviceMessageService deviceMessageService;
    private final IotMqttConfig mqttConfig;
    private final String serverId;

    public IotMqttRawUpstreamHandler(IotDeviceCommonApi deviceApi,
                                     IotDeviceMessageService deviceMessageService,
                                     IotMqttConfig mqttConfig,
                                     String serverId) {
        this.deviceApi = deviceApi;
        this.deviceMessageService = deviceMessageService;
        this.mqttConfig = mqttConfig;
        this.serverId = serverId;
    }

    @SuppressWarnings("unchecked")
    public void handleRawPayload(byte[] payload) {
        Map<String, Object> payloadMap = JsonUtils.parseObject(payload, Map.class);
        if (MapUtil.isEmpty(payloadMap)) {
            return;
        }
        String areaNo = Convert.toStr(payloadMap.get("AN"), null);
        String deviceNo = Convert.toStr(payloadMap.get("DN"), null);
        if (StrUtil.hasBlank(areaNo, deviceNo)) {
            log.warn("[handleRawPayload][缺少 AN 或 DN，payload={}]", JsonUtils.toJsonString(payloadMap));
            return;
        }

        // 1. 自动注册并获取设备
        IotDeviceAutoRegisterReqDTO registerReq = new IotDeviceAutoRegisterReqDTO();
        registerReq.setProductKey(mqttConfig.getRawRegisterProductKey());
        registerReq.setAreaNo(areaNo);
        registerReq.setDeviceNo(deviceNo);
        CommonResult<IotDeviceRespDTO> registerResult = deviceApi.autoRegisterDevice(registerReq);
        if (registerResult.isError()) {
            log.error("[handleRawPayload][自动注册失败，AN={}，DN={}，code={}，msg={}，payload={}]",
                    areaNo, deviceNo, registerResult.getCode(), registerResult.getMsg(), JsonUtils.toJsonString(payloadMap));
            return;
        }
        IotDeviceRespDTO device = registerResult.getData();
        if (device == null) {
            log.warn("[handleRawPayload][自动注册返回空设备，AN={}，DN={}，payload={}]", areaNo, deviceNo, JsonUtils.toJsonString(payloadMap));
            return;
        }

        // 心跳报文独立处理：不进入 RAW 数据映射流程，避免触发“开始处理 raw 上报”等业务日志
        if (isHeartbeatPayload(payloadMap)) {
            IotDeviceMessage heartbeatMessage = IotDeviceMessage.requestOf(
                    IotDeviceMessageMethodEnum.STATE_UPDATE.getMethod(),
                    new IotDeviceStateUpdateReqDTO(IotDeviceStateEnum.ONLINE.getState()));
            log.info("[handleRawPayload][心跳上报 ONLINE deviceId={} AN={} DN={}]", device.getId(), areaNo, deviceNo);
            deviceMessageService.sendDeviceMessage(heartbeatMessage, device.getProductKey(), device.getDeviceName(), serverId);
            return;
        }

        // 2. 查询设备映射
        CommonResult<List<IotDevicePayloadMappingRespDTO>> mappingResult = deviceApi.getEnabledPayloadMappings(device.getId());
        if (mappingResult.isError()) {
            log.error("[handleRawPayload][查询映射失败，deviceId={}，code={}，msg={}，payload={}]",
                    device.getId(), mappingResult.getCode(), mappingResult.getMsg(), JsonUtils.toJsonString(payloadMap));
            return;
        }
        List<IotDevicePayloadMappingRespDTO> mappings = mappingResult.getData();
        if (mappings == null || mappings.isEmpty()) {
            log.debug("[handleRawPayload][设备({}/{}) 暂无映射配置，deviceId={}]", areaNo, deviceNo, device.getId());
            return;
        }

        int cl = Convert.toInt(payloadMap.get("CL"), 0);
        // 2.1 基于原始报文 CL 判断模块启用情况
        List<String> enabledModulePrefixes = getEnabledModulePrefixes(cl);
        List<String> missingModulePrefixes = new ArrayList<>();
        for (String prefix : enabledModulePrefixes) {
            if (!hasAnyMappingForPrefix(mappings, prefix)) {
                missingModulePrefixes.add(prefix);
            }
        }
        if (!missingModulePrefixes.isEmpty()) {
            // 不阻断整条上报：只提示“开启模块缺少映射”，便于排查为何某些模块数据未上报
            log.warn("[handleRawPayload][设备({}/{}) 模块{}已开启(CL={})，但未配置任何映射，将仅上报已配置模块的数据。deviceId={}，payload={}]",
                    areaNo, deviceNo, missingModulePrefixes, cl, device.getId(), JsonUtils.toJsonString(payloadMap));
        }

        log.info("[handleRawPayload][开始处理 raw 上报 deviceId={} AN={} DN={} CL={} enabledModules={} mappingCount={}]",
                device.getId(), areaNo, deviceNo, cl, enabledModulePrefixes, mappings.size());

        Map<String, Object> properties = new HashMap<>();
        List<String> appliedDetailLines = new ArrayList<>();
        for (IotDevicePayloadMappingRespDTO mapping : mappings) {
            String channelKey = mapping.getChannelKey();
            // 仅处理上行与双向
            if (!isUpstreamDirection(mapping.getDirection())) {
                log.debug("[handleRawPayload][跳过映射 direction 非上行 deviceId={} channelKey={} direction={}]",
                        device.getId(), channelKey, mapping.getDirection());
                continue;
            }
            // 按 channelKey 前缀 + 报文 CL 判断启用模块
            String modulePrefix = getModulePrefix(channelKey);
            if (modulePrefix != null && !enabledModulePrefixes.contains(modulePrefix)) {
                log.debug("[handleRawPayload][跳过映射 模块未开启 deviceId={} channelKey={} module={} CL={}]",
                        device.getId(), channelKey, modulePrefix, cl);
                continue;
            }
            Object rawValue = payloadMap.get(channelKey);
            if (rawValue == null) {
                log.debug("[handleRawPayload][跳过映射 报文无字段 deviceId={} channelKey={}]",
                        device.getId(), channelKey);
                continue;
            }
            Object value = transformValue(rawValue, mapping.getFormula(), mapping.getZeroOffset());
            properties.put(mapping.getThingModelIdentifier(), value);
            appliedDetailLines.add(String.format(
                    "%s -> thingModel=%s direction=%s formula=%s zeroOffset=%s raw=%s -> out=%s",
                    channelKey,
                    mapping.getThingModelIdentifier(),
                    mapping.getDirection(),
                    StrUtil.blankToDefault(mapping.getFormula(), "-"),
                    mapping.getZeroOffset() != null ? mapping.getZeroOffset().toPlainString() : "-",
                    rawValue,
                    value));
        }
        if (properties.isEmpty()) {
            log.info("[handleRawPayload][无属性可上报 deviceId={} AN={} DN={} CL={}（检查 CL 是否开启对应模块、报文是否含 channelKey、映射方向）]",
                    device.getId(), areaNo, deviceNo, cl);
            return;
        }

        log.info("[handleRawPayload][thing.property.post deviceId={} AN={} DN={} productKey={} deviceName={} 明细: {}]",
                device.getId(), areaNo, deviceNo, device.getProductKey(), device.getDeviceName(),
                String.join(" | ", appliedDetailLines));
        log.info("[handleRawPayload][thing.property.post 汇总 params={}]", JsonUtils.toJsonString(properties));

        IotDeviceMessage message = IotDeviceMessage.requestOf(
                IotDeviceMessageMethodEnum.PROPERTY_POST.getMethod(),
                IotDevicePropertyPostReqDTO.of(properties));
        deviceMessageService.sendDeviceMessage(message, device.getProductKey(), device.getDeviceName(), serverId);
    }

    private boolean isUpstreamDirection(Integer direction) {
        return direction == null || direction == 0 || direction == 2;
    }

    private List<String> getEnabledModulePrefixes(int cl) {
        // 协议约定：高 7 位有效（bit7..bit1），bit0 保留
        // bit7~bit1 分别代表 U3/U4/U5/A1/A2/D2/D1
        List<String> result = new ArrayList<>(7);
        if ((cl & 0b1000_0000) != 0) {
            result.add("U3");
        }
        if ((cl & 0b0100_0000) != 0) {
            result.add("U4");
        }
        if ((cl & 0b0010_0000) != 0) {
            result.add("U5");
        }
        if ((cl & 0b0001_0000) != 0) {
            result.add("A1");
        }
        if ((cl & 0b0000_1000) != 0) {
            result.add("A2");
        }
        if ((cl & 0b0000_0100) != 0) {
            result.add("D2");
        }
        if ((cl & 0b0000_0010) != 0) {
            result.add("D1");
        }
        return result;
    }

    private boolean hasAnyMappingForPrefix(List<IotDevicePayloadMappingRespDTO> mappings, String prefix) {
        for (IotDevicePayloadMappingRespDTO mapping : mappings) {
            if (mapping == null || StrUtil.isBlank(mapping.getChannelKey())) {
                continue;
            }
            if (StrUtil.startWithIgnoreCase(mapping.getChannelKey(), prefix)) {
                return true;
            }
        }
        return false;
    }

    private String getModulePrefix(String channelKey) {
        if (StrUtil.isBlank(channelKey)) {
            return null;
        }
        if (StrUtil.startWithIgnoreCase(channelKey, "U3")) {
            return "U3";
        }
        if (StrUtil.startWithIgnoreCase(channelKey, "U4")) {
            return "U4";
        }
        if (StrUtil.startWithIgnoreCase(channelKey, "U5")) {
            return "U5";
        }
        if (StrUtil.startWithIgnoreCase(channelKey, "A1")) {
            return "A1";
        }
        if (StrUtil.startWithIgnoreCase(channelKey, "A2")) {
            return "A2";
        }
        if (StrUtil.startWithIgnoreCase(channelKey, "D1")) {
            return "D1";
        }
        if (StrUtil.startWithIgnoreCase(channelKey, "D2")) {
            return "D2";
        }
        return null;
    }

    private boolean isHeartbeatPayload(Map<String, Object> payloadMap) {
        // 心跳报文约定包含 HN，且通常不携带业务采集点位
        return payloadMap.containsKey("HN");
    }

    private Object transformValue(Object rawValue, String formula, BigDecimal zeroOffset) {
        if (!NumberUtil.isNumber(Convert.toStr(rawValue))) {
            return rawValue;
        }
        BigDecimal numeric = NumberUtil.toBigDecimal(Convert.toStr(rawValue));
        if (StrUtil.isNotBlank(formula)) {
            // Calculator 支持基础表达式；通过 x 替换实现动态公式
            String expr = StrUtil.replace(formula, "x", numeric.toPlainString());
            numeric = NumberUtil.toBigDecimal(Calculator.conversion(expr));
        }
        if (zeroOffset != null) {
            numeric = numeric.subtract(zeroOffset);
        }
        return numeric.stripTrailingZeros();
    }

}

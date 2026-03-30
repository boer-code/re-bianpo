package cn.iocoder.yudao.module.iot.gateway.protocol.mqtt.handler.upstream;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.math.Calculator;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
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
        IotDeviceRespDTO device = deviceApi.autoRegisterDevice(registerReq).getCheckedData();
        if (device == null) {
            return;
        }

        // 2. 查询设备映射
        List<IotDevicePayloadMappingRespDTO> mappings = deviceApi.getEnabledPayloadMappings(device.getId())
                .getCheckedData();
        if (mappings == null || mappings.isEmpty()) {
            log.debug("[handleRawPayload][设备({}/{}) 暂无映射配置，deviceId={}]", areaNo, deviceNo, device.getId());
            return;
        }

        int cl = Convert.toInt(payloadMap.get("CL"), 0);
        Map<String, Object> properties = new HashMap<>();
        for (IotDevicePayloadMappingRespDTO mapping : mappings) {
            // 仅处理上行与双向
            if (!isUpstreamDirection(mapping.getDirection())) {
                continue;
            }
            if (mapping.getClBitIndex() != null && !isClBitEnabled(cl, mapping.getClBitIndex())) {
                continue;
            }
            Object rawValue = payloadMap.get(mapping.getChannelKey());
            if (rawValue == null) {
                continue;
            }
            Object value = transformValue(rawValue, mapping.getFormula(), mapping.getZeroOffset());
            properties.put(mapping.getThingModelIdentifier(), value);
        }
        if (properties.isEmpty()) {
            // 心跳包或尚未配置映射时，也写入一条上行状态消息，保证“消息统计”和设备在线状态可见
            if (isHeartbeatPayload(payloadMap)) {
                IotDeviceMessage heartbeatMessage = IotDeviceMessage.requestOf(
                        IotDeviceMessageMethodEnum.STATE_UPDATE.getMethod(),
                        new IotDeviceStateUpdateReqDTO(IotDeviceStateEnum.ONLINE.getState()));
                deviceMessageService.sendDeviceMessage(heartbeatMessage, device.getProductKey(), device.getDeviceName(), serverId);
            }
            return;
        }

        IotDeviceMessage message = IotDeviceMessage.requestOf(
                IotDeviceMessageMethodEnum.PROPERTY_POST.getMethod(),
                IotDevicePropertyPostReqDTO.of(properties));
        deviceMessageService.sendDeviceMessage(message, device.getProductKey(), device.getDeviceName(), serverId);
    }

    private boolean isUpstreamDirection(Integer direction) {
        return direction == null || direction == 0 || direction == 2;
    }

    private boolean isClBitEnabled(int cl, int bitIndex) {
        if (bitIndex < 1 || bitIndex > 7) {
            return true;
        }
        // 协议约定：高 7 位有效（bit7..bit1），bit0 保留
        // bit7~bit1 分别代表 U3/U4/U5/A1/A2/D2/D1
        int actualBit = 8 - bitIndex; // bitIndex=1->bit7, 2->bit6, ... 7->bit1
        return (cl & (1 << actualBit)) != 0;
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

package cn.iocoder.yudao.module.iot.gateway.protocol.mqtt.handler.downstream;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.iot.core.biz.IotDeviceCommonApi;
import cn.iocoder.yudao.module.iot.core.biz.dto.IotDeviceGetReqDTO;
import cn.iocoder.yudao.module.iot.core.biz.dto.IotDeviceRespDTO;
import cn.iocoder.yudao.module.iot.core.mq.message.IotDeviceMessage;
import cn.iocoder.yudao.module.iot.core.util.IotDeviceMessageUtils;
import cn.iocoder.yudao.module.iot.gateway.protocol.mqtt.IotMqttConfig;
import cn.iocoder.yudao.module.iot.gateway.protocol.mqtt.manager.IotMqttConnectionManager;
import cn.iocoder.yudao.module.iot.gateway.service.device.message.IotDeviceMessageService;
import cn.iocoder.yudao.module.iot.gateway.util.IotMqttTopicUtils;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * IoT 网关 MQTT 协议：下行消息处理器
 *
 * @author 芋道源码
 */
@Slf4j
@RequiredArgsConstructor
public class IotMqttDownstreamHandler {

    private static final DateTimeFormatter RAW_DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter RAW_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final IotDeviceMessageService deviceMessageService;

    private final IotMqttConnectionManager connectionManager;
    private final IotDeviceCommonApi deviceApi;
    private final IotMqttConfig mqttConfig;

    /**
     * 处理下行消息
     *
     * @param message 设备消息
     */
    public void handle(IotDeviceMessage message) {
        try {
            log.info("[handle][处理下行消息，设备 ID: {}，方法: {}，消息 ID: {}]",
                    message.getDeviceId(), message.getMethod(), message.getId());

            // 1. 解析设备信息（raw/普通设备都需要用于序列化）
            CommonResult<IotDeviceRespDTO> deviceResult = deviceApi.getDevice(
                    new IotDeviceGetReqDTO().setId(message.getDeviceId()));
            deviceResult.checkError();
            IotDeviceRespDTO device = deviceResult.getData();
            Assert.notNull(device, "设备不存在");

            // 2. raw 设备走共享连接（raw 上报主题 + /reply）
            if (StrUtil.equals(device.getProductKey(), mqttConfig.getRawRegisterProductKey())) {
                sendByRawSharedConnection(message, device);
                return;
            }

            // 3. 普通设备走设备连接
            IotMqttConnectionManager.ConnectionInfo connectionInfo = connectionManager.getConnectionInfoByDeviceId(
                    message.getDeviceId());
            if (connectionInfo == null) {
                log.warn("[handle][连接信息不存在，设备 ID: {}，方法: {}，消息 ID: {}]",
                        message.getDeviceId(), message.getMethod(), message.getId());
                return;
            }

            // 3.1 序列化消息
            byte[] payload = deviceMessageService.serializeDeviceMessage(message, connectionInfo.getProductKey(),
                    connectionInfo.getDeviceName());
            Assert.isTrue(payload != null && payload.length > 0, "消息编码结果不能为空");
            // 3.2 构建主题
            Assert.notBlank(message.getMethod(), "消息方法不能为空");
            boolean isReply = IotDeviceMessageUtils.isReplyMessage(message);
            String topic = IotMqttTopicUtils.buildTopicByMethod(message.getMethod(), connectionInfo.getProductKey(),
                    connectionInfo.getDeviceName(), isReply);
            Assert.notBlank(topic, "主题不能为空");

            // 4. 发送到设备
            boolean success = connectionManager.sendToDevice(message.getDeviceId(), topic, payload,
                    MqttQoS.AT_LEAST_ONCE.value(), false);
            if (!success) {
                throw new RuntimeException("下行消息发送失败");
            }
            log.info("[handle][下行消息发送成功，设备 ID: {}，方法: {}，消息 ID: {}，主题: {}，数据长度: {} 字节]",
                    message.getDeviceId(), message.getMethod(), message.getId(), topic, payload.length);
        } catch (Exception e) {
            log.error("[handle][处理下行消息失败，设备 ID: {}，方法: {}，消息内容: {}]",
                    message.getDeviceId(), message.getMethod(), message, e);
        }
    }

    private void sendByRawSharedConnection(IotDeviceMessage message, IotDeviceRespDTO device) {
        // 1. raw 下行使用固定 JSON 结构（DAY/TIM/AN/DN）
        byte[] payload = JsonUtils.toJsonByte(buildRawReplyPayload(message, device));
        Assert.isTrue(payload != null && payload.length > 0, "消息编码结果不能为空");
        // 2. raw 回复主题固定为 raw 上报主题 + /reply
        String topic = IotMqttTopicUtils.buildRawReplyTopic(mqttConfig.getRawTopicUp());
        Assert.notBlank(topic, "raw 回复主题不能为空");
        // 3. 通过 raw 共享连接下发
        boolean success = connectionManager.sendToRawSharedConnection(topic, payload,
                MqttQoS.AT_LEAST_ONCE.value(), false);
        if (!success) {
            throw new RuntimeException("raw 下行消息发送失败");
        }
        log.info("[sendByRawSharedConnection][raw 下行消息发送成功，设备 ID: {}，方法: {}，消息 ID: {}，主题: {}，数据长度: {} 字节]",
                message.getDeviceId(), message.getMethod(), message.getId(), topic, payload.length);
    }

    private Map<String, Object> buildRawReplyPayload(IotDeviceMessage message, IotDeviceRespDTO device) {
        LocalDateTime reportTime = message.getReportTime() != null ? message.getReportTime() : LocalDateTime.now();
        String[] anDn = parseAnDnFromDeviceName(device.getDeviceName());
        String msg = resolveEnglishMsg(message.getCode(), message.getMsg());
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("id", message.getId());
        payload.put("DAY", RAW_DAY_FORMATTER.format(reportTime));
        payload.put("TIM", RAW_TIME_FORMATTER.format(reportTime));
        payload.put("AN", anDn[0]);
        payload.put("DN", anDn[1]);
        payload.put("requestId", message.getRequestId());
        payload.put("code", message.getCode());
        payload.put("msg", msg);
        if (message.getData() != null) {
            payload.put("data", message.getData());
        }
        if (message.getParams() != null) {
            payload.put("params", message.getParams());
        }
        return payload;
    }

    private String[] parseAnDnFromDeviceName(String deviceName) {
        // 约定 raw 设备名格式：raw_{AN}_{DN}
        if (StrUtil.isBlank(deviceName)) {
            return new String[]{"", ""};
        }
        String[] parts = deviceName.split("_");
        if (parts.length >= 3) {
            return new String[]{parts[1], parts[2]};
        }
        // 兜底：格式不符合时保留原值到 AN，DN 置空，避免丢失定位信息
        return new String[]{deviceName, ""};
    }

    private String resolveEnglishMsg(Integer code, String originalMsg) {
        if (code != null && code == 0) {
            return "Success";
        }
        if (StrUtil.isBlank(originalMsg)) {
            return "Error";
        }
        if (StrUtil.equalsAny(originalMsg, "成功", "操作成功")) {
            return "Success";
        }
        if (StrUtil.contains(originalMsg, "失败")) {
            return "Failed";
        }
        if (StrUtil.contains(originalMsg, "不存在")) {
            return "Not Found";
        }
        if (StrUtil.contains(originalMsg, "超时")) {
            return "Timeout";
        }
        if (StrUtil.contains(originalMsg, "未授权") || StrUtil.contains(originalMsg, "无权限")) {
            return "Unauthorized";
        }
        return "Error";
    }

}

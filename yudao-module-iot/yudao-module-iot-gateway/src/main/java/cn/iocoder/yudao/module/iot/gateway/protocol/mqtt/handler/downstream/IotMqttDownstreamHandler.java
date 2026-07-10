package cn.iocoder.yudao.module.iot.gateway.protocol.mqtt.handler.downstream;

import cn.hutool.core.lang.Assert;
import cn.iocoder.yudao.module.iot.core.mq.message.IotDeviceMessage;
import cn.iocoder.yudao.module.iot.core.util.IotDeviceMessageUtils;
import cn.iocoder.yudao.module.iot.gateway.protocol.mqtt.IotMqttConfig;
import cn.iocoder.yudao.module.iot.gateway.protocol.mqtt.manager.IotMqttConnectionManager;
import cn.iocoder.yudao.module.iot.gateway.service.device.message.IotDeviceMessageService;
import cn.iocoder.yudao.module.iot.gateway.util.IotMqttTopicUtils;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * IoT 网关 MQTT 协议：下行消息处理器
 *
 * @author 芋道源码
 */
@Slf4j
@RequiredArgsConstructor
public class IotMqttDownstreamHandler {

    private final IotDeviceMessageService deviceMessageService;

    private final IotMqttConnectionManager connectionManager;

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

            // 1. 检查设备连接
            IotMqttConnectionManager.ConnectionInfo connectionInfo = connectionManager.getConnectionInfoByDeviceId(
                    message.getDeviceId());
            if (connectionInfo == null) {
                log.warn("[handle][连接信息不存在，设备 ID: {}，方法: {}，消息 ID: {}]",
                        message.getDeviceId(), message.getMethod(), message.getId());
                return;
            }

            // 2. 序列化消息
            boolean isRawDevice = mqttConfig.getRawRegisterProductKey().equals(connectionInfo.getProductKey());
            byte[] payload;
            if (isRawDevice && message.getParams() instanceof String rawPayload) {
                payload = rawPayload.getBytes(StandardCharsets.UTF_8);
            } else {
                payload = deviceMessageService.serializeDeviceMessage(message, connectionInfo.getProductKey(),
                        connectionInfo.getDeviceName());
            }
            Assert.isTrue(payload != null && payload.length > 0, "消息编码结果不能为空");

            // 3. 构建主题：raw 产品走独立 /iot/{后4位}/down，普通设备走 Alink /sys/... 主题
            String topic = buildDownTopic(message, connectionInfo);
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

    /**
     * 构建下行主题
     * <p>
     * raw 设备（productKey == rawRegisterProductKey）：从 deviceName 解析 AN/DN，拼 /iot/{后4位}/down；
     * 普通设备：走 Alink 规范 /sys/{productKey}/{deviceName}/{method}[_reply]。
     *
     * @param message        设备消息
     * @param connectionInfo 连接信息
     * @return 下行主题；raw 设备名无法解析时返回 null
     */
    private String buildDownTopic(IotDeviceMessage message, IotMqttConnectionManager.ConnectionInfo connectionInfo) {
        // 3.1 raw 设备：独立下行 topic
        if (mqttConfig.getRawRegisterProductKey().equals(connectionInfo.getProductKey())) {
            String[] ad = IotMqttTopicUtils.parseRawAreaAndDeviceNo(connectionInfo.getDeviceName());
            if (ad == null) {
                log.warn("[buildDownTopic][raw 设备 deviceName 无法解析 AN/DN，设备 ID: {}，deviceName: {}]",
                        message.getDeviceId(), connectionInfo.getDeviceName());
                return null;
            }
            return mqttConfig.buildRawTopicDown(ad[0], ad[1]);
        }
        // 3.2 普通设备：Alink 主题
        Assert.notBlank(message.getMethod(), "消息方法不能为空");
        boolean isReply = IotDeviceMessageUtils.isReplyMessage(message);
        return IotMqttTopicUtils.buildTopicByMethod(message.getMethod(), connectionInfo.getProductKey(),
                connectionInfo.getDeviceName(), isReply);
    }

}

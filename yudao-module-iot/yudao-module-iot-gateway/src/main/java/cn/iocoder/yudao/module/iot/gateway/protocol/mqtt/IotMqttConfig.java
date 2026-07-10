package cn.iocoder.yudao.module.iot.gateway.protocol.mqtt;

import cn.hutool.core.util.StrUtil;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * IoT 网关 MQTT 协议配置
 *
 * @author 芋道源码
 */
@Data
public class IotMqttConfig {

    /**
     * 最大消息大小（字节）
     */
    @NotNull(message = "最大消息大小不能为空")
    @Min(value = 1024, message = "最大消息大小不能小于 1024 字节")
    private Integer maxMessageSize = 8192;

    /**
     * 连接超时时间（秒）
     */
    @NotNull(message = "连接超时时间不能为空")
    @Min(value = 1, message = "连接超时时间不能小于 1 秒")
    private Integer connectTimeoutSeconds = 60;

    /**
     * raw 设备 clientId 前缀
     * <p>
     * 约定：前缀固定 4 位（如 {@code boer}），实际 clientId = 前缀 + AN 后 2 位 + DN 后 2 位 = 8 位。
     * 例如 AN=00001、DN=00002，则 clientId = {@code boer0102}。
     * 网关以此前缀识别 raw 设备连接。
     */
    @NotBlank(message = "raw 设备 clientId 前缀不能为空")
    private String rawClientIdPrefix = "boer";

    /**
     * raw 用户名
     */
    @NotNull(message = "raw 用户名不能为空")
    private String rawUsername = "admin";

    /**
     * raw 设备密码
     */
    @NotNull(message = "raw 密码不能为空")
    private String rawPassword = "admin";

    /**
     * raw 设备 topic 前缀
     * <p>
     * 约定：上行 topic = 前缀 + AN 后 2 位 + DN 后 2 位 + {@code /up}，
     * 下行 topic = 前缀 + AN 后 2 位 + DN 后 2 位 + {@code /down}。
     * 例如 AN=00001、DN=00002，则上报 topic = {@code /iot/0102/up}，下发 topic = {@code /iot/0102/down}。
     */
    @NotBlank(message = "raw 设备 topic 前缀不能为空")
    private String rawTopicPrefix = "/iot/";

    /**
     * raw 设备自动注册对应产品标识（用于自动注册）
     */
    @NotBlank(message = "原始上报产品标识不能为空")
    private String rawRegisterProductKey = "raw_product";

    // ========== 识别辅助方法 ==========

    /**
     * 判断 clientId 是否为 raw 设备（以配置的前缀开头）
     *
     * @param clientId MQTT 客户端 ID
     * @return 是否 raw 设备
     */
    public boolean isRawClientId(String clientId) {
        return StrUtil.isNotBlank(clientId) && clientId.startsWith(rawClientIdPrefix);
    }

    /**
     * 构建下行 topic：前缀 + AN 后 2 位 + DN 后 2 位 + /down
     *
     * @param areaNo   区域号（AN）
     * @param deviceNo 设备号（DN）
     * @return 下行 topic
     */
    public String buildRawTopicDown(String areaNo, String deviceNo) {
        return rawTopicPrefix + suffix2(areaNo) + suffix2(deviceNo) + "/down";
    }

    /**
     * 判断是否为 raw 设备上行 topic（前缀 + 4 位设备键 + /up）
     *
     * @param topic MQTT topic
     * @return 是否 raw 上行 topic
     */
    public boolean isRawTopicUp(String topic) {
        if (StrUtil.isBlank(topic)) {
            return false;
        }
        return topic.startsWith(rawTopicPrefix) && topic.endsWith("/up")
                && topic.length() == rawTopicPrefix.length() + 4 + "/up".length();
    }

    /**
     * 取编号后 2 位（不足 2 位左补 0）
     */
    private static String suffix2(String no) {
        if (StrUtil.isBlank(no)) {
            return "00";
        }
        if (no.length() <= 2) {
            return StrUtil.fillBefore(no, '0', 2);
        }
        return no.substring(no.length() - 2);
    }

}

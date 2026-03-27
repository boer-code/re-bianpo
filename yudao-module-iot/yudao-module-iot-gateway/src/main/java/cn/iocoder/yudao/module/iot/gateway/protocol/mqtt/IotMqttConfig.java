package cn.iocoder.yudao.module.iot.gateway.protocol.mqtt;

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
     * 管理员设备ID
     */
    @NotNull(message = "管理员设备ID不能为空")
    private String adminClientId = "admin";

    /**
     * 管理员用户名
     */
    @NotNull(message = "管理员用户名不能为空")
    private String adminUsername = "admin";
    
    /**
     * 管理员密码
     */
    @NotNull(message = "管理员密码不能为空")
    private String adminPassword = "admin";

    /**
     * 统一原始上报主题
     */
    @NotBlank(message = "统一原始上报主题不能为空")
    private String rawTopicUp = "/device/raw/up";

    /**
     * 统一原始上报主题对应产品标识（用于自动注册）
     */
    @NotBlank(message = "原始上报产品标识不能为空")
    private String rawRegisterProductKey = "raw_product";
}

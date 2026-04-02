package cn.iocoder.yudao.module.iot.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "yudao.iot")
@Data
public class IotRawProductKeyProperties {

    /**
     * 原始上报自动注册产品标识（raw 产品 productKey）
     */
    private String rawProductKey;

}


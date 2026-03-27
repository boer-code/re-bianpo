package cn.iocoder.yudao.module.iot.core.biz.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * IoT 设备报文字段映射响应 DTO
 */
@Data
public class IotDevicePayloadMappingRespDTO {

    private Long id;
    private Long deviceId;
    private String channelKey;
    private String thingModelIdentifier;
    private Integer clBitIndex;
    private String formula;
    private BigDecimal zeroOffset;
    private Integer direction;
    private Boolean enabled;

}

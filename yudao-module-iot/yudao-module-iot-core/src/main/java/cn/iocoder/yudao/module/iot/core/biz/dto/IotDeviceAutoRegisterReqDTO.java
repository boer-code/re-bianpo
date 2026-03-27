package cn.iocoder.yudao.module.iot.core.biz.dto;

import lombok.Data;

/**
 * IoT 设备自动注册请求 DTO
 */
@Data
public class IotDeviceAutoRegisterReqDTO {

    /**
     * 产品标识
     */
    private String productKey;
    /**
     * 区域号（AN）
     */
    private String areaNo;
    /**
     * 设备号（DN）
     */
    private String deviceNo;

}

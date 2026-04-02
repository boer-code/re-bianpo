package cn.iocoder.yudao.module.iot.dal.dataobject.device;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * IoT 设备报文字段映射 DO
 */
@TableName("iot_device_payload_mapping")
@KeySequence("iot_device_payload_mapping_seq")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IotDevicePayloadMappingDO extends TenantBaseDO {

    @TableId
    private Long id;

    /**
     * 设备编号
     */
    private Long deviceId;
    /**
     * 报文字段（例如 U3D1）
     */
    private String channelKey;
    /**
     * 物模型标识符（例如 temperature）
     */
    private String thingModelIdentifier;
    /**
     * 公式，变量名固定为 x
     */
    private String formula;
    /**
     * 归零偏移
     */
    private BigDecimal zeroOffset;
    /**
     * 映射方向：0上行 1下行 2双向
     */
    private Integer direction;
    /**
     * 是否启用
     */
    private Boolean enabled;

}

package cn.iocoder.yudao.module.iot.dal.dataobject.device;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import cn.iocoder.yudao.module.iot.core.enums.device.IotDeviceStateEnum;
import cn.iocoder.yudao.module.iot.dal.dataobject.product.IotProductDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * IoT 设备上下线记录 DO
 */
@TableName("iot_device_online_record")
@KeySequence("iot_device_online_record_seq")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IotDeviceOnlineRecordDO extends TenantBaseDO {

    /**
     * 记录编号
     */
    @TableId
    private Long id;

    /**
     * 产品标识
     */
    private String productKey;
    /**
     * 设备编号
     *
     * 关联 {@link IotDeviceDO#getId()}
     */
    private Long deviceId;
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 设备状态
     *
     * 枚举 {@link IotDeviceStateEnum}
     */
    private Integer state;

}
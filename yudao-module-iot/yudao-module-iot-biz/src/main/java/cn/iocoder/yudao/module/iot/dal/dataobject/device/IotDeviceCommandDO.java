package cn.iocoder.yudao.module.iot.dal.dataobject.device;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * IoT 设备快捷指令 DO
 */
@TableName("iot_device_command")
@KeySequence("iot_device_command_seq")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IotDeviceCommandDO extends TenantBaseDO {

    @TableId
    private Long id;

    /**
     * 设备编号
     */
    private Long deviceId;

    /**
     * 指令名称
     */
    private String name;

    /**
     * JSON 模板内容
     */
    private String template;

    /**
     * 排序
     */
    private Integer sort;

}

package cn.iocoder.yudao.module.iot.dal.dataobject.device;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * IoT 设备分组 DO
 *
 * @author 芋道源码
 */
@TableName("iot_device_group")
@KeySequence("iot_device_group_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IotDeviceGroupDO extends BaseDO {

    /**
     * 分组 ID
     */
    @TableId
    private Long id;
    /**
     * 分组名字
     */
    private String name;
    /**
     * 分组状态
     *
     * 枚举 {@link cn.iocoder.yudao.framework.common.enums.CommonStatusEnum}
     */
    private Integer status;
    /**
     * 分组描述
     */
    private String description;
    /**
     * 所在地区编号
     */
    private Long regionId;
    /**
     * 站点经度
     */
    private BigDecimal longitude;
    /**
     * 站点纬度
     */
    private BigDecimal latitude;
    /**
     * 站点海拔（米）
     */
    private BigDecimal altitude;

}
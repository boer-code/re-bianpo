package cn.iocoder.yudao.module.iot.dal.mysql.device;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.iot.dal.dataobject.device.IotDeviceOnlineRecordDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * IoT 设备上下线记录 Mapper
 */
@Mapper
public interface IotDeviceOnlineRecordMapper extends BaseMapperX<IotDeviceOnlineRecordDO> {
}
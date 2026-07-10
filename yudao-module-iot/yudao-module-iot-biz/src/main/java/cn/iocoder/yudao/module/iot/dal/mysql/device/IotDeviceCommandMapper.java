package cn.iocoder.yudao.module.iot.dal.mysql.device;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.iot.dal.dataobject.device.IotDeviceCommandDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IotDeviceCommandMapper extends BaseMapperX<IotDeviceCommandDO> {

    default List<IotDeviceCommandDO> selectListByDeviceId(Long deviceId) {
        return selectList(new LambdaQueryWrapperX<IotDeviceCommandDO>()
                .eq(IotDeviceCommandDO::getDeviceId, deviceId)
                .orderByAsc(IotDeviceCommandDO::getSort)
                .orderByDesc(IotDeviceCommandDO::getId));
    }

}

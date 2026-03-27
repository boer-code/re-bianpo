package cn.iocoder.yudao.module.iot.dal.mysql.device;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.iot.dal.dataobject.device.IotDevicePayloadMappingDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * IoT 设备报文字段映射 Mapper
 */
@Mapper
public interface IotDevicePayloadMappingMapper extends BaseMapperX<IotDevicePayloadMappingDO> {

    default IotDevicePayloadMappingDO selectByDeviceIdAndChannelKey(Long deviceId, String channelKey) {
        return selectOne(new LambdaQueryWrapperX<IotDevicePayloadMappingDO>()
                .eq(IotDevicePayloadMappingDO::getDeviceId, deviceId)
                .eq(IotDevicePayloadMappingDO::getChannelKey, channelKey));
    }

    default List<IotDevicePayloadMappingDO> selectListByDeviceId(Long deviceId) {
        return selectList(new LambdaQueryWrapperX<IotDevicePayloadMappingDO>()
                .eq(IotDevicePayloadMappingDO::getDeviceId, deviceId)
                .orderByAsc(IotDevicePayloadMappingDO::getId));
    }

    default List<IotDevicePayloadMappingDO> selectEnabledListByDeviceId(Long deviceId) {
        return selectList(new LambdaQueryWrapperX<IotDevicePayloadMappingDO>()
                .eq(IotDevicePayloadMappingDO::getDeviceId, deviceId)
                .eq(IotDevicePayloadMappingDO::getEnabled, true)
                .orderByAsc(IotDevicePayloadMappingDO::getId));
    }

}

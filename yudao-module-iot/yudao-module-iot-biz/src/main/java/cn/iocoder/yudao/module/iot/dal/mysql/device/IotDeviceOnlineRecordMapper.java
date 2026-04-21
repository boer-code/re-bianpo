package cn.iocoder.yudao.module.iot.dal.mysql.device;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.iot.dal.dataobject.device.IotDeviceOnlineRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * IoT 设备上下线记录 Mapper
 */
@Mapper
public interface IotDeviceOnlineRecordMapper extends BaseMapperX<IotDeviceOnlineRecordDO> {

    default List<IotDeviceOnlineRecordDO> selectRecentList(Integer limit) {
        return selectList(new LambdaQueryWrapperX<IotDeviceOnlineRecordDO>()
                .orderByDesc(IotDeviceOnlineRecordDO::getCreateTime)
                .last("LIMIT " + limit));
    }

}

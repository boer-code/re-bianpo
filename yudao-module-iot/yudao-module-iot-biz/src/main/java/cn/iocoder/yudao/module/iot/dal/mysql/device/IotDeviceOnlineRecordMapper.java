package cn.iocoder.yudao.module.iot.dal.mysql.device;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.iot.dal.dataobject.device.IotDeviceOnlineRecordDO;
import org.apache.ibatis.annotations.Mapper;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * IoT 设备上下线记录 Mapper
 */
@Mapper
public interface IotDeviceOnlineRecordMapper extends BaseMapperX<IotDeviceOnlineRecordDO> {

    default List<IotDeviceOnlineRecordDO> selectRecentList(Integer limit, @Nullable LocalDateTime startTime,
                                                           @Nullable LocalDateTime endTime) {
        return selectList(new LambdaQueryWrapperX<IotDeviceOnlineRecordDO>()
                .betweenIfPresent(IotDeviceOnlineRecordDO::getCreateTime, startTime, endTime)
                .orderByDesc(IotDeviceOnlineRecordDO::getCreateTime)
                .last("LIMIT " + limit));
    }

    default Long selectCountByStateAndCreateTimeRange(@Nullable Integer state, @Nullable LocalDateTime startTime,
                                                      @Nullable LocalDateTime endTime) {
        return selectCount(new LambdaQueryWrapperX<IotDeviceOnlineRecordDO>()
                .eqIfPresent(IotDeviceOnlineRecordDO::getState, state)
                .betweenIfPresent(IotDeviceOnlineRecordDO::getCreateTime, startTime, endTime));
    }

}

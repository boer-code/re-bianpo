package cn.iocoder.yudao.module.iot.service.device;

import cn.iocoder.yudao.module.iot.controller.admin.device.vo.payload.IotDevicePayloadMappingSaveReqVO;
import cn.iocoder.yudao.module.iot.dal.dataobject.device.IotDevicePayloadMappingDO;

import java.util.List;

/**
 * IoT 设备报文字段映射 Service
 */
public interface IotDevicePayloadMappingService {

    Long createMapping(IotDevicePayloadMappingSaveReqVO createReqVO);

    void updateMapping(IotDevicePayloadMappingSaveReqVO updateReqVO);

    void deleteMapping(Long id);

    IotDevicePayloadMappingDO getMapping(Long id);

    List<IotDevicePayloadMappingDO> getMappingsByDeviceId(Long deviceId);

    List<IotDevicePayloadMappingDO> getEnabledMappingsByDeviceId(Long deviceId);

}

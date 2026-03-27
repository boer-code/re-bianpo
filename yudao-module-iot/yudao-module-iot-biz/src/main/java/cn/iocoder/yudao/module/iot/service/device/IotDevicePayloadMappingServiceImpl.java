package cn.iocoder.yudao.module.iot.service.device;

import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.iot.controller.admin.device.vo.payload.IotDevicePayloadMappingSaveReqVO;
import cn.iocoder.yudao.module.iot.dal.dataobject.device.IotDevicePayloadMappingDO;
import cn.iocoder.yudao.module.iot.dal.mysql.device.IotDevicePayloadMappingMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.iot.enums.ErrorCodeConstants.DEVICE_PAYLOAD_MAPPING_EXISTS;
import static cn.iocoder.yudao.module.iot.enums.ErrorCodeConstants.DEVICE_PAYLOAD_MAPPING_NOT_EXISTS;

/**
 * IoT 设备报文字段映射 Service 实现类
 */
@Service
@Validated
public class IotDevicePayloadMappingServiceImpl implements IotDevicePayloadMappingService {

    @Resource
    private IotDevicePayloadMappingMapper payloadMappingMapper;
    @Resource
    private IotDeviceService deviceService;

    @Override
    public Long createMapping(IotDevicePayloadMappingSaveReqVO createReqVO) {
        deviceService.validateDeviceExists(createReqVO.getDeviceId());
        validateMappingUnique(createReqVO.getDeviceId(), createReqVO.getChannelKey(), null);
        IotDevicePayloadMappingDO mapping = BeanUtils.toBean(createReqVO, IotDevicePayloadMappingDO.class);
        payloadMappingMapper.insert(mapping);
        return mapping.getId();
    }

    @Override
    public void updateMapping(IotDevicePayloadMappingSaveReqVO updateReqVO) {
        IotDevicePayloadMappingDO db = validateMappingExists(updateReqVO.getId());
        deviceService.validateDeviceExists(updateReqVO.getDeviceId());
        validateMappingUnique(updateReqVO.getDeviceId(), updateReqVO.getChannelKey(), updateReqVO.getId());
        IotDevicePayloadMappingDO updateObj = BeanUtils.toBean(updateReqVO, IotDevicePayloadMappingDO.class);
        payloadMappingMapper.updateById(updateObj);
        // 特殊：deviceId 可以被变更，调用上层时由业务保障
        if (ObjUtil.notEqual(db.getDeviceId(), updateReqVO.getDeviceId())) {
            deviceService.getDevice(updateReqVO.getDeviceId());
        }
    }

    @Override
    public void deleteMapping(Long id) {
        validateMappingExists(id);
        payloadMappingMapper.deleteById(id);
    }

    @Override
    public IotDevicePayloadMappingDO getMapping(Long id) {
        return payloadMappingMapper.selectById(id);
    }

    @Override
    public List<IotDevicePayloadMappingDO> getMappingsByDeviceId(Long deviceId) {
        return payloadMappingMapper.selectListByDeviceId(deviceId);
    }

    @Override
    public List<IotDevicePayloadMappingDO> getEnabledMappingsByDeviceId(Long deviceId) {
        return payloadMappingMapper.selectEnabledListByDeviceId(deviceId);
    }

    private IotDevicePayloadMappingDO validateMappingExists(Long id) {
        IotDevicePayloadMappingDO mapping = payloadMappingMapper.selectById(id);
        if (mapping == null) {
            throw exception(DEVICE_PAYLOAD_MAPPING_NOT_EXISTS);
        }
        return mapping;
    }

    private void validateMappingUnique(Long deviceId, String channelKey, Long excludeId) {
        IotDevicePayloadMappingDO db = payloadMappingMapper.selectByDeviceIdAndChannelKey(deviceId, channelKey);
        if (db != null && ObjUtil.notEqual(db.getId(), excludeId)) {
            throw exception(DEVICE_PAYLOAD_MAPPING_EXISTS);
        }
    }

}

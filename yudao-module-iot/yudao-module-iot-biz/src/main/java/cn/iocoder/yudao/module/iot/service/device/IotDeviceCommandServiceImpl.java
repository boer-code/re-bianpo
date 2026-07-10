package cn.iocoder.yudao.module.iot.service.device;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.iot.controller.admin.device.vo.command.IotDeviceCommandSaveReqVO;
import cn.iocoder.yudao.module.iot.dal.dataobject.device.IotDeviceCommandDO;
import cn.iocoder.yudao.module.iot.dal.mysql.device.IotDeviceCommandMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.iot.enums.ErrorCodeConstants.DEVICE_COMMAND_NOT_EXISTS;

@Service
@Validated
public class IotDeviceCommandServiceImpl implements IotDeviceCommandService {

    @Resource
    private IotDeviceCommandMapper deviceCommandMapper;

    @Override
    public Long createDeviceCommand(IotDeviceCommandSaveReqVO reqVO) {
        IotDeviceCommandDO command = BeanUtils.toBean(reqVO, IotDeviceCommandDO.class);
        deviceCommandMapper.insert(command);
        return command.getId();
    }

    @Override
    public void updateDeviceCommand(IotDeviceCommandSaveReqVO reqVO) {
        validateDeviceCommandExists(reqVO.getId());
        IotDeviceCommandDO command = BeanUtils.toBean(reqVO, IotDeviceCommandDO.class);
        deviceCommandMapper.updateById(command);
    }

    @Override
    public void deleteDeviceCommand(Long id) {
        validateDeviceCommandExists(id);
        deviceCommandMapper.deleteById(id);
    }

    @Override
    public List<IotDeviceCommandDO> getDeviceCommandListByDeviceId(Long deviceId) {
        return deviceCommandMapper.selectListByDeviceId(deviceId);
    }

    private void validateDeviceCommandExists(Long id) {
        if (deviceCommandMapper.selectById(id) == null) {
            throw exception(DEVICE_COMMAND_NOT_EXISTS);
        }
    }

}

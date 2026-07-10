package cn.iocoder.yudao.module.iot.service.device;

import cn.iocoder.yudao.module.iot.controller.admin.device.vo.command.IotDeviceCommandSaveReqVO;
import cn.iocoder.yudao.module.iot.dal.dataobject.device.IotDeviceCommandDO;
import jakarta.validation.Valid;

import java.util.List;

public interface IotDeviceCommandService {

    Long createDeviceCommand(@Valid IotDeviceCommandSaveReqVO reqVO);

    void updateDeviceCommand(@Valid IotDeviceCommandSaveReqVO reqVO);

    void deleteDeviceCommand(Long id);

    List<IotDeviceCommandDO> getDeviceCommandListByDeviceId(Long deviceId);

}

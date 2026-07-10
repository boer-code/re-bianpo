package cn.iocoder.yudao.module.iot.controller.admin.device;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.iot.controller.admin.device.vo.command.IotDeviceCommandRespVO;
import cn.iocoder.yudao.module.iot.controller.admin.device.vo.command.IotDeviceCommandSaveReqVO;
import cn.iocoder.yudao.module.iot.dal.dataobject.device.IotDeviceCommandDO;
import cn.iocoder.yudao.module.iot.service.device.IotDeviceCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - IoT 设备快捷指令")
@RestController
@RequestMapping("/iot/device-command")
@Validated
public class IotDeviceCommandController {

    @Resource
    private IotDeviceCommandService deviceCommandService;

    @PostMapping("/create")
    @Operation(summary = "创建设备快捷指令")
    @PreAuthorize("@ss.hasPermission('iot:device-command:create')")
    public CommonResult<Long> createDeviceCommand(@Valid @RequestBody IotDeviceCommandSaveReqVO reqVO) {
        return success(deviceCommandService.createDeviceCommand(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新设备快捷指令")
    @PreAuthorize("@ss.hasPermission('iot:device-command:update')")
    public CommonResult<Boolean> updateDeviceCommand(@Valid @RequestBody IotDeviceCommandSaveReqVO reqVO) {
        deviceCommandService.updateDeviceCommand(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除设备快捷指令")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('iot:device-command:delete')")
    public CommonResult<Boolean> deleteDeviceCommand(@RequestParam("id") Long id) {
        deviceCommandService.deleteDeviceCommand(id);
        return success(true);
    }

    @GetMapping("/list")
    @Operation(summary = "获取设备快捷指令列表")
    @Parameter(name = "deviceId", description = "设备编号", required = true)
    @PreAuthorize("@ss.hasPermission('iot:device-command:query')")
    public CommonResult<List<IotDeviceCommandRespVO>> getDeviceCommandList(
            @RequestParam("deviceId") Long deviceId) {
        List<IotDeviceCommandDO> list = deviceCommandService.getDeviceCommandListByDeviceId(deviceId);
        return success(BeanUtils.toBean(list, IotDeviceCommandRespVO.class));
    }

}

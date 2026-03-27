package cn.iocoder.yudao.module.iot.controller.admin.device;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.iot.controller.admin.device.vo.payload.IotDevicePayloadMappingRespVO;
import cn.iocoder.yudao.module.iot.controller.admin.device.vo.payload.IotDevicePayloadMappingSaveReqVO;
import cn.iocoder.yudao.module.iot.dal.dataobject.device.IotDevicePayloadMappingDO;
import cn.iocoder.yudao.module.iot.service.device.IotDevicePayloadMappingService;
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

@Tag(name = "管理后台 - IoT 设备报文字段映射")
@RestController
@RequestMapping("/iot/device-payload-mapping")
@Validated
public class IotDevicePayloadMappingController {

    @Resource
    private IotDevicePayloadMappingService payloadMappingService;

    @PostMapping("/create")
    @Operation(summary = "创建设备报文字段映射")
    @PreAuthorize("@ss.hasPermission('iot:device:update')")
    public CommonResult<Long> createMapping(@Valid @RequestBody IotDevicePayloadMappingSaveReqVO createReqVO) {
        return success(payloadMappingService.createMapping(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新设备报文字段映射")
    @PreAuthorize("@ss.hasPermission('iot:device:update')")
    public CommonResult<Boolean> updateMapping(@Valid @RequestBody IotDevicePayloadMappingSaveReqVO updateReqVO) {
        payloadMappingService.updateMapping(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除设备报文字段映射")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('iot:device:update')")
    public CommonResult<Boolean> deleteMapping(@RequestParam("id") Long id) {
        payloadMappingService.deleteMapping(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得设备报文字段映射")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('iot:device:query')")
    public CommonResult<IotDevicePayloadMappingRespVO> getMapping(@RequestParam("id") Long id) {
        IotDevicePayloadMappingDO mapping = payloadMappingService.getMapping(id);
        return success(BeanUtils.toBean(mapping, IotDevicePayloadMappingRespVO.class));
    }

    @GetMapping("/list-by-device")
    @Operation(summary = "获得设备的报文字段映射列表")
    @Parameter(name = "deviceId", description = "设备编号", required = true)
    @PreAuthorize("@ss.hasPermission('iot:device:query')")
    public CommonResult<List<IotDevicePayloadMappingRespVO>> getMappingListByDevice(@RequestParam("deviceId") Long deviceId) {
        List<IotDevicePayloadMappingDO> list = payloadMappingService.getMappingsByDeviceId(deviceId);
        return success(BeanUtils.toBean(list, IotDevicePayloadMappingRespVO.class));
    }

}

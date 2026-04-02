package cn.iocoder.yudao.module.iot.controller.admin.device;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.iot.framework.config.IotRawProductKeyProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - IoT 原始设备配置")
@RestController
@RequestMapping("/iot/device/raw-config")
@Validated
public class IotDeviceRawConfigController {

    @Resource
    private IotRawProductKeyProperties rawProductKeyProperties;

    @GetMapping("/product-key")
    @Operation(summary = "获得 raw 产品标识（productKey）")
    @PreAuthorize("@ss.hasPermission('iot:device:query')")
    public CommonResult<RawProductKeyRespVO> getRawProductKey() {
        RawProductKeyRespVO respVO = new RawProductKeyRespVO();
        respVO.setProductKey(rawProductKeyProperties.getRawProductKey());
        return success(respVO);
    }

    @Data
    public static class RawProductKeyRespVO {
        private String productKey;
    }

}


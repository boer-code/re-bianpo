package cn.iocoder.yudao.module.iot.controller.admin.device.vo.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - IoT 设备快捷指令新增/修改 Request VO")
@Data
public class IotDeviceCommandSaveReqVO {

    @Schema(description = "指令 ID", example = "1")
    private Long id;

    @Schema(description = "设备编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "42")
    @NotNull(message = "设备编号不能为空")
    private Long deviceId;

    @Schema(description = "指令名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "设置温度")
    @NotEmpty(message = "指令名称不能为空")
    private String name;

    @Schema(description = "JSON 模板内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "JSON 模板不能为空")
    private String template;

    @Schema(description = "排序", example = "0")
    private Integer sort;

}

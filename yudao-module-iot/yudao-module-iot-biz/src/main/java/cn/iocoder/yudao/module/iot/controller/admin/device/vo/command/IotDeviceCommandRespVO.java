package cn.iocoder.yudao.module.iot.controller.admin.device.vo.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IoT 设备快捷指令 Response VO")
@Data
public class IotDeviceCommandRespVO {

    @Schema(description = "指令 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "设备编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "42")
    private Long deviceId;

    @Schema(description = "指令名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "设置温度")
    private String name;

    @Schema(description = "JSON 模板内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private String template;

    @Schema(description = "排序", example = "0")
    private Integer sort;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}

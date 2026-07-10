package cn.iocoder.yudao.module.iot.controller.admin.device.vo.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - IoT Raw 设备消息发送 Request VO")
@Data
public class IotDeviceRawMessageSendReqVO {

    @Schema(description = "设备编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "15")
    @NotNull(message = "设备编号不能为空")
    private Long deviceId;

    @Schema(description = "原始 JSON 内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "发送内容不能为空")
    private String payload;

}

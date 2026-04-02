package cn.iocoder.yudao.module.iot.controller.admin.device.vo.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - IoT 设备报文字段映射新增/修改 Request VO")
@Data
public class IotDevicePayloadMappingSaveReqVO {

    @Schema(description = "主键", example = "1")
    private Long id;

    @Schema(description = "设备编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "设备编号不能为空")
    private Long deviceId;

    @Schema(description = "报文字段键", requiredMode = Schema.RequiredMode.REQUIRED, example = "U3D1")
    @NotBlank(message = "报文字段键不能为空")
    private String channelKey;

    @Schema(description = "物模型标识符", requiredMode = Schema.RequiredMode.REQUIRED, example = "temperature")
    @NotBlank(message = "物模型标识符不能为空")
    private String thingModelIdentifier;

    @Schema(description = "换算公式", example = "x * 0.1 - 50")
    private String formula;

    @Schema(description = "归零偏移量", example = "0.15")
    private BigDecimal zeroOffset;

    @Schema(description = "映射方向：0上行 1下行 2双向", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "映射方向不能为空")
    private Integer direction;

    @Schema(description = "是否启用", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "启用状态不能为空")
    private Boolean enabled;

}

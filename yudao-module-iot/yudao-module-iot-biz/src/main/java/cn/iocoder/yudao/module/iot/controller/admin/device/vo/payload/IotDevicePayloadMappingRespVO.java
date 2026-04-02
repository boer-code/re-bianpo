package cn.iocoder.yudao.module.iot.controller.admin.device.vo.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - IoT 设备报文字段映射 Response VO")
@Data
public class IotDevicePayloadMappingRespVO {

    @Schema(description = "主键", example = "1")
    private Long id;
    @Schema(description = "设备编号", example = "1024")
    private Long deviceId;
    @Schema(description = "报文字段键", example = "U3D1")
    private String channelKey;
    @Schema(description = "物模型标识符", example = "temperature")
    private String thingModelIdentifier;
    @Schema(description = "换算公式", example = "x * 0.1")
    private String formula;
    @Schema(description = "归零偏移量", example = "0.15")
    private BigDecimal zeroOffset;
    @Schema(description = "映射方向", example = "2")
    private Integer direction;
    @Schema(description = "是否启用", example = "true")
    private Boolean enabled;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}

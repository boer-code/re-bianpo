package cn.iocoder.yudao.module.iot.controller.admin.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IoT 大屏设备状态记录 Response VO")
@Data
public class IotStatisticsDeviceStateRecordRespVO {

    @Schema(description = "设备名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "device001")
    private String deviceName;

    @Schema(description = "设备备注名称", example = "1号温度传感器")
    private String nickname;

    @Schema(description = "大屏兼容字段：在线状态，1 在线、0 离线", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer onlineState;

    @Schema(description = "状态记录时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "站点名称", example = "站点A")
    private String siteName;
}

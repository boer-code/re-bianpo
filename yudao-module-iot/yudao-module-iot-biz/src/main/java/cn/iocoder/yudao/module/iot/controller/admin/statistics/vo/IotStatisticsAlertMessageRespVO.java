package cn.iocoder.yudao.module.iot.controller.admin.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IoT 大屏告警消息 Response VO")
@Data
public class IotStatisticsAlertMessageRespVO {

    @Schema(description = "告警名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "水位超限")
    private String alertName;

    @Schema(description = "告警级别", example = "2")
    private Integer alertLevel;

    @Schema(description = "设备名称", example = "raw_00001_00006")
    private String deviceName;

    @Schema(description = "设备备注名称", example = "坡脚受力区")
    private String nickname;

    @Schema(description = "站点名称", example = "武夷山站点")
    private String siteName;

    @Schema(description = "告警时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}

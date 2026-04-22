package cn.iocoder.yudao.module.iot.controller.admin.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IoT 大屏告警消息 Response VO")
@Data
public class IotStatisticsAlertMessageRespVO {

    @Schema(description = "告警记录编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "告警名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "水位超限")
    private String alertName;

    @Schema(description = "告警内容", example = "水位超限")
    private String alertDetail;

    @Schema(description = "告警级别", example = "2")
    private Integer alertLevel;

    @Schema(description = "告警值", example = "1")
    private Object alertValue;

    @Schema(description = "是否处理", example = "false")
    private Boolean processStatus;

    @Schema(description = "设备编号", example = "177")
    private Long deviceId;

    @Schema(description = "设备名称", example = "raw_00001_00006")
    private String deviceName;

    @Schema(description = "设备备注名称", example = "坡脚受力区")
    private String nickname;

    @Schema(description = "站点名称", example = "武夷山站点")
    private String siteName;

    @Schema(description = "站点地址", example = "武夷山站点")
    private String address;

    @Schema(description = "告警时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}

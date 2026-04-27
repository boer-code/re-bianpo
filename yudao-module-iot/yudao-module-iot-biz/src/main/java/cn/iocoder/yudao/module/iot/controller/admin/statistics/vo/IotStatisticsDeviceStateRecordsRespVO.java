package cn.iocoder.yudao.module.iot.controller.admin.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - IoT 大屏设备状态记录列表 Response VO")
@Data
public class IotStatisticsDeviceStateRecordsRespVO {

    @Schema(description = "符合时间范围的上线事件总数", example = "16")
    private Long totalOnline;

    @Schema(description = "符合时间范围的离线事件总数", example = "17")
    private Long totalOffline;

    @Schema(description = "设备上下线状态明细列表（按时间倒序，受 limitNum 限制）")
    private List<IotStatisticsDeviceStateRecordRespVO> list;

}

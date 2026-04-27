package cn.iocoder.yudao.module.iot.controller.admin.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - IoT 大屏告警消息列表 Response VO")
@Data
public class IotStatisticsAlertMessagesRespVO {

    @Schema(description = "符合时间范围的告警总数", example = "26")
    private Long total;

    @Schema(description = "告警消息明细列表（按时间倒序，受 limitNum 限制）")
    private List<IotStatisticsAlertMessageRespVO> list;

}

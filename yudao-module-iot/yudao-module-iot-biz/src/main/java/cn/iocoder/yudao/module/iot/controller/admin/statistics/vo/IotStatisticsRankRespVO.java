package cn.iocoder.yudao.module.iot.controller.admin.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - IoT 排名统计 Response VO")
@Data
public class IotStatisticsRankRespVO {

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "武夷山站点")
    private String name;

    @Schema(description = "数值", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Long value;

}

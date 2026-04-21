package cn.iocoder.yudao.module.iot.controller.admin.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - IoT 大屏设备状态记录列表 Response VO")
@Data
public class IotStatisticsDeviceStateRecordsRespVO {

    @Schema(description = "设备上下线状态记录")
    private List<IotStatisticsDeviceStateRecordRespVO> list;

}

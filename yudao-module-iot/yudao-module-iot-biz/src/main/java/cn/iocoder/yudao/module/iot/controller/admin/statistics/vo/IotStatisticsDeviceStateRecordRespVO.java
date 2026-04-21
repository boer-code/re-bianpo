package cn.iocoder.yudao.module.iot.controller.admin.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - IoT 大屏设备状态记录 Response VO")
@Data
public class IotStatisticsDeviceStateRecordRespVO {

    @Schema(description = "记录编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "设备编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "177")
    private Long deviceId;

    @Schema(description = "设备名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "device001")
    private String deviceName;

    @Schema(description = "设备备注名称", example = "1号温度传感器")
    private String nickname;

    @Schema(description = "设备序列号", example = "SN001")
    private String serialNumber;

    @Schema(description = "大屏兼容字段：设备标识", example = "SN001")
    private String gatewayno;

    @Schema(description = "产品标识", example = "pk001")
    private String productKey;

    @Schema(description = "设备状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer state;

    @Schema(description = "大屏兼容字段：在线状态，1 在线、0 离线", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer onlineState;

    @Schema(description = "状态记录时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "站点编号", example = "1")
    private Long siteId;

    @Schema(description = "站点名称", example = "站点A")
    private String siteName;

    @Schema(description = "站点地址", example = "福建省/南平市/武夷山市")
    private String address;

    @Schema(description = "大屏兼容字段：省份", example = "福建省")
    private String provinceName;

    @Schema(description = "大屏兼容字段：城市", example = "南平市")
    private String cityName;

    @Schema(description = "大屏兼容字段：区县", example = "武夷山市")
    private String countyName;

    @Schema(description = "设备经度", example = "116.397428")
    private BigDecimal longitude;

    @Schema(description = "设备纬度", example = "39.90923")
    private BigDecimal latitude;

    @Schema(description = "设备海拔（米）", example = "56.30")
    private BigDecimal altitude;

}

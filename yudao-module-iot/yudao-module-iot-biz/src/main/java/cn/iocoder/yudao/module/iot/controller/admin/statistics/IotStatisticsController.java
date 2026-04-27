package cn.iocoder.yudao.module.iot.controller.admin.statistics;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.date.LocalDateTimeUtils;
import cn.iocoder.yudao.module.iot.controller.admin.statistics.vo.IotStatisticsAlertMessageRespVO;
import cn.iocoder.yudao.module.iot.controller.admin.statistics.vo.IotStatisticsAlertMessagesRespVO;
import cn.iocoder.yudao.module.iot.controller.admin.statistics.vo.IotStatisticsDeviceMessageReqVO;
import cn.iocoder.yudao.module.iot.controller.admin.statistics.vo.IotStatisticsDeviceMessageSummaryByDateRespVO;
import cn.iocoder.yudao.module.iot.controller.admin.statistics.vo.IotStatisticsDeviceStateRecordRespVO;
import cn.iocoder.yudao.module.iot.controller.admin.statistics.vo.IotStatisticsDeviceStateRecordsRespVO;
import cn.iocoder.yudao.module.iot.controller.admin.statistics.vo.IotStatisticsRankRespVO;
import cn.iocoder.yudao.module.iot.controller.admin.statistics.vo.IotStatisticsSummaryRespVO;
import cn.iocoder.yudao.module.iot.core.enums.device.IotDeviceStateEnum;
import cn.iocoder.yudao.module.iot.dal.dataobject.alert.IotAlertRecordDO;
import cn.iocoder.yudao.module.iot.dal.dataobject.device.IotDeviceDO;
import cn.iocoder.yudao.module.iot.dal.dataobject.device.IotDeviceGroupDO;
import cn.iocoder.yudao.module.iot.dal.dataobject.device.IotDeviceOnlineRecordDO;
import cn.iocoder.yudao.module.iot.dal.mysql.alert.IotAlertRecordMapper;
import cn.iocoder.yudao.module.iot.dal.mysql.device.IotDeviceMapper;
import cn.iocoder.yudao.module.iot.dal.mysql.device.IotDeviceOnlineRecordMapper;
import cn.iocoder.yudao.module.iot.service.alert.IotAlertRecordService;
import cn.iocoder.yudao.module.iot.service.device.IotDeviceGroupService;
import cn.iocoder.yudao.module.iot.service.device.IotDeviceService;
import cn.iocoder.yudao.module.iot.service.device.message.IotDeviceMessageService;
import cn.iocoder.yudao.module.iot.service.product.IotProductCategoryService;
import cn.iocoder.yudao.module.iot.service.product.IotProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;

@Tag(name = "管理后台 - IoT 数据统计")
@RestController
@RequestMapping("/iot/statistics")
@Validated
public class IotStatisticsController {

    private static final int DEFAULT_DEVICE_STATE_RECORD_LIMIT = 20;
    private static final int MAX_DEVICE_STATE_RECORD_LIMIT = 100;
    private static final int DEFAULT_ALERT_RANKING_LIMIT = 8;
    private static final int MAX_ALERT_RANKING_LIMIT = 50;
    private static final int DEFAULT_ALERT_MESSAGE_LIMIT = 20;
    private static final int MAX_ALERT_MESSAGE_LIMIT = 100;

    @Resource
    private IotDeviceService deviceService;
    @Resource
    private IotDeviceMapper deviceMapper;
    @Resource
    private IotDeviceGroupService deviceGroupService;
    @Resource
    private IotDeviceOnlineRecordMapper deviceOnlineRecordMapper;
    @Resource
    private IotAlertRecordMapper alertRecordMapper;
    @Resource
    private IotProductCategoryService productCategoryService;
    @Resource
    private IotProductService productService;
    @Resource
    private IotDeviceMessageService deviceMessageService;
    @Resource
    private IotAlertRecordService alertRecordService;

    @GetMapping("/get-summary")
    @Operation(summary = "获取全局的数据统计")
    @PermitAll
    public CommonResult<IotStatisticsSummaryRespVO> getStatisticsSummary(){
        IotStatisticsSummaryRespVO respVO = new IotStatisticsSummaryRespVO();
        // 1.1 获取总数
        respVO.setProductCategoryCount(productCategoryService.getProductCategoryCount(null));
        respVO.setProductCount(productService.getProductCount(null));
        respVO.setDeviceCount(deviceService.getDeviceCount(null));
        respVO.setDeviceMessageCount(deviceMessageService.getDeviceMessageCount(null));
        // 1.2 获取今日新增数量
        LocalDateTime todayStart = LocalDateTimeUtils.getToday();
        respVO.setProductCategoryTodayCount(productCategoryService.getProductCategoryCount(todayStart));
        respVO.setProductTodayCount(productService.getProductCount(todayStart));
        respVO.setDeviceTodayCount(deviceService.getDeviceCount(todayStart));
        respVO.setDeviceMessageTodayCount(deviceMessageService.getDeviceMessageCount(todayStart));

        // 2. 获取各个品类下设备数量统计
        respVO.setProductCategoryDeviceCounts(productCategoryService.getProductCategoryDeviceCountMap());

        // 3. 获取设备状态数量统计
        Map<Integer, Long> deviceCountMap = deviceService.getDeviceCountMapByState();
        respVO.setDeviceOnlineCount(deviceCountMap.getOrDefault(IotDeviceStateEnum.ONLINE.getState(), 0L));
        respVO.setDeviceOfflineCount(deviceCountMap.getOrDefault(IotDeviceStateEnum.OFFLINE.getState(), 0L));
        respVO.setDeviceInactiveCount(deviceCountMap.getOrDefault(IotDeviceStateEnum.INACTIVE.getState(), 0L));
        respVO.setAlertRecordCount(alertRecordService.getAlertRecordCount(null));
        return success(respVO);
    }

    @GetMapping("/get-device-message-summary-by-date")
    @Operation(summary = "获取设备消息的数据统计")
    @PermitAll
    public CommonResult<List<IotStatisticsDeviceMessageSummaryByDateRespVO>> getDeviceMessageSummaryByDate(
            @Valid IotStatisticsDeviceMessageReqVO reqVO) {
        return success(deviceMessageService.getDeviceMessageSummaryByDate(reqVO));
    }

    @GetMapping("/alert-ranking-by-device-group")
    @Operation(summary = "获取站点告警排名", description = "匿名接口，按设备所属站点聚合告警次数并返回 Top 排名")
    @Parameter(name = "limitNum", description = "返回条数，默认 8，最大 50", example = "8")
    @PermitAll
    public CommonResult<List<IotStatisticsRankRespVO>> getAlertRankingByDeviceGroup(
            @RequestParam(value = "limitNum", required = false) Integer limitNum) {
        Integer limit = normalizeAlertRankingLimit(limitNum);
        List<Map<String, Object>> deviceAlertCounts = alertRecordMapper.selectAlertCountGroupByDeviceId();
        if (CollUtil.isEmpty(deviceAlertCounts)) {
            return success(Collections.emptyList());
        }

        Set<Long> deviceIds = convertSet(deviceAlertCounts, row -> MapUtil.getLong(row, "deviceId"));
        Map<Long, IotDeviceDO> deviceMap = convertMap(deviceMapper.selectBatchIds(deviceIds), IotDeviceDO::getId);
        Map<Long, IotDeviceGroupDO> groupMap = convertMap(
                deviceGroupService.getDeviceGroupListByStatus(CommonStatusEnum.ENABLE.getStatus()),
                IotDeviceGroupDO::getId);

        Map<Long, Long> groupAlertCountMap = new HashMap<>();
        for (Map<String, Object> row : deviceAlertCounts) {
            Long deviceId = MapUtil.getLong(row, "deviceId");
            Long alertCount = MapUtil.getLong(row, "alertCount", 0L);
            IotDeviceDO device = deviceMap.get(deviceId);
            if (device == null || CollUtil.isEmpty(device.getGroupIds())) {
                continue;
            }
            for (Long groupId : device.getGroupIds()) {
                if (groupMap.containsKey(groupId)) {
                    groupAlertCountMap.merge(groupId, alertCount, Long::sum);
                }
            }
        }

        List<IotStatisticsRankRespVO> result = new ArrayList<>();
        groupAlertCountMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue(Comparator.reverseOrder()))
                .limit(limit)
                .forEach(entry -> {
                    IotDeviceGroupDO group = groupMap.get(entry.getKey());
                    result.add(new IotStatisticsRankRespVO()
                            .setName(group.getName())
                            .setValue(entry.getValue()));
                });
        return success(result);
    }

    @GetMapping("/alert-messages")
    @Operation(summary = "获取大屏告警消息列表",
            description = "匿名接口，支持按时间范围筛选并返回告警总数；list 为按时间倒序截取的明细列表，适合大屏时间线展示")
    @Parameter(name = "limitNum", description = "返回明细条数，默认 20，最大 100；不影响 total 统计总数", example = "10")
    @Parameter(name = "startTime", description = "筛选开始时间，格式 yyyy-MM-dd HH:mm:ss", example = "2026-03-29 00:00:00")
    @Parameter(name = "endTime", description = "筛选结束时间，格式 yyyy-MM-dd HH:mm:ss", example = "2026-04-27 23:59:59")
    @PermitAll
    public CommonResult<IotStatisticsAlertMessagesRespVO> getAlertMessages(
            @RequestParam(value = "limitNum", required = false) Integer limitNum,
            @RequestParam(value = "startTime", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(value = "endTime", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        Integer limit = normalizeAlertMessageLimit(limitNum);
        LocalDateTime[] timeRange = normalizeTimeRange(startTime, endTime);
        List<IotAlertRecordDO> records = alertRecordMapper.selectRecentList(limit, timeRange[0], timeRange[1]);

        IotStatisticsAlertMessagesRespVO respVO = new IotStatisticsAlertMessagesRespVO();
        respVO.setTotal(alertRecordMapper.selectCountByCreateTimeRange(timeRange[0], timeRange[1]));
        if (CollUtil.isEmpty(records)) {
            respVO.setList(Collections.emptyList());
            return success(respVO);
        }

        Set<Long> deviceIds = records.stream()
                .map(IotAlertRecordDO::getDeviceId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, IotDeviceDO> deviceMap = convertMap(deviceMapper.selectBatchIds(deviceIds), IotDeviceDO::getId);
        Map<Long, IotDeviceGroupDO> groupMap = convertMap(
                deviceGroupService.getDeviceGroupListByStatus(CommonStatusEnum.ENABLE.getStatus()),
                IotDeviceGroupDO::getId);
        respVO.setList(convertList(records, record -> buildAlertMessageRespVO(
                record, deviceMap.get(record.getDeviceId()), groupMap)));
        return success(respVO);
    }

    @GetMapping("/device-state-records")
    @Operation(summary = "获取大屏设备上下线状态记录",
            description = "匿名接口，支持按时间范围筛选并返回上线总数、离线总数；list 为按时间倒序截取的明细列表，适合大屏时间线展示")
    @Parameter(name = "limitNum", description = "返回明细条数，默认 20，最大 100；不影响 totalOnline/totalOffline 统计总数", example = "5")
    @Parameter(name = "startTime", description = "筛选开始时间，格式 yyyy-MM-dd HH:mm:ss", example = "2026-03-29 00:00:00")
    @Parameter(name = "endTime", description = "筛选结束时间，格式 yyyy-MM-dd HH:mm:ss", example = "2026-04-27 23:59:59")
    @PermitAll
    public CommonResult<IotStatisticsDeviceStateRecordsRespVO> getDeviceStateRecords(
            @RequestParam(value = "limitNum", required = false) Integer limitNum,
            @RequestParam(value = "startTime", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(value = "endTime", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        Integer limit = normalizeDeviceStateRecordLimit(limitNum);
        LocalDateTime[] timeRange = normalizeTimeRange(startTime, endTime);
        List<IotDeviceOnlineRecordDO> records = deviceOnlineRecordMapper.selectRecentList(limit, timeRange[0], timeRange[1]);

        IotStatisticsDeviceStateRecordsRespVO respVO = new IotStatisticsDeviceStateRecordsRespVO();
        respVO.setTotalOnline(deviceOnlineRecordMapper.selectCountByStateAndCreateTimeRange(
                IotDeviceStateEnum.ONLINE.getState(), timeRange[0], timeRange[1]));
        respVO.setTotalOffline(deviceOnlineRecordMapper.selectCountByStateAndCreateTimeRange(
                IotDeviceStateEnum.OFFLINE.getState(), timeRange[0], timeRange[1]));
        if (CollUtil.isEmpty(records)) {
            respVO.setList(Collections.emptyList());
            return success(respVO);
        }

        Set<Long> deviceIds = convertSet(records, IotDeviceOnlineRecordDO::getDeviceId);
        Map<Long, IotDeviceDO> deviceMap = convertMap(deviceMapper.selectBatchIds(deviceIds), IotDeviceDO::getId);
        Map<Long, IotDeviceGroupDO> groupMap = convertMap(
                deviceGroupService.getDeviceGroupListByStatus(CommonStatusEnum.ENABLE.getStatus()),
                IotDeviceGroupDO::getId);

        respVO.setList(convertList(records, record -> buildDeviceStateRecordRespVO(
                record, deviceMap.get(record.getDeviceId()), groupMap)));
        return success(respVO);
    }

    private Integer normalizeDeviceStateRecordLimit(Integer limitNum) {
        if (limitNum == null) {
            return DEFAULT_DEVICE_STATE_RECORD_LIMIT;
        }
        return Math.min(Math.max(limitNum, 1), MAX_DEVICE_STATE_RECORD_LIMIT);
    }

    private Integer normalizeAlertRankingLimit(Integer limitNum) {
        if (limitNum == null) {
            return DEFAULT_ALERT_RANKING_LIMIT;
        }
        return Math.min(Math.max(limitNum, 1), MAX_ALERT_RANKING_LIMIT);
    }

    private Integer normalizeAlertMessageLimit(Integer limitNum) {
        if (limitNum == null) {
            return DEFAULT_ALERT_MESSAGE_LIMIT;
        }
        return Math.min(Math.max(limitNum, 1), MAX_ALERT_MESSAGE_LIMIT);
    }

    private LocalDateTime[] normalizeTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            return new LocalDateTime[]{endTime, startTime};
        }
        return new LocalDateTime[]{startTime, endTime};
    }

    private IotStatisticsAlertMessageRespVO buildAlertMessageRespVO(IotAlertRecordDO record, IotDeviceDO device,
                                                                    Map<Long, IotDeviceGroupDO> groupMap) {
        IotDeviceGroupDO site = findFirstSite(device, groupMap);
        IotStatisticsAlertMessageRespVO respVO = new IotStatisticsAlertMessageRespVO();
        respVO.setAlertName(record.getConfigName());
        respVO.setAlertLevel(record.getConfigLevel());
        respVO.setCreateTime(record.getCreateTime());

        if (device != null) {
            respVO.setDeviceName(device.getDeviceName());
            respVO.setNickname(device.getNickname());
        }
        if (site != null) {
            respVO.setSiteName(site.getName());
        }
        return respVO;
    }

    private IotStatisticsDeviceStateRecordRespVO buildDeviceStateRecordRespVO(IotDeviceOnlineRecordDO record,
                                                                              IotDeviceDO device,
                                                                              Map<Long, IotDeviceGroupDO> groupMap) {
        IotDeviceGroupDO site = findFirstSite(device, groupMap);
        IotStatisticsDeviceStateRecordRespVO respVO = new IotStatisticsDeviceStateRecordRespVO();
        respVO.setDeviceName(record.getDeviceName());
        respVO.setOnlineState(Objects.equals(record.getState(), IotDeviceStateEnum.ONLINE.getState()) ? 1 : 0);
        respVO.setCreateTime(record.getCreateTime());

        if (device != null) {
            respVO.setDeviceName(device.getDeviceName());
            respVO.setNickname(device.getNickname());
        }
        if (site != null) {
            respVO.setSiteName(site.getName());
        }
        return respVO;
    }

    private IotDeviceGroupDO findFirstSite(IotDeviceDO device, Map<Long, IotDeviceGroupDO> groupMap) {
        if (device == null || CollUtil.isEmpty(device.getGroupIds())) {
            return null;
        }
        return device.getGroupIds().stream()
                .map(groupMap::get)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

}

package cn.iocoder.yudao.module.iot.controller.admin.statistics;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.date.LocalDateTimeUtils;
import cn.iocoder.yudao.module.iot.controller.admin.statistics.vo.IotStatisticsDeviceMessageReqVO;
import cn.iocoder.yudao.module.iot.controller.admin.statistics.vo.IotStatisticsDeviceMessageSummaryByDateRespVO;
import cn.iocoder.yudao.module.iot.controller.admin.statistics.vo.IotStatisticsDeviceStateRecordRespVO;
import cn.iocoder.yudao.module.iot.controller.admin.statistics.vo.IotStatisticsDeviceStateRecordsRespVO;
import cn.iocoder.yudao.module.iot.controller.admin.statistics.vo.IotStatisticsSummaryRespVO;
import cn.iocoder.yudao.module.iot.core.enums.device.IotDeviceStateEnum;
import cn.iocoder.yudao.module.iot.dal.dataobject.device.IotDeviceDO;
import cn.iocoder.yudao.module.iot.dal.dataobject.device.IotDeviceGroupDO;
import cn.iocoder.yudao.module.iot.dal.dataobject.device.IotDeviceOnlineRecordDO;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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

    @Resource
    private IotDeviceService deviceService;
    @Resource
    private IotDeviceMapper deviceMapper;
    @Resource
    private IotDeviceGroupService deviceGroupService;
    @Resource
    private IotDeviceOnlineRecordMapper deviceOnlineRecordMapper;
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
    public CommonResult<List<IotStatisticsDeviceMessageSummaryByDateRespVO>> getDeviceMessageSummaryByDate(
            @Valid IotStatisticsDeviceMessageReqVO reqVO) {
        return success(deviceMessageService.getDeviceMessageSummaryByDate(reqVO));
    }

    @GetMapping("/device-state-records")
    @Operation(summary = "获取大屏设备上下线状态记录", description = "匿名接口，返回最近设备上下线记录及设备所属站点、位置等大屏展示字段")
    @Parameter(name = "limitNum", description = "返回条数，默认 20，最大 100", example = "20")
    @PermitAll
    public CommonResult<IotStatisticsDeviceStateRecordsRespVO> getDeviceStateRecords(
            @RequestParam(value = "limitNum", required = false) Integer limitNum) {
        Integer limit = normalizeDeviceStateRecordLimit(limitNum);
        List<IotDeviceOnlineRecordDO> records = deviceOnlineRecordMapper.selectRecentList(limit);

        IotStatisticsDeviceStateRecordsRespVO respVO = new IotStatisticsDeviceStateRecordsRespVO();
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

    private IotStatisticsDeviceStateRecordRespVO buildDeviceStateRecordRespVO(IotDeviceOnlineRecordDO record,
                                                                              IotDeviceDO device,
                                                                              Map<Long, IotDeviceGroupDO> groupMap) {
        IotDeviceGroupDO site = findFirstSite(device, groupMap);
        IotStatisticsDeviceStateRecordRespVO respVO = new IotStatisticsDeviceStateRecordRespVO();
        respVO.setId(record.getId());
        respVO.setDeviceId(record.getDeviceId());
        respVO.setProductKey(record.getProductKey());
        respVO.setDeviceName(record.getDeviceName());
        respVO.setGatewayno(record.getDeviceName());
        respVO.setState(record.getState());
        respVO.setOnlineState(Objects.equals(record.getState(), IotDeviceStateEnum.ONLINE.getState()) ? 1 : 0);
        respVO.setCreateTime(record.getCreateTime());

        if (device != null) {
            respVO.setDeviceName(device.getDeviceName());
            respVO.setNickname(device.getNickname());
            respVO.setSerialNumber(device.getSerialNumber());
            respVO.setGatewayno(StrUtil.blankToDefault(device.getSerialNumber(), device.getDeviceName()));
            respVO.setLongitude(device.getLongitude());
            respVO.setLatitude(device.getLatitude());
            respVO.setAltitude(device.getAltitude());
        }
        if (site != null) {
            respVO.setSiteId(site.getId());
            respVO.setSiteName(site.getName());
            setSiteAddressFields(respVO, site);
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

    private void setSiteAddressFields(IotStatisticsDeviceStateRecordRespVO respVO, IotDeviceGroupDO site) {
        respVO.setAddress(site.getName());
    }

}

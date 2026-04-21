package cn.iocoder.yudao.module.iot.service.alert;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.iot.controller.admin.alert.vo.recrod.IotAlertRecordPageReqVO;
import cn.iocoder.yudao.module.iot.core.mq.message.IotDeviceMessage;
import cn.iocoder.yudao.module.iot.dal.dataobject.alert.IotAlertConfigDO;
import cn.iocoder.yudao.module.iot.dal.dataobject.alert.IotAlertRecordDO;
import jakarta.validation.constraints.NotNull;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.time.LocalDateTime;

/**
 * IoT 告警记录 Service 接口
 *
 * @author 芋道源码
 */
public interface IotAlertRecordService {

    /**
     * 获得告警记录
     *
     * @param id 编号
     * @return 告警记录
     */
    IotAlertRecordDO getAlertRecord(Long id);

    /**
     * 获得告警记录分页
     *
     * @param pageReqVO 分页查询
     * @return 告警记录分页
     */
    PageResult<IotAlertRecordDO> getAlertRecordPage(IotAlertRecordPageReqVO pageReqVO);

    /**
     * 获得告警记录数量
     *
     * @param createTime 创建时间，如果为空，则统计所有告警记录数量
     * @return 告警记录数量
     */
    Long getAlertRecordCount(@Nullable LocalDateTime createTime);

    /**
     * 获得指定场景规则的告警记录列表
     *
     * @param sceneRuleId 场景规则编号
     * @param deviceId 设备编号
     * @param processStatus 处理状态，允许空
     * @return 告警记录列表
     */
    List<IotAlertRecordDO> getAlertRecordListBySceneRuleId(@NotNull(message = "场景规则编号不能为空") Long sceneRuleId,
                                                           Long deviceId, Boolean processStatus);

    /**
     * 处理告警记录
     *
     * @param ids 告警记录编号
     * @param remark 处理结果（备注）
     */
    void processAlertRecordList(Collection<Long> ids, String remark);

    /**
     * 创建告警记录（包含场景规则编号）
     *
     * @param config 告警配置
     * @param sceneRuleId 场景规则编号
     * @param deviceMessage 设备消息，可为空
     * @return 告警记录编号
     */
    Long createAlertRecord(IotAlertConfigDO config, Long sceneRuleId, IotDeviceMessage deviceMessage);

}
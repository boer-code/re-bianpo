-- IoT 设备上下线记录表
CREATE TABLE IF NOT EXISTS `iot_device_online_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `product_key` varchar(64) NOT NULL DEFAULT '' COMMENT '产品标识',
  `device_id` bigint NOT NULL COMMENT '设备ID',
  `device_name` varchar(64) NOT NULL DEFAULT '' COMMENT '设备名称',
  `state` tinyint NOT NULL COMMENT '状态 1在线 2离线',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_device_time` (`device_id`, `event_time`),
  KEY `idx_product_state_time` (`product_id`, `state`, `event_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='IoT 设备上下线记录表';
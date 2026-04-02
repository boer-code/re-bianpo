-- IoT 设备报文字段映射表
CREATE TABLE IF NOT EXISTS `iot_device_payload_mapping` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `device_id` bigint NOT NULL COMMENT '设备ID',
  `channel_key` varchar(64) NOT NULL DEFAULT '' COMMENT '报文字段键，例如 U3D1',
  `thing_model_identifier` varchar(64) NOT NULL DEFAULT '' COMMENT '物模型标识符',
  `formula` varchar(255) DEFAULT NULL COMMENT '换算公式，变量名 x',
  `zero_offset` decimal(24,8) DEFAULT NULL COMMENT '归零偏移量',
  `direction` tinyint NOT NULL DEFAULT '2' COMMENT '映射方向 0上行 1下行 2双向',
  `enabled` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否启用',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '逻辑删除',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_device_channel_deleted` (`device_id`,`channel_key`,`deleted`),
  KEY `idx_device_id` (`device_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='IoT 设备报文字段映射表';

-- IoT 设备快捷指令表
CREATE TABLE IF NOT EXISTS `iot_device_command` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `device_id` bigint NOT NULL COMMENT '设备编号',
  `name` varchar(100) NOT NULL COMMENT '指令名称',
  `template` text NOT NULL COMMENT 'JSON 模板内容',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '逻辑删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_device_id` (`device_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='IoT 设备快捷指令';

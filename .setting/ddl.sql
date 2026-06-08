# 2026-03-27 08:56:42
CREATE TABLE IF NOT EXISTS iot_device_payload_mapping (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  device_id BIGINT NOT NULL COMMENT '设备ID',
  channel_key VARCHAR(64) NOT NULL DEFAULT '' COMMENT '报文字段键，例如 U3D1',
  thing_model_identifier VARCHAR(64) NOT NULL DEFAULT '' COMMENT '物模型标识符',
  cl_bit_index TINYINT DEFAULT NULL COMMENT 'CL 位索引（1-7）',
  formula VARCHAR(255) DEFAULT NULL COMMENT '换算公式，变量名 x',
  zero_offset DECIMAL(24,8) DEFAULT NULL COMMENT '归零偏移量',
  direction TINYINT NOT NULL DEFAULT 2 COMMENT '映射方向 0上行 1下行 2双向',
  enabled BIT(1) NOT NULL DEFAULT b'1' COMMENT '是否启用',
  creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updater VARCHAR(64) DEFAULT '' COMMENT '更新者',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted BIT(1) NOT NULL DEFAULT b'0' COMMENT '逻辑删除',
  tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (id),
  UNIQUE KEY uk_device_channel_deleted (device_id, channel_key, deleted),
  KEY idx_device_id (device_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='IoT 设备报文字段映射表';
# 2026-03-31 06:56:43
ALTER TABLE iot_device_payload_mapping DROP COLUMN cl_bit_index;
# 2026-04-13 02:24:53
ALTER TABLE iot_device ADD COLUMN altitude DECIMAL(10,2) NULL COMMENT '海拔（米）' AFTER longitude;
# 2026-04-13 03:29:27
ALTER TABLE `iot_device_group` ADD COLUMN `region_id` BIGINT NULL COMMENT '所在地区编号', ADD COLUMN `longitude` DECIMAL(10, 6) NULL COMMENT '站点经度', ADD COLUMN `latitude` DECIMAL(10, 6) NULL COMMENT '站点纬度', ADD COLUMN `altitude` DECIMAL(10, 2) NULL COMMENT '站点海拔（米）';

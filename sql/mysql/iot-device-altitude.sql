-- 为 iot_device 增加海拔字段
ALTER TABLE `iot_device`
ADD COLUMN `altitude` DECIMAL(10,2) NULL COMMENT '海拔（米）' AFTER `longitude`;

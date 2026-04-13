ALTER TABLE `iot_device_group`
    ADD COLUMN `region_id` BIGINT NULL COMMENT '所在地区编号',
    ADD COLUMN `longitude` DECIMAL(10, 6) NULL COMMENT '站点经度',
    ADD COLUMN `latitude` DECIMAL(10, 6) NULL COMMENT '站点纬度',
    ADD COLUMN `altitude` DECIMAL(10, 2) NULL COMMENT '站点海拔（米）';

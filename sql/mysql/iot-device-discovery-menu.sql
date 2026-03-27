-- IoT 新设备发现与初始化菜单（权限由菜单库控制）
-- 说明：
-- 1) 不修改前端静态路由，通过 system_menu 配置组件路径加载页面
-- 2) 页面复用 iot:device:query / iot:device:update 权限，不新增按钮权限

INSERT INTO `system_menu`
(`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`,
 `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT
  4090, '设备发现初始化', 'iot:device:query', 2, 20, 4008, 'discovery', 'ep:search',
  'iot/device/discovery/index', 'IoTDeviceDiscovery',
  0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
WHERE NOT EXISTS (
  SELECT 1 FROM `system_menu` WHERE `parent_id` = 4008 AND `path` = 'discovery' AND `deleted` = b'0'
);

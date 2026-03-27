-- IoT RAW 预设产品（用于统一 RAW 报文自动注册）
INSERT INTO iot_product (
  name, product_key, product_secret, register_enabled, category_id,
  icon, pic_url, description, status, device_type, net_type,
  protocol_type, serialize_type, tenant_id, creator, updater, deleted
)
SELECT
  'RAW预设产品', 'raw_product', 'raw_product_secret_2026', 1, 1,
  NULL, NULL, '用于统一RAW报文自动注册与后续物模型配置', 0, 0, 0,
  'mqtt', 'json', 1, '1', '1', b'0'
WHERE NOT EXISTS (
  SELECT 1 FROM iot_product WHERE product_key = 'raw_product' AND deleted = b'0'
);

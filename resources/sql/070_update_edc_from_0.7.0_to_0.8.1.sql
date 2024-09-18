ALTER TABLE edc_policydefinitions
ADD COLUMN IF NOT EXISTS profiles JSON;
COMMENT ON COLUMN edc_policydefinitions.profiles IS 'Java List<String> serialized as JSON';

ALTER TABLE edc_data_plane
ADD COLUMN IF NOT EXISTS transfer_type_destination VARCHAR;
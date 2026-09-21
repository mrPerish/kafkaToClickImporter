-- ExportChargesRequestBillingData
CREATE TABLE IF NOT EXISTS raw_smev3_export_charges_cc_req
(
    mid String,
    d String,
    s_mn String,
    ct_vvs String,
    ct_eol String,
    ct_originator_id String,
    ct_has_esia_user_info String,
    ct_export_kind String,
    ct_bill_id String,
    ct_payer_id String,
    ct_charge_type String,
    ct_has_payer_inn String,
    ct_payer_id_count String,
    ct_bill_id_count String
)
ENGINE = MergeTree
ORDER BY mid;

-- ExportChargesRequestBillingData
CREATE TABLE IF NOT EXISTS raw_smev3_export_charges_pc_req
(
    mid String,
    d String,
    s_mn String,
    ct_vvs String,
    ct_eol String,
    ct_originator_id String,
    ct_has_esia_user_info String,
    ct_export_kind String,
    ct_bill_id String,
    ct_payer_id String,
    ct_charge_type String,
    ct_has_payer_inn String,
    ct_payer_id_count String,
    ct_bill_id_count String
)
ENGINE = MergeTree
ORDER BY mid;

-- ExportChargesRequestBillingData
CREATE TABLE IF NOT EXISTS raw_smev3_export_charges_tc_req
(
    mid String,
    d String,
    s_mn String,
    ct_vvs String,
    ct_eol String,
    ct_originator_id String,
    ct_has_esia_user_info String,
    ct_export_kind String,
    ct_bill_id String,
    ct_payer_id String,
    ct_charge_type String,
    ct_has_payer_inn String,
    ct_payer_id_count String,
    ct_bill_id_count String
)
ENGINE = MergeTree
ORDER BY mid;

-- ExportChargesResponseBillingData
CREATE TABLE IF NOT EXISTS raw_smev3_export_charges_res
(
    mid String,
    d String,
    s_mn String,
    ct_vvs String,
    ct_to_original_mid String,
    ct_to_sender_mnemonic String,
    ct_has_more String,
    ct_need_re_request String,
    ct_charge_type String,
    ct_charge_count String,
    ct_bill_id String,
    ct_bill_date String,
    ct_charge_offense String,
    ct_charge_ack_status String,
    ct_linked_bill_ids String,
    ct_payee_inn String,
    ct_payee_kpp String,
    ct_charge_kbk String,
    ct_charge_oktmo String,
    ct_charge_amount_to_pay String,
    ct_charge_total_amount String,
    ct_payer_id String,
    ct_additional_payer_id String,
    ct_meaning String
)
ENGINE = MergeTree
ORDER BY mid;

-- ExportNoticeRequestBillingData
CREATE TABLE IF NOT EXISTS raw_smev3_export_notice_req
(
    mid String,
    d String,
    s_mn String,
    ct_vvs String,
    ct_recipient_id String,
    ct_routing_code String,
    ct_notice_type String,
    ct_event_notification String,
    ct_meaning String,
    ct_bill_id String,
    ct_bill_date String,
    ct_charge_offense String,
    ct_ack_status String,
    ct_kbk String,
    ct_oktmo String,
    ct_amount_to_pay String,
    ct_total_amount String,
    ct_payee_inn String,
    ct_payee_kpp String,
    ct_payer_id String,
    ct_additional_payer_id String
)
ENGINE = MergeTree
ORDER BY mid;

-- ExportPaymentsRequestBillingData
CREATE TABLE IF NOT EXISTS raw_smev3_export_payment_details_req
(
    mid String,
    d String,
    s_mn String,
    ct_vvs String,
    ct_eol String,
    ct_originator_id String,
    ct_consumer_type String
)
ENGINE = MergeTree
ORDER BY mid;

-- ExportPaymentsResponseBillingData
CREATE TABLE IF NOT EXISTS raw_smev3_export_payment_details_res
(
    mid String,
    d String,
    s_mn String,
    ct_vvs String,
    ct_to_original_mid String,
    ct_to_sender_mnemonic String,
    ct_response_type String,
    ct_response_guid String,
    ct_reminder String,
    ct_response_count String
)
ENGINE = MergeTree
ORDER BY mid;

-- ImportPaymentsRequestBillingData
CREATE TABLE IF NOT EXISTS raw_smev3_import_payments_req
(
    mid String,
    d String,
    s_mn String,
    ct_vvs String,
    ct_eol String,
    ct_originator_id String,
    ct_import_type String,
    ct_id String,
    ct_import_payment_id String,
    ct_bill_id String,
    ct_payment_id String,
    ct_receipt_date String,
    ct_payment_date String,
    ct_kbk String,
    ct_oktmo String,
    ct_amount String,
    ct_trans_kind String,
    ct_payment_org_bik String,
    ct_payee_bik String,
    ct_import_count String
)
ENGINE = MergeTree
ORDER BY mid;

-- ImportPaymentsResponseBillingData
CREATE TABLE IF NOT EXISTS raw_smev3_import_payments_res
(
    mid String,
    d String,
    s_mn String,
    ct_vvs String,
    ct_to_original_mid String,
    ct_to_sender_mnemonic String,
    ct_id String,
    ct_rq_id String,
    ct_code String,
    ct_entity_id String,
    ct_protocol_count String
)
ENGINE = MergeTree
ORDER BY mid;

-- RequestRejectedBillingData
CREATE TABLE IF NOT EXISTS raw_smev3_non_business_res
(
    mid String,
    d String,
    s_mn String,
    ct_vvs String,
    ct_to_original_mid String,
    ct_to_sender_mnemonic String,
    ct_no_content_type String,
    ct_code String,
    ct_description String
)
ENGINE = MergeTree
ORDER BY mid;

-- DeliveryDocumentsRequestBillingData
CREATE TABLE IF NOT EXISTS raw_smev3_delivery_data_documents_req
(
    mid String,
    d String,
    s_mn String,
    ct_vvs String,
    ct_mid String,
    ct_routing_code String,
    ct_oid String,
    ct_doc_type String,
    ct_updated_at String,
    ct_permission_id String,
    ct_status String,
    ct_info_file_count String
)
ENGINE = MergeTree
ORDER BY mid;

-- PersonalDocumentsRequestBillingData
CREATE TABLE IF NOT EXISTS raw_smev3_personal_data_documents_req
(
    mid String,
    d String,
    s_mn String,
    ct_vvs String,
    ct_mid String,
    ct_registry_count String,
    ct_record_id String,
    ct_data_type String,
    ct_permission_id String,
    ct_person_data_type String,
    ct_person_value String
)
ENGINE = MergeTree
ORDER BY mid;

-- PersonalDocumentsResponseBillingData
CREATE TABLE IF NOT EXISTS raw_smev3_personal_data_documents_res
(
    mid String,
    d String,
    s_mn String,
    ct_vvs String,
    ct_mid String,
    ct_to_original_mid String,
    ct_to_sender_mnemonic String,
    ct_to_cid String,
    ct_to_sid String,
    ct_registry_count String,
    ct_record_id String,
    ct_record_attr_id String,
    ct_status_response String,
    ct_error_code String,
    ct_error_message String,
    ct_data_type String,
    ct_permission_id String,
    ct_oid String,
    ct_request_id String,
    ct_info_file_count String
)
ENGINE = MergeTree
ORDER BY mid;

package ru.rtksoft.smev3.billing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@SuperBuilder
public class ExportChargesRequestBillingData extends BillingData {

    /**
     * Значение EOL запроса
     */
    @JsonProperty("ct_eol")
    String eol;
    /**
     * Идентификатор косвенного взаимодействия
     */
    @JsonProperty("ct_originator_id")
    String originatorId;
    /**
     * Наличие контейнера EsiaUserInfo
     */
    @JsonProperty("ct_has_esia_user_info")
    String hasUserInfo;
    /**
     * Значение атрибута kind - тип запроса на предоставление необходимой для уплаты информации
     */
    @JsonProperty("ct_export_kind")
    String exportKind;
    /**
     * Идентификатор платёжного начисления (УИН)
     */
    @JsonProperty("ct_bill_id")
    String supplierBillId;
    /**
     * PayerIdentifier
     */
    @JsonProperty("ct_payer_id")
    String payerIdentifier;
    /**
     * Подзапрос начисления
     */
    @JsonProperty("ct_charge_type")
    String chargeType;
    /**
     * Признак наличия блоков ChargesExportConditions/PayersConditions/PayerInn
     */
    @JsonProperty("ct_has_payer_inn")
    String hasPayerInn;
    /**
     * Количество блоков ChargesExportConditions/PayersConditions/PayerIdentifier
     */
    @JsonProperty("ct_payer_id_count")
    String payerIdCount;
    /**
     * Количество блоков ChargesExportConditions/ChargesConditions/SupplierBillID
     */
    @JsonProperty("ct_bill_id_count")
    String billIdCount;
}
package ru.rtksoft.smev3.billing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@Accessors(chain = true)
public class ExportChargesResponseBillingData extends BillingData {

    /**
     * Идентификатор оригинального сообщения из блока To
     */
    @JsonProperty("ct_to_original_mid")
    String toOriginalMessageId;
    /**
     * Мнемоника отправителя из блока To
     */
    @JsonProperty("ct_to_sender_mnemonic")
    String toSenderMnemonic;
    /**
     * Признак конца выборки
     */
    @JsonProperty("ct_has_more")
    String hasMore;
    /**
     * Признак необходимости направления повторного запроса.
     */
    @JsonProperty("ct_need_re_request")
    String needReRequest;
    /**
     * Подзапрос начисления
     */
    @JsonProperty("ct_charge_type")
    String chargeType;
    /**
     * Количество блоков ExportChargesResponse/ChargeInfo
     */
    @JsonProperty("ct_charge_count")
    String chargeCount;
    /**
     * Идентификатор платёжного начисления (УИН)
     */
    @JsonProperty("ct_bill_id")
    String supplierBillId;
    /**
     * Дата платежа
     */
    @JsonProperty("ct_bill_date")
    String billDate;
    /**
     * Признак автоматической фиксации факта правонарушения, зафиксированного с применением средств фото (видео) фиксации
     */
    @JsonProperty("ct_charge_offense")
    String chargeOffense;
    /**
     * Статус, присваиваемый начислению при создании квитанции
     */
    @JsonProperty("ct_charge_ack_status")
    String acknowledgmentStatus;
    /**
     * Идентификаторы ExportChargesResponse/ChargeInfo/LinkedChargesIdentifiers/SupplierBillID через запятую
     */
    @JsonProperty("ct_linked_bill_ids")
    String linkedBillIds;
    /**
     * Поле номер 61:
     * ИНН организации
     */
    @JsonProperty("ct_payee_inn")
    String payeeInn;
    /**
     * Поле номер 103:
     * КПП организации
     */
    @JsonProperty("ct_payee_kpp")
    String payeeKpp;
    /**
     * Поле номер 104: КБК
     */
    @JsonProperty("ct_charge_kbk")
    String chargeKbk;
    /**
     * Поле номер 105: Код по ОКТМО, указываемый АН или ГАН в соответствии с НПА
     */
    @JsonProperty("ct_charge_oktmo")
    String chargeOktmo;
    /**
     * Остаток суммы подлежащей оплате, указанной в начислении
     */
    @JsonProperty("ct_charge_amount_to_pay")
    String chargeAmountPoPay;
    /**
     * Поле номер 7: Сумма начисления (в копейках)
     */
    @JsonProperty("ct_charge_total_amount")
    String chargeTotalAmount;
    /**
     * Поле номер 201:
     * Идентификатор плательщика
     */
    @JsonProperty("ct_payer_id")
    String payerIdentifier;
    /**
     * Поле номер 1201: Дополнительный идентификатор плательщика
     */
    @JsonProperty("ct_additional_payer_id")
    String additionalPayerIdentifier;
    /**
     * Статус, отражающий изменение данных
     */
    @JsonProperty("ct_meaning")
    String meaning;
}

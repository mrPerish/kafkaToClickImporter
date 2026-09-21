package ru.rtksoft.smev3.billing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class ImportPaymentsRequestBillingData extends BillingData {


    /**
     *
     */
    @JsonProperty("ct_eol")
    String eol;
    /**
     * Идентификатор косвенного взаимодействия
     */
    @JsonProperty("ct_originator_id")
    String originatorId;

    /**
     * Тип блока с информацией о платеже
     */
    @JsonProperty("ct_import_type")
    String importType;

    /**
     *  Идентификатор запроса
     */
    @JsonProperty("ct_id")
    String paymentRequestId;
    /**
     * Идентификатор платежа в пакете
     */
    @JsonProperty("ct_import_payment_id")
    String importPaymentId;
    /**
     * Идентификатор платёжного начисления (УИН)
     */
    @JsonProperty("ct_bill_id")
    String supplierBillId;
    /**
     * УПНО
     */
    @JsonProperty("ct_payment_id")
    String paymentId;

    /**
     * Дата поступления распоряжения в банк плательщика.
     */
    @JsonProperty("ct_receipt_date")
    String receiptDate;
    /**
     * Дата платежа
     */
    @JsonProperty("ct_payment_date")
    String paymentDate;
    /**
     * Поле номер 104: КБК.
     */
    @JsonProperty("ct_kbk")
    String kbk;
    /**
     * Поле номер 105:
     * Код ОКТМО, указанный в распоряжении о переводе денежных средств.
     */
    @JsonProperty("ct_oktmo")
    String oktmo;

    /**
     * Поле номер 7:
     * Сумма платежа в копейках
     */
    @JsonProperty("ct_amount")
    String amount;
    /**
     * Поле номер 18:
     * Вид операции. Указывается шифр платежного документа.
     */
    @JsonProperty("ct_trans_kind")
    String transKind;
    /**
     *  Поле номер 14 для получателя средств. Поле номер 11 для организации, принявшей платеж.
     *  БИК ТОФК, структурного подразделения кредитной организации или подразделения Банка России, в котором открыт счет
     */
    @JsonProperty("ct_payment_org_bik")
    String orgBik;
    /**
     * Поле номер 14 для получателя средств. Поле номер 11 для организации, принявшей платеж.
     * БИК ТОФК, структурного подразделения кредитной организации или подразделения Банка России, в котором открыт счет
     */
    @JsonProperty("ct_payee_bik")
    String payeeBik;
    /**
     * Количество блоков ImportedPayment
     */
    @JsonProperty("ct_import_count")
    String importCount;
}

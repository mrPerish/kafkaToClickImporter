package ru.rtksoft.smev3.billing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Предоставление уведомлений по подписке
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class ExportNoticeRequestBillingData extends BillingData {

    /**
     * УРН участника получателя
     */
    @JsonProperty("ct_recipient_id")
    String recipientId;
    /**
     * Код маршрутизации участника для предоставления информации по ВС с табличной маршрутизацией
     */
    @JsonProperty("ct_routing_code")
    String routingCode;
    /**
     * Тип уведомлений
     */
    @JsonProperty("ct_notice_type")
    String noticeType;

    /**
     * Код события для направления уведомлений о начислении
     */
    @JsonProperty("ct_event_notification")
    String eventNotification;

    /**
     * Статус, отражающий изменение данных:
     */
    @JsonProperty("ct_meaning")
    String meaning;

    /**
     * УИН
     */
    @JsonProperty("ct_bill_id")
    String billId;

    /**
     * Дата и время начисления суммы денежных средств, подлежащих уплате
     */
    @JsonProperty("ct_bill_date")
    String billDate;

    /**
     * Признак автоматической фиксации факта правонарушения, зафиксированного с применением средств фото (видео) фиксации
     */
    @JsonProperty("ct_charge_offense")
    String chargeOffense;

    /**
     * Статус присвоенный начислению при создании квитанции
     */
    @JsonProperty("ct_ack_status")
    String ackStatus;

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
     * Остаток суммы подлежащей оплате, указанной в начислении
     */
    @JsonProperty("ct_amount_to_pay")
    String amountToPay;

    /**
     * Сумма начисления (в копейках)
     */
    @JsonProperty("ct_total_amount")
    String totalAmount;

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
}

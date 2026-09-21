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
public class ExportPaymentsResponseBillingData extends BillingData {

    /**
     * Идентификатор сообщения запроса
     */
    @JsonProperty("ct_to_original_mid")
    String toOriginalMessageId;

    /**
     * Мнемоника отправителя запроса
     */
    @JsonProperty("ct_to_sender_mnemonic")
    String toSenderMnemonic;

    /**
     * Тип ответа
     */
    @JsonProperty("ct_response_type")
    String responseType;

    /**
     * guid контейнера
     */
    @JsonProperty("ct_response_guid")
    String responseGuid;

    /**
     * Сумма начисления
     */
    @JsonProperty("ct_reminder")
    String reminder;

    /**
     * Количество блоков
     */
    @JsonProperty("ct_response_count")
    String responseCount;

}

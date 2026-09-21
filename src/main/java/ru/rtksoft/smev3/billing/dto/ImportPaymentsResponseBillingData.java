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
public class ImportPaymentsResponseBillingData extends BillingData {

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
     * Идентификатор ответа
     */
    @JsonProperty("ct_id")
    String id;
    /**
     * Идентификатор запроса
     */
    @JsonProperty("ct_rq_id")
    String rqId;
    /**
     * Код результата обработки
     */
    @JsonProperty("ct_code")
    String code;
    /**
     * Идентификатор сущности в пакете
     */
    @JsonProperty("ct_entity_id")
    String entityId;
    /**
     * Количество блоков
     */
    @JsonProperty("ct_protocol_count")
    String protocolCount;


}

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
public class RequestRejectedBillingData extends BillingData {

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


    @JsonProperty("ct_no_content_type")
    String noContentType;

    @JsonProperty("ct_code")
    String code;

    @JsonProperty("ct_description")
    String description;

}

package ru.rtksoft.smev3.billing.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillingData {
    /**
     * MessageId исходного сообщения
     */
    @JsonProperty("mid")
    UUID messageId;
    /**
     * Время поступления конверта в транспорт СМЭВ
     */
    @JsonProperty("d")
    String createdAt;
    /**
     * Мнемоника ИС-отправителя сообщений
     */
    @JsonProperty("s_mn")
    String senderMnemonic;
    /**
     * Пространство имён ВВС
     */
    @JsonProperty("ct_vvs")
    String namespaceUri;

}

package ru.rtksoft.smev3.billing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@SuperBuilder
@Accessors(chain = true)
public class PersonalDocumentsResponseBillingData extends BillingData {

    /**
     * MessageId исходного сообщения
     */
    @JsonProperty("ct_mid")
    String contentMessageId;
    /**
     * Идентификатор исходного запроса (из тега To)
     */
    @JsonProperty("ct_to_original_mid")
    String toOriginalMid;
    /**
     * Мнемоника ИС инициатора обмена (из тега To)
     */
    @JsonProperty("ct_to_sender_mnemonic")
    String toSenderMnemonic;
    /**
     * Идентификатор CID (из тега To)
     */
    @JsonProperty("ct_to_cid")
    String toCid;
    /**
     * Идентификатор SID (из тегаTo)
     */
    @JsonProperty("ct_to_sid")
    Long toSid;
    /**
     * Количество реестровых записей ( кол-во тегов RegistryRecord)
     */
    @JsonProperty("ct_registry_count")
    Integer registryCount;
    /**
     * Идентификатор реестровой записи (из тега RecordId)
     */
    @JsonProperty("ct_record_id")
    String recordId;
    /**
     * Идентификатор записи (атрибут ID в теге Record)
     */
    @JsonProperty("ct_record_attr_id")
    String recordAttrId;
    /**
     * Статус ответа (из тега statusResponse)
     */
    @JsonProperty("ct_status_response")
    String statusResponse;
    /**
     * Код ошибки (из тега errorStatusInfo, если запрос неуспешен)
     */
    @JsonProperty("ct_error_code")
    String errorCode;
    /**
     * Сообщение об ошибке (из тега errorStatusInfo, если запрос неуспешен)
     */
    @JsonProperty("ct_error_message")
    String errorMessage;
    /**
     * Бизнес-содержимое ответа (из тега dataType)
     */
    @JsonProperty("ct_data_type")
    String dataType;
    /**
     * Идентификатор согласия (из тега idPermission)
     */
    @JsonProperty("ct_permission_id")
    String permissionId;
    /**
     * Идентификатор ЕСИА (из тега oid)
     */
    @JsonProperty("ct_oid")
    String oid;
    /**
     * Идентификатор запроса (из тега requestId)
     */
    @JsonProperty("ct_request_id")
    String requestId;
    /**
     * Количество вложенных файлов (из тега infoFile)
     */
    @JsonProperty("ct_info_file_count")
    Integer infoFileCount;


}

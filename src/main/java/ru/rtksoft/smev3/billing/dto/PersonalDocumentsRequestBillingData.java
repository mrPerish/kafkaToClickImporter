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
public class PersonalDocumentsRequestBillingData extends BillingData {

    /**
     * MessageId исходного сообщения
     */
    @JsonProperty("ct_mid")
    String contentMessageId;
    /**
     * Количество реестровых записей (кол-во тегов RegistryRecord)
     */
    @JsonProperty("ct_registry_count")
    Integer registryCount;
    /**
     * Идентификатор реестровой записи (RecordId)
     */
    @JsonProperty("ct_record_id")
    String recordId;
    /**
     * Бизнес-содержимое запроса (из тега dataType)
     */
    @JsonProperty("ct_data_type")
    String dataType;
    /**
     * Идентификатор согласия (из тега idPermission)
     */
    @JsonProperty("ct_permission_id")
    String permissionId;
    /**
     * Тип данных пользователя (personV1 - personV4)
     */
    @JsonProperty("ct_person_data_type")
    String personDataType;
    /**
     * Значение данных пользователя (для типов personV1 - personV3)
     */
    @JsonProperty("ct_person_value")
    String personValue;

}

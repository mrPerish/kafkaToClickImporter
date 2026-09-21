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
public class DeliveryDocumentsRequestBillingData extends BillingData {

    /**
     * MessageId исходного сообщения
     */
    @JsonProperty("ct_mid")
    String contentMessageId;
    /**
     * Код маршрутизации (из тега routingCode)
     */
    @JsonProperty("ct_routing_code")
    String routingCode;
    /**
     * Идентификатор ЕСИА (из тега oid)
     */
    @JsonProperty("ct_oid")
    String oid;
    /**
     * Тип документа (из тега dataType)
     */
    @JsonProperty("ct_doc_type")
    String docType;

    @JsonProperty("ct_updated_at")
    String updatedAt;
    /**
     * Идентификатор согласия (из тега idPermission)
     */
    @JsonProperty("ct_permission_id")
    String permissionId;
    /**
     * Статус запрашиваемого сведения или документа (из тега status)
     */
    @JsonProperty("ct_status")
    String status;
    /**
     * Количество вложенных файлов (из тега infoFile)
     */
    @JsonProperty("ct_info_file_count")
    Integer infoFileCount;

}

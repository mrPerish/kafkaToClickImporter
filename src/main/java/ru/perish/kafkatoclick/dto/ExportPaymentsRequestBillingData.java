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
public class ExportPaymentsRequestBillingData extends BillingData {

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
     *
     */
    @JsonProperty("ct_consumer_type")
    String consumerType;


}

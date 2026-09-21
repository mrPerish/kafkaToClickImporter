package ru.perish.kafkatoclick.kafka;

import org.junit.jupiter.api.Test;
import ru.perish.kafkatoclick.config.ImporterProperties;
import ru.perish.kafkatoclick.config.ImporterProperties.Route;
import ru.rtksoft.smev3.billing.dto.ExportChargesRequestBillingData;
import ru.rtksoft.smev3.billing.dto.RequestRejectedBillingData;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MessageRouterTest {

    private final ImporterProperties properties = new ImporterProperties("topic", Map.of(
            "RequestRejected", new Route(RequestRejectedBillingData.class,
                    "raw_smev3_non_business_res", null, Map.of()),
            "ExportChargesRequest", new Route(ExportChargesRequestBillingData.class,
                    null, "ct_charge_type",
                    Map.of("ChargesConditions", "raw_smev3_export_charges_cc_req",
                            "PayersConditions", "raw_smev3_export_charges_pc_req",
                            "TimeConditions", "raw_smev3_export_charges_tc_req"))));
    private final MessageRouter router = new MessageRouter(JsonMapper.builder().build(), properties);

    @Test
    void parsesPayloadIntoConfiguredType() {
        MessageRouter.RoutedMessage routed = router.route("RequestRejected",
                "{\"mid\":\"6f0e6b9c-6a4f-4b8e-8a2c-5f4b6d3c2a11\",\"ct_code\":\"ERR\"}");

        assertThat(routed.table()).isEqualTo("raw_smev3_non_business_res");
        assertThat(routed.data()).isInstanceOf(RequestRejectedBillingData.class);
        assertThat(((RequestRejectedBillingData) routed.data()).getCode()).isEqualTo("ERR");
    }

    @Test
    void picksTableByDiscriminator() {
        assertThat(router.route("ExportChargesRequest", "{\"ct_charge_type\":\"ChargesConditions\"}").table())
                .isEqualTo("raw_smev3_export_charges_cc_req");
        assertThat(router.route("ExportChargesRequest", "{\"ct_charge_type\":\"PayersConditions\"}").table())
                .isEqualTo("raw_smev3_export_charges_pc_req");
        assertThat(router.route("ExportChargesRequest", "{\"ct_charge_type\":\"TimeConditions\"}").table())
                .isEqualTo("raw_smev3_export_charges_tc_req");
    }

    @Test
    void rejectsUnknownDiscriminator() {
        assertThatThrownBy(() -> router.route("ExportChargesRequest", "{\"ct_charge_type\":\"Other\"}"))
                .isInstanceOf(UnknownDiscriminatorException.class);
        assertThatThrownBy(() -> router.route("ExportChargesRequest", "{}"))
                .isInstanceOf(UnknownDiscriminatorException.class);
    }

    @Test
    void rejectsUnknownTypeId() {
        assertThatThrownBy(() -> router.route("Nope", "{}"))
                .isInstanceOf(UnknownMessageTypeException.class);
    }
}

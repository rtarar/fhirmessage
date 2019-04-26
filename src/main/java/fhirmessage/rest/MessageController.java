package fhirmessage.rest;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.PerformanceOptionsEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.UriType;
import org.hl7.fhir.instance.model.api.IBaseResource;

import java.util.List;

@Controller("/")
public class MessageController {


    @Get(produces = MediaType.TEXT_PLAIN)
    public String index() {
        return processMessage();
    }


    private String processMessage(){
        FhirContext ctx = FhirContext.forDstu3();
        ctx.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);
        ctx.setPerformanceOptions(PerformanceOptionsEnum.DEFERRED_MODEL_SCANNING);
        ctx.getRestfulClientFactory().setConnectTimeout(20 * 1000);

        String serverBase = "https://eip-fhir.experimental.aimsplatform.com/hapi-fhir/baseDstu3";
        IGenericClient client = ctx.newRestfulGenericClient(serverBase);

        Bundle results = client
                .read()
                .resource(Bundle.class)
                //.withIdAndVersion("43", "2")
                .withId("44")
                .execute();
        System.out.println(ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(results));
        Bundle b = getTransactionBundle(results);
        //Bundle response = client.transaction().withBundle(results).execute();


        final String s = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(b);
        return s;
    }

    private Bundle getTransactionBundle(Bundle messageBundle){
        Bundle transactionBundle = new Bundle();
        transactionBundle.setType(Bundle.BundleType.TRANSACTION);
        for (Bundle.BundleEntryComponent bec: messageBundle.getEntry()) {
            bec.getRequest()
                    .setMethod(Bundle.HTTPVerb.POST)
                    .setUrl(bec.getResource().fhirType());
            transactionBundle.addEntry(bec);
        }

        return transactionBundle;

    }



}



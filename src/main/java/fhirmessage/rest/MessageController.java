package fhirmessage.rest;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.PerformanceOptionsEnum;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.parser.StrictErrorHandler;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import fhirmessage.service.FHIRService;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;


import io.micronaut.retry.annotation.Fallback;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.UriType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;

import java.util.List;

@Controller("/")
public class MessageController {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(MessageController.class);


    FHIRService fhirService = new FHIRService();


    @Get(produces = MediaType.TEXT_PLAIN)
    public String index() {


        return "POST FHIR XML or JSON to /transaction to get a converted FHIR Transaction Bundle \n"
                + "POST FHIR XML or  JSON to /$process-message to process a Message Bundle and unpack it on the FHIR Server.";


    }


    @Post(uri = "/$validate", consumes = { MediaType.APPLICATION_XML , MediaType.APPLICATION_JSON }, produces = MediaType.APPLICATION_XML)
    public String doValidate(@Body String body){
        //validate the payload for structure
        FhirContext ctx = FhirContext.forDstu3();
        IParser parser = ctx.newXmlParser();
        parser.setParserErrorHandler(new StrictErrorHandler());
        Bundle response = parser.parseResource(Bundle.class, body);

        //validate as per schema definitions



        Bundle b = getBundle(body);
        Bundle transactionBundle = getTransactionBundle(b);
       // FhirContext ctx = FhirContext.forDstu3();
        String serverBase = "https://eip-fhir.experimental.aimsplatform.com/hapi-fhir/baseDstu3";
        IGenericClient client = ctx.newRestfulGenericClient(serverBase);

        // Create a parser and configure it to use the strict error handler
       // IParser parser = ctx.newXmlParser();
        parser.setParserErrorHandler(new StrictErrorHandler());

        String ret = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(transactionBundle);
        logger.debug(ret);
        return ret;
    }



    @Post(uri = "/transaction", consumes = { MediaType.APPLICATION_XML , MediaType.APPLICATION_JSON }, produces = MediaType.APPLICATION_XML)
    public String doTanscation(@Body String body){
        Bundle b = getBundle(body);
        Bundle transactionBundle = getTransactionBundle(b);
        FhirContext ctx = FhirContext.forDstu3();
        String ret = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(transactionBundle);
        logger.debug(ret);
        return ret;
    }

    @Post(uri = "/$process-message", consumes = { MediaType.APPLICATION_XML , MediaType.APPLICATION_JSON }, produces = MediaType.APPLICATION_XML)
    public String processMessage(@Body String body){
        FhirContext ctx = getFHIRContext();
        Bundle b = getBundle(body);
        Bundle transactionBundle = getTransactionBundle(b);
        System.out.println(ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(transactionBundle));

        String serverBase = "https://eip-fhir.experimental.aimsplatform.com/hapi-fhir/baseDstu3";
        IGenericClient client = ctx.newRestfulGenericClient(serverBase);
        Bundle response = client.transaction().withBundle(transactionBundle).execute();
        String ret =  ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(response);
        return ret;
    }

    private FhirContext getFHIRContext(){
        FhirContext ctx = FhirContext.forDstu3();
        ctx.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);
        ctx.setPerformanceOptions(PerformanceOptionsEnum.DEFERRED_MODEL_SCANNING);
        ctx.getRestfulClientFactory().setConnectTimeout(20 * 1000);

        return ctx;
    }


// TODO Scrap method below - remove it
    @Get(uri = "/boom", produces = MediaType.TEXT_PLAIN)
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

        System.out.println(s);
        Bundle response = client.transaction().withBundle(b).execute();

        return ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(response);
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


    private Bundle getBundle(String body) {
        FhirContext ctx = FhirContext.forDstu3();
        IParser parser;
        if (body != null && body.trim().length() >0 ) {
            if (body.startsWith("<")) {
                parser = ctx.newXmlParser();
            } else {
                parser = ctx.newJsonParser();
            }
            return (Bundle) parser.parseResource(body);
        }
        return null;
    }

}



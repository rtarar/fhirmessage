package fhirmessage.fhir;

import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.dstu3.hapi.ctx.IValidationSupport;
import org.hl7.fhir.dstu3.model.CodeSystem;
import org.hl7.fhir.dstu3.model.StructureDefinition;
import org.hl7.fhir.dstu3.model.ValueSet;
import org.hl7.fhir.instance.model.api.IBaseResource;

import java.util.List;

class BSeRValidationSupport implements IValidationSupport{


    @Override
    public ValueSet.ValueSetExpansionComponent expandValueSet(FhirContext fhirContext, ValueSet.ConceptSetComponent conceptSetComponent) {
        return null;
    }

    @Override
    public List<IBaseResource> fetchAllConformanceResources(FhirContext fhirContext) {
        return null;
    }

    @Override
    public List<StructureDefinition> fetchAllStructureDefinitions(FhirContext fhirContext) {
        return null;
    }

    @Override
    public CodeSystem fetchCodeSystem(FhirContext fhirContext, String s) {
        return null;
    }

    @Override
    public <T extends IBaseResource> T fetchResource(FhirContext fhirContext, Class<T> aClass, String s) {
        return null;
    }

    @Override
    public StructureDefinition fetchStructureDefinition(FhirContext fhirContext, String s) {
        return null;
    }

    @Override
    public boolean isCodeSystemSupported(FhirContext fhirContext, String s) {
        return false;
    }

    @Override
    public CodeValidationResult validateCode(FhirContext fhirContext, String s, String s1, String s2) {
        return null;
    }
}
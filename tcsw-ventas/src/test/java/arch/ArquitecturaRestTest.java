package arch;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

// P09: Spring/HTTP es un detalle del adaptador de entrada; el nucleo no lo conoce y la API solo entra por el facade
class ArquitecturaRestTest {

    private static JavaClasses clasesDeProduccion() {
        return new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("domain", "application", "adapter");
    }

    @Test
    void domainYApplicationNoDependenDeSpringNiDeJsonNiDeValidacion() {
        ArchRule regla = noClasses().that().resideInAnyPackage("domain..", "application..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "org.springframework..", "com.fasterxml.jackson..", "jakarta.validation..", "jakarta.servlet..");
        regla.check(clasesDeProduccion());
    }

    @Test
    void controladoresDtosYErroresNoConocenLosAdaptadoresDeSalida() {
        // solo ConfiguracionApi (raiz de composicion) puede conocer adapter.out
        ArchRule regla = noClasses().that().resideInAnyPackage(
                "adapter.in.rest.controller..", "adapter.in.rest.dto..", "adapter.in.rest.error..")
            .should().dependOnClassesThat().resideInAPackage("adapter.out..");
        regla.check(clasesDeProduccion());
    }

    @Test
    void losControladoresEntranPorElFacadeNoPorLosPuertosDeSalida() {
        ArchRule regla = noClasses().that().resideInAPackage("adapter.in.rest.controller..")
            .should().dependOnClassesThat().resideInAPackage("application.port.out..");
        regla.check(clasesDeProduccion());
    }

    @Test
    void swingYRestSonAdaptadoresIndependientes() {
        ArchRule swing = noClasses().that().resideInAPackage("adapter.in.swing..")
            .should().dependOnClassesThat().resideInAPackage("adapter.in.rest..");
        ArchRule rest = noClasses().that().resideInAPackage("adapter.in.rest..")
            .should().dependOnClassesThat().resideInAPackage("adapter.in.swing..");
        swing.check(clasesDeProduccion());
        rest.check(clasesDeProduccion());
    }
}

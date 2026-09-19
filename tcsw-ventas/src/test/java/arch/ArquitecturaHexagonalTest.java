package arch;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArquitecturaHexagonalTest {

    // el dominio jamas debe conocer application ni adapter: esta regla truena si alguien lo rompe
    @Test
    void elDominioNoDependeDeApplicationNiDeAdapter() {
        JavaClasses clases = new ClassFileImporter().importPackages("domain", "application", "adapter");

        ArchRule regla = noClasses().that().resideInAPackage("domain..")
            .should().dependOnClassesThat().resideInAnyPackage("application..", "adapter..");

        regla.check(clases);
    }

    // application orquesta con interfaces, no debe atarse a un adaptador concreto
    @Test
    void applicationNoDependeDeAdapter() {
        JavaClasses clases = new ClassFileImporter().importPackages("domain", "application", "adapter");

        ArchRule regla = noClasses().that().resideInAPackage("application..")
            .should().dependOnClassesThat().resideInAPackage("adapter..");

        regla.check(clases);
    }
}
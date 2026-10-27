package arch;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

// C02: Hibernate/JPA es un detalle del adaptador; el nucleo y los demas adaptadores no lo conocen
class ArquitecturaHibernateTest {

    private static JavaClasses clasesDeProduccion() {
        return new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("domain", "application", "adapter");
    }

    @Test
    void domainYApplicationNoDependenDeHibernateNiJpa() {
        ArchRule regla = noClasses().that().resideInAnyPackage("domain..", "application..")
            .should().dependOnClassesThat().resideInAnyPackage("org.hibernate..", "jakarta.persistence..", "javax.persistence..");

        regla.check(clasesDeProduccion());
    }

    @Test
    void soloElAdaptadorHibernateUsaHibernateYJpa() {
        ArchRule regla = noClasses().that().resideOutsideOfPackages("adapter.out.hibernate..")
            .should().dependOnClassesThat().resideInAnyPackage("org.hibernate..", "jakarta.persistence..", "javax.persistence..");

        regla.check(clasesDeProduccion());
    }
}

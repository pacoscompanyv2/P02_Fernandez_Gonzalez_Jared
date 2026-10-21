package arch;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

// P08: la persistencia es un detalle del adaptador; el nucleo no puede conocer JDBC ni PostgreSQL
class ArquitecturaPersistenciaTest {

    private static JavaClasses clasesDeProduccion() {
        return new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("domain", "application", "adapter");
    }

    @Test
    void domainYApplicationNoDependenDeJdbc() {
        ArchRule regla = noClasses().that().resideInAnyPackage("domain..", "application..")
            .should().dependOnClassesThat().resideInAnyPackage("java.sql..", "javax.sql..", "org.postgresql..");

        regla.check(clasesDeProduccion());
    }

    @Test
    void soloElAdaptadorJdbcUsaJavaSql() {
        ArchRule regla = noClasses().that().resideOutsideOfPackages("adapter.out.jdbc..")
            .should().dependOnClassesThat().resideInAnyPackage("java.sql..", "javax.sql..", "org.postgresql..");

        regla.check(clasesDeProduccion());
    }
}

package antipatron;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;

// demuestra el problema: el estado de una prueba se filtra a la siguiente
// se fuerza el orden con @Order porque JUnit 5 no garantiza el orden por defecto,
// y aqui el orden es justo lo que se quiere evidenciar como fragil
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ConfiguracionGlobalSingletonTest {

    @Test
    @Order(1)
    void primeraPruebaConfiguraElDescuentoEn10() {
        ConfiguracionGlobalSingleton.getInstancia().setPorcentajeDescuentoGlobal(10);
        assertEquals(10, ConfiguracionGlobalSingleton.getInstancia().getPorcentajeDescuentoGlobal());
    }

    @Test
    @Order(2)
    void segundaPruebaVeElValorDejadoPorLaPrimeraEnVezDeUnEstadoLimpio() {
        double valorVisto = ConfiguracionGlobalSingleton.getInstancia().getPorcentajeDescuentoGlobal();
        assertEquals(10, valorVisto, "Esto demuestra el problema: deberia ser 0 si las pruebas fueran independientes, pero el Singleton arrastra estado entre ellas");
    }
}
package antipatron;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// demuestra el problema: el estado de una prueba se filtra a la siguiente
class ConfiguracionGlobalSingletonTest {

    @Test
    void primeraPruebaConfiguraElDescuentoEn10() {
        ConfiguracionGlobalSingleton.getInstancia().setPorcentajeDescuentoGlobal(10);
        assertEquals(10, ConfiguracionGlobalSingleton.getInstancia().getPorcentajeDescuentoGlobal());
    }

    @Test
    void segundaPruebaVeElValorDejadoPorLaPrimeraEnVezDeUnEstadoLimpio() {
        double valorVisto = ConfiguracionGlobalSingleton.getInstancia().getPorcentajeDescuentoGlobal();
        assertEquals(10, valorVisto, "Esto demuestra el problema: deberia ser 0 si las pruebas fueran independientes, pero el Singleton arrastra estado entre ellas");
    }
}
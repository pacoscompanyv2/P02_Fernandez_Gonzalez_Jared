package adapter.out.jdbc;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// pruebas SIN base de datos: solo la logica de leer y validar la configuracion
class ConfiguracionJdbcTest {

    private static Map<String, String> entornoCompleto() {
        Map<String, String> entorno = new HashMap<>();
        entorno.put(ConfiguracionJdbc.VAR_URL, "jdbc:postgresql://localhost:5432/demo");
        entorno.put(ConfiguracionJdbc.VAR_USUARIO, "usuario_demo");
        entorno.put(ConfiguracionJdbc.VAR_CLAVE, "clave-sintetica-123");
        return entorno;
    }

    @Test
    void conLasTresVariablesCreaLaConfiguracion() {
        ConfiguracionJdbc cfg = ConfiguracionJdbc.desde(entornoCompleto());

        assertEquals("jdbc:postgresql://localhost:5432/demo", cfg.getUrl());
        assertEquals("usuario_demo", cfg.getUsuario());
    }

    @Test
    void siFaltanVariablesLoDiceSinMostrarValores() {
        Map<String, String> entorno = entornoCompleto();
        entorno.remove(ConfiguracionJdbc.VAR_CLAVE);
        entorno.put(ConfiguracionJdbc.VAR_USUARIO, "   ");

        IllegalStateException e = assertThrows(IllegalStateException.class,
            () -> ConfiguracionJdbc.desde(entorno));

        assertTrue(e.getMessage().contains(ConfiguracionJdbc.VAR_CLAVE));
        assertTrue(e.getMessage().contains(ConfiguracionJdbc.VAR_USUARIO));
        assertFalse(e.getMessage().contains("clave-sintetica-123"));
        assertFalse(e.getMessage().contains("jdbc:postgresql"));
    }

    @Test
    void elEntornoVacioFaltanLasTres() {
        IllegalStateException e = assertThrows(IllegalStateException.class,
            () -> ConfiguracionJdbc.desde(new HashMap<>()));

        assertTrue(e.getMessage().contains(ConfiguracionJdbc.VAR_URL));
        assertTrue(e.getMessage().contains(ConfiguracionJdbc.VAR_USUARIO));
        assertTrue(e.getMessage().contains(ConfiguracionJdbc.VAR_CLAVE));
    }

    @Test
    void toStringNuncaImprimeLaClave() {
        ConfiguracionJdbc cfg = ConfiguracionJdbc.desde(entornoCompleto());

        assertFalse(cfg.toString().contains("clave-sintetica-123"));
        assertTrue(cfg.toString().contains("********"));
    }

    @Test
    void conUrlCambiaSoloLaBase() {
        ConfiguracionJdbc cfg = ConfiguracionJdbc.desde(entornoCompleto());

        ConfiguracionJdbc otra = cfg.conUrl("jdbc:postgresql://localhost:5432/demo_test");

        assertEquals("jdbc:postgresql://localhost:5432/demo_test", otra.getUrl());
        assertEquals("usuario_demo", otra.getUsuario());
        assertThrows(IllegalArgumentException.class, () -> cfg.conUrl(" "));
    }
}

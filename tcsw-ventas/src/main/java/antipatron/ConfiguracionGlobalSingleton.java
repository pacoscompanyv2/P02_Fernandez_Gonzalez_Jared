package antipatron;

// DEMO PARA ANALISIS CRITICO (P06) - NO SE USA EN EL CODIGO REAL DEL PROYECTO
public class ConfiguracionGlobalSingleton {

    private static ConfiguracionGlobalSingleton instancia;
    private double porcentajeDescuentoGlobal;

    private ConfiguracionGlobalSingleton() {
        this.porcentajeDescuentoGlobal = 0;
    }

    public static ConfiguracionGlobalSingleton getInstancia() {
        if (instancia == null) {
            instancia = new ConfiguracionGlobalSingleton();
        }
        return instancia;
    }

    public void setPorcentajeDescuentoGlobal(double porcentaje) {
        this.porcentajeDescuentoGlobal = porcentaje;
    }

    public double getPorcentajeDescuentoGlobal() {
        return porcentajeDescuentoGlobal;
    }
}
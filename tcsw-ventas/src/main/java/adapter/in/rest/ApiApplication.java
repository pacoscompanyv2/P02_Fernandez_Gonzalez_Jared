package adapter.in.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// P09: adaptador de ENTRADA REST. Escanea solo adapter.in.rest..: no toca Swing ni los adaptadores de salida
// (esos se enchufan en ConfiguracionApi, la raiz de composicion de la API).
@SpringBootApplication
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
}

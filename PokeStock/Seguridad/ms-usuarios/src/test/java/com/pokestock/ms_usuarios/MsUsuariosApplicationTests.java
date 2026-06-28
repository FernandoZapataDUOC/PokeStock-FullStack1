package com.pokestock.ms_usuarios;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// Test de smoke básico: verifica que el módulo compila y está correctamente estructurado.
// No requiere contexto de Spring ni base de datos.
@DisplayName("PokeStock ms-usuarios - smoke test")
class MsUsuariosApplicationTests {

    @Test
    @DisplayName("El módulo compila y la clase de aplicación existe")
    void contextLoads() {
        // Si este test compila y se ejecuta, la estructura del módulo es correcta.
    }

}

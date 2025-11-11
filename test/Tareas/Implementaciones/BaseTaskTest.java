package Tareas.Implementaciones; // O el paquete de test que uses

import Mensajes.AlmacenMensajes; 
import org.junit.Before; 

/**
 * Clase base para todos los tests de Tareas (JUnit 4).
 * Limpia el AlmacenMensajes antes de cada test.
 */
public abstract class BaseTaskTest {

    protected TestHelper helper = new TestHelper();
    
    @Before // <-- JUnit 4
    public void setUpBase() { // <-- JUnit 4 (public)
        // Limpiamos el Singleton ANTES de cada test
        AlmacenMensajes.getInstance().limpiar();
    }
}
package Conectores;

import Puertos.Puerto;

/**
 * Clase abstracta base para todos los conectores
 * Define la estructura común para conectores que interactúan con sistemas externos
 * Establece un Constructor (puerto + configuración) para todos los tipos de Conectores
 */
public abstract class Conector {

    protected Puerto puerto;
    protected String configuracion;

    public Conector(Puerto puerto, String configuracion) {
        this.puerto = puerto;
        this.configuracion = configuracion;
    }

    /**
     * Método abstracto que deben implementar todas las subclases
     */
    public abstract void ejecutar();
}
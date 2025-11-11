/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Mensajes;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Almacén de mensajes o datos (Patrón Singleton)
 * Permite a las tareas guardar y recuperar datos de forma centralizada.
 * Sustituye la necesidad de usar Hashtable, siendo thread-safe.
 * @author agustinrodriguez
 */
public class AlmacenMensajes {

    // Instancia única (Singleton)
    private static final AlmacenMensajes instancia = new AlmacenMensajes();
    
    // Almacén basado en ConcurrentHashMap para seguridad en hilos.
    private final Map<String, Object> almacen;

    /**
     * Constructor privado para forzar el Singleton.
     */
    private AlmacenMensajes() {
        this.almacen = new ConcurrentHashMap<>(); // Igual que HashMap pero con acceso concurrente.
    }

    /**
     * Devuelve la instancia única del almacén.
     */
    public static AlmacenMensajes getInstance() {
        return instancia;
    }

    /**
     * Guarda un valor en el almacén.
     * @param clave La clave única para el dato.
     * @param valor El dato a guardar (puede ser un Mensaje, una Lista, etc.).
     */
    public void guardar(String clave, Object valor) {
        almacen.put(clave, valor);
    }

    /**
     * Obtiene un valor del almacén.
     * @param clave La clave del dato.
     * @return El dato, o null si no existe.
     */
    public Object obtener(String clave) {
        return almacen.get(clave);
    }

    /**
     * Elimina un valor del almacén (importante para limpiar).
     * @param clave La clave del dato a eliminar.
     * @return El dato eliminado, o null si no existía.
     */
    public Object eliminar(String clave) {
        return almacen.remove(clave);
    }
    
    /**
     * Comprueba si una clave existe.
     */
    public boolean existe(String clave) {
        return almacen.containsKey(clave);
    }
    
    /**
     * Limpia todo el almacén.
     * Usado principalmente para tests unitarios.
     */
    public void limpiar() {
        almacen.clear();
    }
}

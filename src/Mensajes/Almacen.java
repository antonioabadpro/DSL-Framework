package Mensajes;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 */
public class Almacen {

    // Instancia única del Singleton
    private static Almacen instancia;

    // Almacenes principales
    private Map<UUID, Mensaje> mensajesPorId;
    private Map<UUID, List<Mensaje>> mensajesPorCorrelacion;
    private Queue<Mensaje> colaProcesamiento;

    // Estadísticas del Almacen de Mensajes
    private int totalMensajes;
    private int capacidadMaxima;

    // Constructor privado
    private Almacen() {
        this.mensajesPorId = new ConcurrentHashMap<>();
        this.mensajesPorCorrelacion = new ConcurrentHashMap<>();
        this.colaProcesamiento = new LinkedList<>();
        this.capacidadMaxima = 10000; // Capacidad por defecto
        this.totalMensajes = 0;
    }

    /**
     * @return Devuelve la instancia única del Singleton
     */
    public static Almacen getInstancia() {
        if (instancia == null) {
            instancia = new Almacen();
        }
        return instancia;
    }

    /**
     * Guarda un mensaje en el Almacén de Mensajes
     * @param mensaje Mensaje a guardar
     * @return Dvuelve 'true' si se guardó correctamente y 'false' en caso contrario o si el almacén está lleno
     */
    public boolean guardarMensaje(Mensaje mensaje) {
        // Verificar capacidad primero
        if (estaLleno()) {
            return false;
        }

        UUID id = mensaje.getId();

        // Guardamos el id del Mensaje
        mensajesPorId.put(id, mensaje);

        // Guardamos el idCorrelacion del Mensaje
        UUID idCorrelacion = mensaje.getIdCorrelacion();
        if (idCorrelacion != null) {
            // Verificamos si ya existe una lista para esta correlación
            if (!mensajesPorCorrelacion.containsKey(idCorrelacion)) {
                // Si NO existe, creamos una nueva lista vacía
                mensajesPorCorrelacion.put(idCorrelacion, new ArrayList<>());
            }
            // Obtenemos la lista y añadimos el mensaje a dicha lista de Mensajes
            mensajesPorCorrelacion.get(idCorrelacion).add(mensaje);
        }

        // Encolamos el Mensaje para procesarlo
        boolean encolado = colaProcesamiento.offer(mensaje);
        if (!encolado) {
            // Si el Mensaje NO se puede encolar, NO lo guardamos en el Almcaen
            mensajesPorId.remove(id);
            if (idCorrelacion != null) {
                List<Mensaje> listaCorrelacion = mensajesPorCorrelacion.get(idCorrelacion);
                if (listaCorrelacion != null) {
                    listaCorrelacion.remove(mensaje);
                    // Si la lista queda vacía, la eliminamos del Map
                    if (listaCorrelacion.isEmpty()) {
                        mensajesPorCorrelacion.remove(idCorrelacion);
                    }
                }
            }
            return false;
        }

        totalMensajes++;
        return true;
    }

    /**
     * Obtiene un mensaje por su ID
     * @param id UUID del mensaje
     * @return Devuelve el Mensaje o null si NO existe
     */
    public Mensaje obtenerPorId(UUID id) {
        return mensajesPorId.get(id);
    }

    /**
     * Obtiene todos los mensajes de una correlación específica
     * @param idCorrelacion ID de correlación
     * @return Devuelve una Lista de mensajes (la lista puede estar vacía si NO hay ningun Mensaje)
     */
    public List<Mensaje> obtenerPorCorrelacion(UUID idCorrelacion) {
        return mensajesPorCorrelacion.getOrDefault(idCorrelacion, new ArrayList<>());
    }

    /**
     * Obtiene el siguiente mensaje para procesar
     * @return Devuelve el siguiente mensaje para procesarlo o null si NO hay más Mensajes
     */
    public Mensaje obtenerSiguiente() {
        return colaProcesamiento.poll();
    }

    /**
     * Elimina un mensaje del almacén
     * @param id UUID del mensaje a eliminar
     * @return Devuelve 'true' si el Mensaje se ha eliminado con exito y 'false' en caso contrario o si NO existe
     */
    public boolean eliminarMensaje(UUID id) {
        Mensaje mensaje = mensajesPorId.remove(id);
        if (mensaje != null) {
            // Eliminamos el idCorrelacion del Mensaje
            UUID idCorrelacion = mensaje.getIdCorrelacion();
            if (idCorrelacion != null) {
                List<Mensaje> correlacionados = mensajesPorCorrelacion.get(idCorrelacion);
                if (correlacionados != null) {
                    correlacionados.remove(mensaje);
                    if (correlacionados.isEmpty()) {
                        mensajesPorCorrelacion.remove(idCorrelacion);
                    }
                }
            }
            totalMensajes--;
            return true;
        }
        return false;
    }

    /**
     * Verifica si el almacén está lleno
     * @return Devuelve 'true' si el Almacen está lleno y 'false' en caso contrario
     */
    public boolean estaLleno() {
        return totalMensajes >= capacidadMaxima;
    }

    /**
     * Obtiene todos los mensajes almacenados
     * @return Devuelve una Colección de todos los mensajes que hay en el Almacen
     */
    public Collection<Mensaje> obtenerTodos() {
        return new ArrayList<>(mensajesPorId.values());
    }

    /**
     * @return Devuelve Map con estadísticas del Almacen de Mensajes
     */
    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalMensajes", totalMensajes);
        stats.put("capacidadMaxima", capacidadMaxima);
        stats.put("porcentajeUso", (totalMensajes * 100.0) / capacidadMaxima);
        stats.put("correlacionesActivas", mensajesPorCorrelacion.size());
        stats.put("enColaProcesamiento", colaProcesamiento.size());
        return stats;
    }

    /**
     * Limpia todo el Almacen de Mensajes
     */
    public void limpiar() {
        mensajesPorId.clear();
        mensajesPorCorrelacion.clear();
        colaProcesamiento.clear();
        totalMensajes = 0;
    }

    /**
     * Establece la capacidad máxima del almacén
     * @param capacidad Nueva capacidad
     */
    public void setCapacidadMaxima(int capacidad) {
        this.capacidadMaxima = capacidad;
    }

    // Getters
    public int getTotalMensajes() {
        return totalMensajes;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public int getEnColaProcesamiento() {
        return colaProcesamiento.size();
    }
}

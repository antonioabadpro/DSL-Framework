package Mensajes;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Almacen {

    private static Almacen instancia;

    // Mapas concurrentes (Thread-Safe)
    private Map<UUID, Mensaje> mensajesPorId;
    private Map<UUID, List<Mensaje>> mensajesPorCorrelacion;

    // Cola concurrente (Thread-Safe)
    private Queue<Mensaje> colaProcesamiento;

    // Contador Atómico (Thread-Safe para sumas y restas)
    private AtomicInteger totalMensajes;
    private volatile int capacidadMaxima; // volatile asegura que todos los hilos vean el cambio de valor

    private Almacen() {
        this.mensajesPorId = new ConcurrentHashMap<>();
        this.mensajesPorCorrelacion = new ConcurrentHashMap<>();
        this.colaProcesamiento = new ConcurrentLinkedQueue<>();
        this.capacidadMaxima = 10000;
        this.totalMensajes = new AtomicInteger(0);
    }

    public static synchronized Almacen getInstancia() {
        if (instancia == null) {
            instancia = new Almacen();
        }
        return instancia;
    }

    public boolean guardarMensaje(Mensaje mensaje) {
        if (estaLleno()) {
            return false;
        }

        UUID id = mensaje.getId();
        mensajesPorId.put(id, mensaje);

        UUID idCorrelacion = mensaje.getIdCorrelacion();
        if (idCorrelacion != null) {
            // computeIfAbsent es atómico: si no existe la lista, la crea de forma segura
            // Usamos una lista sincronizada para proteger el acceso interno a esa lista
            mensajesPorCorrelacion.computeIfAbsent(idCorrelacion,
                    k -> Collections.synchronizedList(new ArrayList<>())
            ).add(mensaje);
        }

        colaProcesamiento.offer(mensaje);

        // Incremento atómico
        totalMensajes.incrementAndGet();
        return true;
    }

    public Mensaje obtenerPorId(UUID id) {
        return mensajesPorId.get(id);
    }

    public List<Mensaje> obtenerPorCorrelacion(UUID idCorrelacion) {
        // Devolvemos una copia o la lista sincronizada (cuidado al iterar manualmente fuera)
        List<Mensaje> lista = mensajesPorCorrelacion.get(idCorrelacion);
        if (lista == null) {
            return new ArrayList<>();
        }
        // Devolvemos una copia para evitar problemas de concurrencia al leer
        synchronized (lista) {
            return new ArrayList<>(lista);
        }
    }

    public Mensaje obtenerSiguiente() {
        return colaProcesamiento.poll();
    }

    public boolean eliminarMensaje(UUID id) {
        Mensaje mensaje = mensajesPorId.remove(id);
        if (mensaje != null) {
            UUID idCorrelacion = mensaje.getIdCorrelacion();
            if (idCorrelacion != null) {
                List<Mensaje> lista = mensajesPorCorrelacion.get(idCorrelacion);
                if (lista != null) {
                    lista.remove(mensaje);
                    if (lista.isEmpty()) {
                        mensajesPorCorrelacion.remove(idCorrelacion);
                    }
                }
            }
            // Decremento atómico
            totalMensajes.decrementAndGet();
            return true;
        }
        return false;
    }

    public boolean estaLleno() {
        return totalMensajes.get() >= capacidadMaxima;
    }

    public Collection<Mensaje> obtenerTodos() {
        return new ArrayList<>(mensajesPorId.values());
    }

    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalMensajes", totalMensajes.get());
        stats.put("capacidadMaxima", capacidadMaxima);
        stats.put("porcentajeUso", (totalMensajes.get() * 100.0) / capacidadMaxima);
        stats.put("correlacionesActivas", mensajesPorCorrelacion.size());
        stats.put("enColaProcesamiento", colaProcesamiento.size());
        return stats;
    }

    public void limpiar() {
        mensajesPorId.clear();
        mensajesPorCorrelacion.clear();
        colaProcesamiento.clear();
        totalMensajes.set(0);
    }

    public void setCapacidadMaxima(int capacidad) {
        this.capacidadMaxima = capacidad;
    }

    public int getTotalMensajes() {
        return totalMensajes.get();
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public int getEnColaProcesamiento() {
        return colaProcesamiento.size();
    }
}

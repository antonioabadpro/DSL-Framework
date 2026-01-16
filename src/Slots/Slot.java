package Slots;

import Mensajes.Mensaje;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Slot {

    // Usamos LinkedBlockingQueue: es segura para hilos (Thread-Safe)
    // y bloquea automáticamente si intentas leer y está vacía.
    private final BlockingQueue<Mensaje> colaMensajes;

    public Slot() {
        this.colaMensajes = new LinkedBlockingQueue<>();
    }

    /**
     * Saca un mensaje. BLOQUEA al hilo si está vacío hasta que llegue algo.
     */
    public Mensaje leerSlot() {
        try {
            // take() es la clave: espera pasivamente si no hay mensajes
            Mensaje msj = colaMensajes.take();
            // System.out.println("Slot: Leído mensaje " + msj.getId()); // Descomenta para depurar a lo bestia
            return msj;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    /**
     * Mete un mensaje. Es seguro para múltiples hilos escribiendo a la vez.
     */
    public void escribirSlot(Mensaje msj) {
        try {
            colaMensajes.put(msj);
            // System.out.println("Slot: Escrito mensaje " + msj.getId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int getLongitud() {
        return colaMensajes.size();
    }

    public boolean estaVacio() {
        return colaMensajes.isEmpty();
    }
}

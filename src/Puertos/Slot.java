/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Puertos;

import Mensajes.Mensaje;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author agustinrodriguez
 */
public class Slot {
 
    // Ahora la cola opera con los mensajes.
    private final Queue<Mensaje> queue = new ConcurrentLinkedQueue<>();

    /**
     * Lee y elimina un mensaje de la cola.
     * Usa poll() que devuelve null si la cola está vacía (más seguro que remove()).
     * Ya no lanza Exception, simplificando las tareas.
     * @return 
     */
    public Mensaje leer() {
        return queue.poll();
    }

    /**
     * Escribe un mensaje en la cola.
     * Ya no lanza Exception.
     * @param msg
     */
    public void escribir(Mensaje msg) {
        queue.add(msg);
    }
    
    /**
     * Método seguro para consultar el tamaño de la cola.
     * Reemplaza la necesidad de getQueue().size()
     * @return 
     */
    public int getTamanio() {
        return queue.size();
    }
    
    /**
     * Método seguro para consultar si la cola está vacía.
     * @return 
     */
    public boolean estaVacio() {
        return queue.isEmpty();
    }
    
}
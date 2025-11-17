package Slots;

import Mensajes.Mensaje;
import java.util.ArrayList;
import java.util.List;

/**
 * Slots del Proceso de Integracion que se comunican con las Tareas y los Conectores
 */
public class Slot {
    
    // Atributos
    private final List<Mensaje> listaMensajes;

    // Constructor
    public Slot() {
        this.listaMensajes = new ArrayList<>();
    }
    
    /**
     * @return Devuelve el primer mensaje del Slot
     */
    public Mensaje leerSlot() {
        if (listaMensajes.isEmpty()) {
            System.out.println("Slot vacío - No hay mensajes para leer");
            return null;
        }
        
        Mensaje msjLeido = listaMensajes.remove(0);
        System.out.println("Leyendo mensaje: " + msjLeido);
        return msjLeido;
    }

    /**
     * Escribe un mensaje al final del Slot
     * @param msj Mensaje que queremos escribir en el Slot
     */
    public void escribirSlot(Mensaje msj) {
        listaMensajes.add(msj);
        System.out.println("Escribiendo mensaje: " + msj);
    }
    
    /**
     * @return Devuelve el numero de listas de Mensajes 'listaMensajes' que hay en el Slot
     */
    public int getLongitud() {
        return listaMensajes.size();
    }
    
    /**
     * @return Devuelve 'true' si la Cola esta vacia y 'false' en caso contrario
     */
    public boolean estaVacio() {
        return listaMensajes.isEmpty();
    }
    
    /**
     * Obtiene el siguiente mensaje del Slot sin eliminarlo
     * @return Devuelve el siguiente mensaje que hay en el Slot o null si está vacío
     */
    public Mensaje mirarSiguiente() {
        if (listaMensajes.isEmpty()) {
            return null;
        }
        return listaMensajes.get(0);
    }
    
    /**
     * Elimina o Limpia todas las Listas de Mensajes 'listaMensajes' del Slot
     */
    public void limpiar() {
        int cantidad = listaMensajes.size();
        listaMensajes.clear();
        System.out.println("Slot limpiado. Mensajes eliminados: " + cantidad);
    }
    
    /**
     * @return Devuelve el Slot con un formato personalizado
     */
    @Override
    public String toString() {
        return String.format("Slot[mensajes=%d]", listaMensajes.size());
    }
}
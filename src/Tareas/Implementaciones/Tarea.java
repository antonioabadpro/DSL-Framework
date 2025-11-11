package Tareas.Implementaciones;

import Puertos.Slot;
import Mensajes.Mensaje; // Importamos la nueva clase Mensaje
import Mensajes.AlmacenMensajes; // Importamos el Almacén
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Document;

/**
 * @author agustinrodriguez
 */
public abstract class Tarea {
    private ArrayList<Slot> slotEntradas;
    private ArrayList<Slot> slotSalidas;
    private TipoTarea tipo;

    // Damos acceso al Almacén a todas las tareas hijas para consultar mensajes a través del Singleton.
    protected final AlmacenMensajes almacen = AlmacenMensajes.getInstance();

    public Tarea(ArrayList<Slot> slotEntradas, ArrayList<Slot> slotSalidas, TipoTarea tipo) {
        this.slotEntradas = slotEntradas;
        this.slotSalidas = slotSalidas;
        this.tipo = tipo;
    }

    // <editor-fold defaultstate="collapsed" desc="Getters y Setters">
    public ArrayList<Slot> getEntradas() {
        return slotEntradas;
    }

    public void setEntradas(ArrayList<Slot> entradas) {
        this.slotEntradas = entradas;
    }

    public ArrayList<Slot> getSalidas() {
        return slotSalidas;
    }

    public void setSalidas(ArrayList<Slot> salidas) {
        this.slotSalidas = salidas;
    }

    public TipoTarea getTipo() {
        return tipo;
    }

    public void setTipo(TipoTarea tipo) {
        this.tipo = tipo;
    }
    // </editor-fold>

    public abstract void ejecutar();

    /**
     * AÑADIDO: Método de utilidad para clonar un mensaje.
     * Esencial para tareas que replican o distribuyen mensajes.
     */
    protected Mensaje clonarMensaje(Mensaje msg) {
        if (msg == null) {
            return null;
        }
        // Clonar el cuerpo (XML Document)
        Document cuerpoCopia = (Document) msg.getCuerpo().cloneNode(true);
        // Clonar la cabecera (Map)
        Map<String, Object> cabeceraCopia = new HashMap<>(msg.getCabecera());
        
        return new Mensaje(cabeceraCopia, cuerpoCopia);
    }
}
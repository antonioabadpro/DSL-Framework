package Puertos;

import Mensajes.Mensaje;
import Slots.Slot;

/**
 * El Puerto de Solicitud puede leer y escribir en diferentes Slots
 */
public class PuertoSolicitud extends Puerto {
    
    // Atributos
    private Slot slotSalida; 

    // Constructor
    public PuertoSolicitud(Slot slotEntrada, Slot slotSalida) {
        super(slotEntrada);
        this.slotSalida = slotSalida;
    }
    
    // Metodos
    public void escribirMensajeEnSlot(Mensaje msj) {
        this.slotSalida.escribirSlot(msj); 
    }
    
    public Mensaje leerMensajeDeSlot() {
        Mensaje msjLeido = this.slot.leerSlot();
        
        return msjLeido;
    }
    
    // Getters
    public Slot getSlotEntrada() {
        return this.slot;  
    }
    
    public Slot getSlotSalida() {
        return this.slotSalida;
    }
}

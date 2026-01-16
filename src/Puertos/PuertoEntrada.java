package Puertos;

import Mensajes.Mensaje;
import Slots.Slot;

/**
 * El Puerto de Entrada se encarga de escribir los mensajes en el Slot (Entra en el Proceso de Integracion)
 */
public class PuertoEntrada extends Puerto {
        
    public PuertoEntrada(Slot slot) {
        super(slot);
    }

    public void escribirMensajeEnSlot(Mensaje msj) {
        this.slot.escribirSlot(msj);
    }
    
}

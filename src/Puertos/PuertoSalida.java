package Puertos;

import Mensajes.Mensaje;
import Slots.Slot;

/**
 * El Puerto de Salida se encarga de leer los mensajes del Slot (Sale del Proceso de Integracion)
 */
public class PuertoSalida extends Puerto {
        
    public PuertoSalida(Slot slot) {
        super(slot);
    }

    public Mensaje leerMensajeDeSlot() {
        Mensaje msjLeido = this.slot.leerSlot();
        return msjLeido;
    }
}

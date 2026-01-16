package Tareas.Implementacion;

import Mensajes.Mensaje;
import Slots.Slot;
import Tareas.Tarea;
import Tareas.Tipo;
import java.util.ArrayList;

public class Replicator extends Tarea {

    public Replicator(ArrayList<Slot> listaEntradas, ArrayList<Slot> listaSalidas) {
        super(listaEntradas, listaSalidas, Tipo.ENRUTADORA);

        if (listaEntradas == null || listaEntradas.size() != 1) {
            throw new IllegalArgumentException("La Tarea Replicator debe tener 1 Slot de Entrada");
        }
        if (listaSalidas == null || listaSalidas.size() < 2) {
            throw new IllegalArgumentException("La Tarea Replicator debe tener al menos 2 Slots de Salida");
        }
    }

    @Override
    public void procesarMensaje() {
        // 1. Lectura bloqueante
        Mensaje msjInicial = this.listaEntradas.get(0).leerSlot();
        if (msjInicial == null) {
            return;
        }

        for (Slot s : this.listaSalidas) {
            try {
                // Clonamos para cada salida
                Mensaje msgCopia = Mensaje.clonarMensaje(msjInicial);
                s.escribirSlot(msgCopia);
            } catch (Exception ex) {
                System.out.println("Replicator: Error al clonar mensaje");
            }
        }
    }
}

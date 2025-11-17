package Tareas.Implementacion;

import Mensajes.Mensaje;
import Slots.Slot;
import Tareas.Tarea;
import Tareas.Tipo;
import java.util.ArrayList;

/**
 * Entradas: 1; Salidas: n
 */
public class Replicator extends Tarea {

    public Replicator(ArrayList<Slot> listaEntradas, ArrayList<Slot> listaSalidas) {
        super(listaEntradas, listaSalidas, Tipo.ENRUTADORA);

        // Comprobamos que el numero de Slots de Entrada y de Salida son correctos
        if (listaEntradas == null || listaEntradas.size() != 1) {
            throw new IllegalArgumentException("La Tarea Replicator debe tener 1 Slot de Entrada");
        }

        if (listaSalidas == null || listaSalidas.size() < 2) {
            throw new IllegalArgumentException("La Tarea Replicator debe tener al menos 2 Slots de Salida");
        }
    }

    @Override
    public void ejecutar() {
        while (!this.listaEntradas.get(0).estaVacio()) {
            Mensaje msjInicial = this.listaEntradas.getFirst().leerSlot();

            if (msjInicial != null) {
                for (Slot s : this.listaSalidas) {
                    try {
                        Mensaje msgCopia = Mensaje.clonarMensaje(msjInicial);
                        s.escribirSlot(msgCopia);
                    } catch (Exception ex) {
                        System.out.println("\nReplicator: Error al clonar mensaje");
                    }
                }
            }
        }
    }
}

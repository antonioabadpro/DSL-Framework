package Tareas.Implementacion;

import Mensajes.Mensaje;
import Slots.Slot;
import Tareas.Tarea;
import Tareas.Tipo;
import java.util.ArrayList;

/**
 * Entradas: n; Salidas: 1
 */
public class Merger extends Tarea {

    public Merger(ArrayList<Slot> listaEntradas, ArrayList<Slot> listaSalidas) {
        super(listaEntradas, listaSalidas, Tipo.ENRUTADORA);

        // Comprobamos que el numero de Slots de Entrada y de Salida son correctos
        if (listaEntradas == null || listaEntradas.size() < 2) {
            throw new IllegalArgumentException("La Tarea Merger debe tener al menos 2 Slots de Entrada");
        }

        if (listaSalidas == null || listaSalidas.size() != 1) {
            throw new IllegalArgumentException("La Tarea Merger debe tener 1 Slot de Salida");
        }
    }

    @Override
    public void ejecutar() {
        for (Slot s : this.listaEntradas) {
            
            while (!s.estaVacio()) {
                Mensaje msj = s.leerSlot();
                if (msj != null)
                    this.listaSalidas.getFirst().escribirSlot(msj);
            }
            
        }
    }
}

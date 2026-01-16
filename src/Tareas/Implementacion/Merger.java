package Tareas.Implementacion;

import Mensajes.Mensaje;
import Slots.Slot;
import Tareas.Tarea;
import Tareas.Tipo;
import java.util.ArrayList;

public class Merger extends Tarea {

    public Merger(ArrayList<Slot> listaEntradas, ArrayList<Slot> listaSalidas) {
        super(listaEntradas, listaSalidas, Tipo.ENRUTADORA);

        if (listaEntradas == null || listaEntradas.size() < 2) {
            throw new IllegalArgumentException("La Tarea Merger debe tener al menos 2 Slots de Entrada");
        }
        if (listaSalidas == null || listaSalidas.size() != 1) {
            throw new IllegalArgumentException("La Tarea Merger debe tener 1 Slot de Salida");
        }
    }

    @Override
    public void procesarMensaje() {
        // En un merger concurrente, tenemos que vigilar todos los canales.
        // Como 'leerSlot' bloquea, hacemos una ronda rápida.
        // NOTA: Esta implementación simple puede bloquearse esperando en el primer slot
        // si está vacío, aunque el segundo tenga datos. Para una versión más avanzada
        // haría falta un método 'poll' no bloqueante en Slot, pero esto sirve para empezar.

        for (Slot s : this.listaEntradas) {
            // Solo intentamos leer si NO está vacío para evitar bloquear el hilo en un canal muerto
            // mientras otros tienen datos.
            if (!s.estaVacio()) {
                Mensaje msj = s.leerSlot();
                if (msj != null) {
                    this.listaSalidas.get(0).escribirSlot(msj);
                }
            }
        }

        // Pequeña pausa para no quemar la CPU si todos están vacíos
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

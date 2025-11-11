package Tareas.Creadoras;

import Puertos.Slot;
import Tareas.Implementaciones.Tarea;
import java.util.ArrayList;

/**
 *
 * @author Sergio
 */
public abstract class CreadorTarea {
    public abstract Tarea creaTarea(ArrayList<Slot> slotEntradas, ArrayList<Slot> slotSalidas);
}

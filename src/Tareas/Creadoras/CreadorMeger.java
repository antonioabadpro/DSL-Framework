package Tareas.Creadoras;

import Puertos.Slot;
import Tareas.Implementaciones.Merger;
import Tareas.Implementaciones.Tarea;
import java.util.ArrayList;

/**
 *
 * @author Sergio
 */
public class CreadorMeger extends CreadorTarea{

    public CreadorMeger() {
    }

    @Override
    public Tarea creaTarea(ArrayList<Slot> slotEntradas, ArrayList<Slot> slotSalidas) {
        return new Merger(slotEntradas,slotSalidas);
    }
    
    
}

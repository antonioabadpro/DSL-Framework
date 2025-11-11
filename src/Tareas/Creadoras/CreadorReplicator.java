package Tareas.Creadoras;

import Puertos.Slot;
import Tareas.Implementaciones.Replicator;
import Tareas.Implementaciones.Tarea;
import java.util.ArrayList;

/**
 *
 * @author Sergio
 */
public class CreadorReplicator extends CreadorTarea{

    public CreadorReplicator() {
    }
    
    @Override
    public Tarea creaTarea(ArrayList<Slot> slotEntradas, ArrayList<Slot> slotSalidas) {
        return new Replicator(slotEntradas, slotSalidas);
    }
    
}

package Tareas.Creadoras;

import Puertos.Slot;
import Tareas.Implementaciones.Distributor;
import Tareas.Implementaciones.Tarea;
import java.util.ArrayList;

/**
 *
 * @author Sergio
 */
public class CreadorDistributor extends CreadorTarea{

    public CreadorDistributor() {
    }
        
    @Override
    public Tarea creaTarea(ArrayList<Slot> slotEntradas, ArrayList<Slot> slotSalidas) {
        return new Distributor(slotEntradas, slotSalidas);
    }
    
}

package Tareas.Creadoras;

import Puertos.Slot;
import Tareas.Implementaciones.Correlator;
import Tareas.Implementaciones.Tarea;
import java.util.ArrayList;

/**
 *
 * @author Sergio
 */
public class CreadorCorrelator extends CreadorTarea{
    private String tagClave;

    public CreadorCorrelator(String tagClave) {
        this.tagClave = tagClave;
    }

    @Override
    public Tarea creaTarea(ArrayList<Slot> slotEntradas, ArrayList<Slot> slotSalidas) {
        return new Correlator(slotEntradas, slotSalidas, tagClave);
    }
    
    
}

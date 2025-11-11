package Tareas.Creadoras;

import Puertos.Slot;
import Tareas.Implementaciones.Tarea;
import Tareas.Implementaciones.Translator;
import java.util.ArrayList;
/**
 *
 * @author Sergio
 */
public class CreadorTranslator extends CreadorTarea{
    private String xslt;

    public CreadorTranslator(String xslt) {
        this.xslt = xslt;
    }

    @Override
    public Tarea creaTarea(ArrayList<Slot> slotEntradas, ArrayList<Slot> slotSalidas) {
        return new Translator(slotEntradas, slotSalidas, this.xslt);
    }
    
    
}

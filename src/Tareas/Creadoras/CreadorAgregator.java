package Tareas.Creadoras;

import Puertos.Slot;
import Tareas.Implementaciones.Agregator;
import Tareas.Implementaciones.Tarea;
import java.util.ArrayList;

/**
 *
 * @author Sergio
 */
public class CreadorAgregator extends CreadorTarea{
    private String etiquetaGrupo;   
    private String etiquetaElemento; 
    private String etiquetaId;  

    public CreadorAgregator(String etiquetaGrupo, String etiquetaElemento, String etiquetaId) {
        this.etiquetaGrupo = etiquetaGrupo;
        this.etiquetaElemento = etiquetaElemento;
        this.etiquetaId = etiquetaId;
    }
    
    @Override
    public Tarea creaTarea(ArrayList<Slot> slotEntradas, ArrayList<Slot> slotSalidas) {
       return new Agregator(slotEntradas, slotSalidas, this.etiquetaGrupo, this.etiquetaElemento, this.etiquetaId);
    }
    
}

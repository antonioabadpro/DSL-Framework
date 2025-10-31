/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Tareas.Implementaciones;

import Puertos.Slot;
import java.util.ArrayList;

/**
 *
 * @author agustinrodriguez
 */
public abstract class Tarea {
    
    private ArrayList<Slot> inputs;
    private ArrayList<Slot> outputs;
    private TipoTarea tipo;
    
    public void run(){}
    
}

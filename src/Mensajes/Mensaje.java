/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Mensajes;

import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Document;

/**
 * Implementación del patrón "Message" de EIP.
 * Encapsula una cabecera (metadatos) y un cuerpo.
 * @author agustinrodriguez
 */
public class Mensaje {

    // Cabecera para metadatos (IDs, contadores, etc.)
    private Map<String, Object> cabecera;
    
    // Cuerpo del mensaje (el XML)
    private Document cuerpo;

    /**
     * Constructor para mensajes completos.
     */
    public Mensaje(Map<String, Object> cabecera, Document cuerpo) {
        this.cabecera = cabecera;
        this.cuerpo = cuerpo;
    }

    /**
     * Constructor para mensajes que solo tienen cuerpo.
     * Inicializa una cabecera vacía para evitar NullPointerExceptions.
     */
    public Mensaje(Document cuerpo) {
        this.cabecera = new HashMap<>();
        this.cuerpo = cuerpo;
    }

    // --- Getters y Setters ---
    
    public Map<String, Object> getCabecera() {
        return cabecera;
    }

    public void setCabecera(Map<String, Object> cabecera) {
        this.cabecera = cabecera;
    }

    public Document getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(Document cuerpo) {
        this.cuerpo = cuerpo;
    }
    
    // --- Métodos de cabecera ---
    
    /**
     * Añade o actualiza un valor en la cabecera.
     */
    public void setHeader(String nombre, Object valor) {
        this.cabecera.put(nombre, valor);
    }
    
    /**
     * Obtiene un valor de la cabecera.
     */
    public Object getHeader(String nombre) {
        return this.cabecera.get(nombre);
    }
}
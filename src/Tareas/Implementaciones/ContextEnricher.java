package Tareas.Implementaciones;

import Puertos.Slot;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ContextEnricher extends Tarea {

    private Slot entrada;
    private Slot contexto;
    private Slot salida;

    public ContextEnricher(Slot entrada, Slot contexto, Slot salida) {
        this.entrada = entrada;
        this.contexto = contexto;
        this.salida = salida;
    }

    public Slot getEntrada() {
        return entrada;
    }

    public void setEntrada(Slot entrada) {
        this.entrada = entrada;
    }

    public Slot getContexto() {
        return contexto;
    }

    public void setContexto(Slot contexto) {
        this.contexto = contexto;
    }

    public Slot getSalida() {
        return salida;
    }

    public void setSalida(Slot salida) {
        this.salida = salida;
    }

    @Override
    public void ejecutar() {

        Document documentoContexto;
        Element elementoContexto = null;

        try {
            documentoContexto = this.contexto.leer();

            if (documentoContexto == null) {
                System.out.println("El slot de contexto está vacío");
                return;
            } 
            else {
                elementoContexto = documentoContexto.getDocumentElement();
            }

        } catch (Exception e) {
            System.out.println("Excepcion al leer contexto: " + e.getMessage());
        }

        while (!this.entrada.getQueue().isEmpty()) {
            Document docMensaje = null;

            try {
                docMensaje = this.entrada.leer();
                if (docMensaje != null) {
                    
                    //enriquecer
                    Element elementoMensaje = docMensaje.getDocumentElement();
                    Node nodoImportado = docMensaje.importNode(elementoContexto, true);
                    elementoMensaje.appendChild(nodoImportado);

                    this.salida.escribir(docMensaje);
                }

            } catch (Exception e) {
                System.out.println("Excepcion procesando un mensaje: " + e.getMessage());
            }
        }
    }
}

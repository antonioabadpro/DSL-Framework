package Tareas.Implementaciones;

import Puertos.Slot;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ContextEnricher extends Tarea {
  
    private Slot contexto;

    public ContextEnricher(ArrayList<Slot> entrada, Slot contexto, ArrayList<Slot> salida) {
        super(entrada, salida, TipoTarea.MODIFICADORAS);
        this.contexto = contexto;
    }

    public Slot getContexto() {
        return contexto;
    }

    public void setContexto(Slot contexto) {
        this.contexto = contexto;
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

        while (!this.getEntradas().get(0).getQueue().isEmpty()) {
            Document docMensaje = null;

            try {
                docMensaje = this.getEntradas().get(0).leer();
                if (docMensaje != null) {
                    
                    //enriquecer
                    Element elementoMensaje = docMensaje.getDocumentElement();
                    Node nodoImportado = docMensaje.importNode(elementoContexto, true);
                    elementoMensaje.appendChild(nodoImportado);

                    this.getSalidas().get(0).escribir(docMensaje);
                }

            } catch (Exception e) {
                System.out.println("Excepcion procesando un mensaje: " + e.getMessage());
            }
        }
    }
}

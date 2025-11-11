package Tareas.Implementaciones;

import Puertos.Slot;
import Mensajes.Mensaje; // Importar
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ContextEnricher extends Tarea {
  
    // CAMBIO: Ya no es un Slot, es la CLAVE del almacén
    private String claveContexto;

    public ContextEnricher(ArrayList<Slot> entrada, String claveContexto, ArrayList<Slot> salida) {
        super(entrada, salida, TipoTarea.MODIFICADORAS);
        this.claveContexto = claveContexto;
    }

    public String getClaveContexto() {
        return claveContexto;
    }

    public void setClaveContexto(String claveContexto) {
        this.claveContexto = claveContexto;
    }

    @Override
    public void ejecutar() {
        Document documentoContexto;
        Element elementoContexto = null;

        try {
            // CAMBIO: Obtenemos el contexto desde el Almacén
            // (El campo 'almacen' viene de la clase Tarea)
            documentoContexto = (Document) almacen.obtener(this.claveContexto);

            if (documentoContexto == null) {
                System.out.println("ContextEnricher: No se encontró contexto en el almacén con clave: " + claveContexto);
                return;
            } else {
                elementoContexto = documentoContexto.getDocumentElement();
            }
        } catch (Exception e) {
            System.out.println("Excepcion al leer contexto del almacén: " + e.getMessage());
            return; // No podemos continuar si no hay contexto
        }

        while (!this.getEntradas().get(0).estaVacio()) {
            Mensaje msgEntrada = null;
            try {
                // Leemos el Mensaje completo
                msgEntrada = this.getEntradas().get(0).leer();
                if (msgEntrada == null) continue;
                
                // Obtenemos el cuerpo para modificarlo
                Document docMensaje = msgEntrada.getCuerpo();
                
                // Lógica de enriquecimiento (igual que antes)
                Element elementoMensaje = docMensaje.getDocumentElement();
                Node nodoImportado = docMensaje.importNode(elementoContexto, true);
                elementoMensaje.appendChild(nodoImportado);

                // CAMBIO: Escribimos el MENSAJE ORIGINAL.
                // Como modificamos su 'cuerpo' (docMensaje) "in-place", 
                // la cabecera se preserva automáticamente.
                this.getSalidas().get(0).escribir(msgEntrada);
                
            } catch (Exception e) {
                System.out.println("Excepcion procesando un mensaje: " + e.getMessage());
            }
        }
    }
}
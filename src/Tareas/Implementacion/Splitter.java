package Tareas.Implementacion;

import Mensajes.Almacen;
import Mensajes.Mensaje;
import Slots.Slot;
import Tareas.Tarea;
import Tareas.Tipo;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.UUID;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Entradas: 1; Salidas: 1
 * Ejemplo de mensaje fragmento:
        <cabecera>
            <id>UUID</id>
            <idCorrelacion>ID_SECUENCIA_LISTA</idCorrelacion>
            <idSecuencia>POSICION</idSecuencia>
            <longitud>TOTAL_FRAGMENTOS</longitud>
        </cabecera>
        <drink>...</drink>
 */
public class Splitter extends Tarea {

    private final String expresionXpath;

    public Splitter(ArrayList<Slot> entradas, ArrayList<Slot> salidas, String expresion) {
        super(entradas, salidas, Tipo.TRANSFORMADORA);
        this.expresionXpath = expresion;

        if (listaEntradas == null || listaEntradas.size() != 1) {
            throw new IllegalArgumentException("La Tarea Splitter debe tener 1 Slot de Entrada");
        }

        if (listaSalidas == null || listaSalidas.size() != 1) {
            throw new IllegalArgumentException("La Tarea Splitter debe tener 1 Slot de Salida");
        }
    }

    @Override
    public void ejecutar() {
        while (listaEntradas.get(0).getLongitud() > 0) {
            try {
                // Leemos el Mensaje original
                Mensaje mensaje = listaEntradas.get(0).leerSlot();
                UUID idOriginal = mensaje.getId();      // este será nuestro "id de secuencia único de la lista"
                Document cuerpo = mensaje.getCuerpo();

                // Guardamos el mensaje original en el Almacen
                try {
                    Mensaje copiaOriginal = Mensaje.clonarMensaje(mensaje);
                    // Usamos el 'id' del mensaje como 'idCorrelacion' (coincide con el 'idSecuencia' de la lista de Mensajes)
                    copiaOriginal.setIdCorrelacion(idOriginal);
                    Almacen.getInstancia().guardarMensaje(copiaOriginal);
                    System.out.println("Splitter: Mensaje original guardado en Almacen con idCorrelacion = " + idOriginal);
                } catch (Exception e) {
                    Logger.getLogger(Splitter.class.getName()).log(Level.WARNING, "Splitter: No se pudo guardar el mensaje original en el Almacen", e);
                }

                // Split normal por XPath
                XPathFactory xPathFactory = XPathFactory.newInstance();
                XPath xpath = xPathFactory.newXPath();
                NodeList nodes = (NodeList) xpath.evaluate(expresionXpath, cuerpo, XPathConstants.NODESET);

                System.out.println("Splitter: Dividiendo mensaje en " + nodes.getLength() + " fragmentos");

                for (int i = 0; i < nodes.getLength(); i++) {
                    Node node = nodes.item(i);
                    Mensaje fragmento = crearFragmento(idOriginal, node, i, nodes.getLength());

                    System.out.println("Splitter: Escribiendo fragmento " + (i + 1));
                    listaSalidas.get(0).escribirSlot(fragmento);
                }

            } catch (XPathExpressionException ex) {
                Logger.getLogger(Splitter.class.getName()).log(Level.SEVERE,
                        "Splitter: Error en expresión XPath: " + expresionXpath, ex);
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(Splitter.class.getName()).log(Level.SEVERE,
                        "Splitter: Error general en Splitter", ex);
            }
        }
    }
    
    private Mensaje crearFragmento(UUID idSecuenciaLista, Node nodo, int posicion, int totalFragmentos) throws ParserConfigurationException {
        // Creamos un nuevo documento para el fragmento
        Document nuevoDoc = javax.xml.parsers.DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .newDocument();

        // Importamos el nodo al nuevo documento
        Node importado = nuevoDoc.importNode(nodo, true);
        nuevoDoc.appendChild(importado);

        // Creamos el Mensaje fragmento
        Mensaje fragmento = new Mensaje(nuevoDoc);
        fragmento.setId(UUID.randomUUID());
        // Utilizamos el 'idSecuencia' de la lista de Mensajes como 'idCorrelacion' del Mensaje
        fragmento.setIdCorrelacion(idSecuenciaLista);
        fragmento.setIdSecuencia(posicion);
        fragmento.setLongitud(totalFragmentos);

        return fragmento;
    }
}

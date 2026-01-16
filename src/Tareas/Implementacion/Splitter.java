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

    // ELIMINAMOS ejecutar() y PONEMOS procesarMensaje()
    @Override
    public void procesarMensaje() {
        // 1. LEER (Bloqueante): El hilo se duerme aquí hasta que llegue un mensaje
        Mensaje mensaje = listaEntradas.get(0).leerSlot();

        // Si nos devuelven null es que el hilo se ha interrumpido o parado
        if (mensaje == null) {
            return;
        }

        try {
            UUID idOriginal = mensaje.getId();
            Document cuerpo = mensaje.getCuerpo();

            // Guardamos original en Almacen
            try {
                Mensaje copiaOriginal = Mensaje.clonarMensaje(mensaje);
                copiaOriginal.setIdCorrelacion(idOriginal);
                Almacen.getInstancia().guardarMensaje(copiaOriginal);
                System.out.println("Splitter: Mensaje original guardado en Almacen con idCorrelacion = " + idOriginal);
            } catch (Exception e) {
                Logger.getLogger(Splitter.class.getName()).log(Level.WARNING, "Splitter: No se pudo guardar original", e);
            }

            // Lógica de Split
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
            Logger.getLogger(Splitter.class.getName()).log(Level.SEVERE, "Splitter: Error XPath", ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Splitter.class.getName()).log(Level.SEVERE, "Splitter: Error Parser", ex);
        }
    }

    private Mensaje crearFragmento(UUID idSecuenciaLista, Node nodo, int posicion, int totalFragmentos) throws ParserConfigurationException {
        Document nuevoDoc = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Node importado = nuevoDoc.importNode(nodo, true);
        nuevoDoc.appendChild(importado);

        Mensaje fragmento = new Mensaje(nuevoDoc);
        fragmento.setId(UUID.randomUUID());
        fragmento.setIdCorrelacion(idSecuenciaLista);
        fragmento.setIdSecuencia(posicion);
        fragmento.setLongitud(totalFragmentos);

        return fragmento;
    }
}

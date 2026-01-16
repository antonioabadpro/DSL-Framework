package Tareas.Implementacion;

import Mensajes.Mensaje;
import Slots.Slot;
import Tareas.Tarea;
import Tareas.Tipo;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ContextEnricher extends Tarea {

    public ContextEnricher(ArrayList<Slot> entradas, ArrayList<Slot> salidas) {
        super(entradas, salidas, Tipo.MODIFICADORA);

        if (listaEntradas == null || listaEntradas.size() != 2) {
            throw new IllegalArgumentException("La Tarea ContextEnricher debe tener 2 Slots de Entrada");
        }
        if (listaSalidas == null || listaSalidas.size() != 1) {
            throw new IllegalArgumentException("La Tarea ContextEnricher debe tener 1 Slot de Salida");
        }
    }

    @Override
    public void procesarMensaje() {
        // 1. Leemos el mensaje base (Bloqueante)
        Mensaje mensajeBase = listaEntradas.get(0).leerSlot();
        if (mensajeBase == null) {
            return;
        }

        // 2. Leemos el mensaje de enriquecimiento (Bloqueante)
        // El hilo se quedará aquí parado hasta que la BD responda
        Mensaje mensajeEnriquecido = listaEntradas.get(1).leerSlot();
        if (mensajeEnriquecido == null) {
            return;
        }

        try {
            Document docBase = mensajeBase.getCuerpo();
            Document docEnriquecido = mensajeEnriquecido.getCuerpo();

            // Fusionamos
            fusionarNodos(docBase.getDocumentElement(), docEnriquecido.getDocumentElement());

            // Lógica de stock (true/false)
            Element raiz = docBase.getDocumentElement();
            boolean disponible = false;

            Element fila = buscarElementoPorNombre(raiz, "fila");
            if (fila != null) {
                Element stockElem = buscarElementoPorNombre(fila, "stock");
                if (stockElem != null) {
                    try {
                        int valorStock = Integer.parseInt(stockElem.getTextContent().trim());
                        disponible = valorStock > 0;
                    } catch (NumberFormatException nfe) {
                        disponible = false;
                    }
                }
                raiz.removeChild(fila);
            }

            eliminarElementosPorNombre(raiz, "stock");
            Element nuevoStock = docBase.createElement("stock");
            nuevoStock.setTextContent(String.valueOf(disponible));
            raiz.appendChild(nuevoStock);

            // Preparar salida
            Mensaje mensajeSalida = new Mensaje(docBase);
            mensajeSalida.setId(mensajeBase.getId());
            mensajeSalida.setIdCorrelacion(mensajeBase.getIdCorrelacion());
            mensajeSalida.setIdSecuencia(mensajeBase.getIdSecuencia());
            mensajeSalida.setLongitud(mensajeBase.getLongitud());

            listaSalidas.get(0).escribirSlot(mensajeSalida);
            System.out.println("Enricher: Mensaje fusionado (stock=" + disponible + ")");

        } catch (Exception ex) {
            System.out.println("Enricher: Error durante la fusión: " + ex.getMessage());
        }
    }

    // --- Métodos auxiliares privados (igual que antes) ---
    private void fusionarNodos(Element base, Element enriquecimiento) {
        NodeList hijos = enriquecimiento.getChildNodes();
        for (int i = 0; i < hijos.getLength(); i++) {
            Node hijo = hijos.item(i);
            if (hijo.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) hijo;
                Element elemBase = buscarElementoPorNombre(base, elem.getTagName());
                if (elemBase != null) {
                    fusionarOReemplazar(elemBase, elem);
                } else {
                    añadirElemento(base, elem);
                }
            }
        }
    }

    private void fusionarOReemplazar(Element base, Element nuevo) {
        if (tieneHijosElemento(base) && tieneHijosElemento(nuevo)) {
            fusionarNodos(base, nuevo);
        } else {
            reemplazarElemento(base, nuevo);
        }
    }

    private Element buscarElementoPorNombre(Element padre, String nombre) {
        NodeList hijos = padre.getChildNodes();
        for (int i = 0; i < hijos.getLength(); i++) {
            Node hijo = hijos.item(i);
            if (hijo.getNodeType() == Node.ELEMENT_NODE) {
                if (((Element) hijo).getTagName().equals(nombre)) {
                    return (Element) hijo;
                }
            }
        }
        return null;
    }

    private void eliminarElementosPorNombre(Element padre, String nombre) {
        NodeList hijos = padre.getChildNodes();
        for (int i = hijos.getLength() - 1; i >= 0; i--) {
            Node hijo = hijos.item(i);
            if (hijo.getNodeType() == Node.ELEMENT_NODE && ((Element) hijo).getTagName().equals(nombre)) {
                padre.removeChild(hijo);
            }
        }
    }

    private boolean tieneHijosElemento(Element e) {
        NodeList hijos = e.getChildNodes();
        for (int i = 0; i < hijos.getLength(); i++) {
            if (hijos.item(i).getNodeType() == Node.ELEMENT_NODE) {
                return true;
            }
        }
        return false;
    }

    private void reemplazarElemento(Element viejo, Element nuevo) {
        Node importado = viejo.getOwnerDocument().importNode(nuevo, true);
        viejo.getParentNode().replaceChild(importado, viejo);
    }

    private void añadirElemento(Element padre, Element nuevo) {
        padre.appendChild(padre.getOwnerDocument().importNode(nuevo, true));
    }
}
    
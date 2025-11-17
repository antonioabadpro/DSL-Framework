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

/**
 * Entradas: 2; Salidas: 1
 *  - Entrada 1: mensaje base (<drink> con name/type)
 *  - Entrada 2: resultado BD (<resultados><fila><name>..</name><stock>..</stock></fila></resultados>)
 *  - Salida: <drink> enriquecido con <stock>true/false</stock>
 *
 * Reglas:
 *  - Si stock > 0  -> <stock>true</stock>
 *  - Si stock = 0 -> <stock>false</stock>
 */
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
    public void ejecutar() {
        int procesados = 0;

        // Procesamos todos los pares de Mensajes disponibles (base + enriquecimiento)
        while (!listaEntradas.get(0).estaVacio() && !listaEntradas.get(1).estaVacio()) {
            try {
                // Leemos los Mensajes de ambas entradas
                Mensaje mensajeBase = listaEntradas.get(0).leerSlot();
                Mensaje mensajeEnriquecido = listaEntradas.get(1).leerSlot();

                if (mensajeBase == null || mensajeEnriquecido == null) {
                    break;
                }

                Document docBase = mensajeBase.getCuerpo();
                Document docEnriquecido = mensajeEnriquecido.getCuerpo();

                System.out.println("Enricher: Fusionando mensajes...");

                // Fusionamos los resultados dentro del <drink>
                fusionarNodos(docBase.getDocumentElement(), docEnriquecido.getDocumentElement());

                Element raiz = docBase.getDocumentElement(); // <drink>

                //  Implementamos la logica booleana del stock de los productos
                boolean disponible = false;  // valor por defecto

                // Buscamos la fila dentro de <drink> despues de fusionar los Mensajes
                Element fila = buscarElementoPorNombre(raiz, "fila");
                if (fila != null) {
                    // Buscamos el <stock> dentro de la fila
                    Element stockElem = buscarElementoPorNombre(fila, "stock");
                    if (stockElem != null) {
                        String textoStock = stockElem.getTextContent();
                        try {
                            int valorStock = Integer.parseInt(textoStock.trim());
                            disponible = valorStock > 0;
                        } catch (NumberFormatException nfe) {
                            // Si el Valor NO es numérico lo consideramos como NO disponible
                            disponible = false;
                        }
                    }
                    // Eliminamos la fila para NO ensuciar el mensaje final
                    raiz.removeChild(fila);
                } else {
                    // Si NO hay fila, NO existe en BD o NO hay resultados -> disponible = false
                    disponible = false;
                }

                // Eliminamos posibles <stock> anteriores por si acaso
                eliminarElementosPorNombre(raiz, "stock");

                // Creamos el <stock> booleano directamente despues de <drink>
                Element nuevoStock = docBase.createElement("stock");
                nuevoStock.setTextContent(String.valueOf(disponible)); // "true" o "false"
                raiz.appendChild(nuevoStock);

                // Creamos el Mensaje de salida conservando metadatos del mensaje base
                Mensaje mensajeSalida = new Mensaje(docBase);
                mensajeSalida.setId(mensajeBase.getId());
                mensajeSalida.setIdCorrelacion(mensajeBase.getIdCorrelacion());
                mensajeSalida.setIdSecuencia(mensajeBase.getIdSecuencia());
                mensajeSalida.setLongitud(mensajeBase.getLongitud());

                listaSalidas.get(0).escribirSlot(mensajeSalida);
                procesados++;
                System.out.println("Enricher: Mensaje fusionado enviado a salida (stock=" + disponible + ")");

            } catch (Exception ex) {
                System.out.println("Enricher: Error durante la fusión de mensajes: " + ex.getMessage());
            }
        }

        if (procesados == 0) {
            System.out.println("Enricher: No hay pares completos de mensajes que procesar.");
        } else {
            System.out.println("Enricher: Total de pares procesados = " + procesados);
        }
    }

    /**
     * Fusiona recursivamente los nodos de dos elementos XML.
     * @param nodoBase
     * @param nodoEnriquecimiento Suele ser <resultados>
     */
    private void fusionarNodos(Element nodoBase, Element nodoEnriquecimiento) {
        NodeList hijosEnriquecimiento = nodoEnriquecimiento.getChildNodes();

        for (int i = 0; i < hijosEnriquecimiento.getLength(); i++) {
            Node hijo = hijosEnriquecimiento.item(i);

            if (hijo.getNodeType() == Node.ELEMENT_NODE) {
                Element elemento = (Element) hijo;
                String nombre = elemento.getTagName();

                // Buscar elemento equivalente en el nodo base
                Element elementoBase = buscarElementoPorNombre(nodoBase, nombre);

                if (elementoBase != null) {
                    fusionarOReemplazar(elementoBase, elemento);
                } else {
                    añadirElemento(nodoBase, elemento);
                }
            }
        }
    }

    /**
     * Busca un elemento por nombre entre los hijos directos del padre.
     */
    private Element buscarElementoPorNombre(Element padre, String nombre) {
        NodeList hijos = padre.getChildNodes();

        for (int i = 0; i < hijos.getLength(); i++) {
            Node hijo = hijos.item(i);
            if (hijo.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) hijo;
                if (nombre.equals(e.getTagName())) {
                    return e;
                }
            }
        }
        return null;
    }

    /**
     * Elimina todos los hijos con el nombre introducido por parametro
     * @param padre Padre de referencia para buscar los Nodos Hijos que queremos eliminar
     * @param nombre Nombre de los hijos que queremos eliminar
     */
    private void eliminarElementosPorNombre(Element padre, String nombre) {
        NodeList hijos = padre.getChildNodes();
        // Recorremos al reves para evitar problemas
        for (int i = hijos.getLength() - 1; i >= 0; i--) {
            Node hijo = hijos.item(i);
            if (hijo.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) hijo;
                if (nombre.equals(e.getTagName())) {
                    padre.removeChild(e);
                }
            }
        }
    }

    /**
     * Fusiona o reemplaza elementos según su estructura
     */
    private void fusionarOReemplazar(Element elementoBase, Element elementoEnriquecimiento) {
        if (tieneHijosElemento(elementoBase) && tieneHijosElemento(elementoEnriquecimiento)) {
            fusionarNodos(elementoBase, elementoEnriquecimiento);
        } else {
            reemplazarElemento(elementoBase, elementoEnriquecimiento);
        }
    }

    /**
     * @return Devuelve 'true' si el elemento tiene hijos que también son elementos o 'false' en caso contrario
     */
    private boolean tieneHijosElemento(Element elemento) {
        NodeList hijos = elemento.getChildNodes();
        for (int i = 0; i < hijos.getLength(); i++) {
            if (hijos.item(i).getNodeType() == Node.ELEMENT_NODE) {
                return true;
            }
        }
        return false;
    }

    /**
     * Reemplaza elementoViejo por elementoNuevo en el DOM.
     */
    private void reemplazarElemento(Element elementoViejo, Element elementoNuevo) {
        Element padre = (Element) elementoViejo.getParentNode();
        Node nodoImportado = padre.getOwnerDocument().importNode(elementoNuevo, true);
        padre.replaceChild(nodoImportado, elementoViejo);
    }

    /**
     * Añade elemento como hijo de padre, importándolo al documento de padre.
     */
    private void añadirElemento(Element padre, Element elemento) {
        Node nodoImportado = padre.getOwnerDocument().importNode(elemento, true);
        padre.appendChild(nodoImportado);
    }
}

package Tareas.Implementacion;

import Mensajes.Almacen;
import Mensajes.Mensaje;
import Slots.Slot;
import Tareas.Tarea;
import Tareas.Tipo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Entradas: 1; Salidas: 1
 * Agrupa los Mensajes previamente divididos por el Splitter en base al idCorrelacion de cada Mensaje
 * Recupera el Mensaje original del Almacen para recomponer la estructura del Mensaje dividido
 * Solo agrega al mensaje final los fragmentos cuyo elemento raíz sea el tipo de lista principal (p.ej. <drink>)
 */
public class Aggregator extends Tarea {

    private String etiquetaRaiz;
    private Map<UUID, List<Mensaje>> mapaFragmentos;

    public Aggregator(ArrayList<Slot> entradas, ArrayList<Slot> salidas, String etiquetaRaiz) {
        super(entradas, salidas, Tipo.TRANSFORMADORA);
        this.etiquetaRaiz = etiquetaRaiz;
        this.mapaFragmentos = new HashMap<>();

        if (listaEntradas == null || listaEntradas.size() != 1) {
            throw new IllegalArgumentException("La Tarea Aggregator debe tener 1 Slot de Entrada");
        }

        if (listaSalidas == null || listaSalidas.size() != 1) {
            throw new IllegalArgumentException("La Tarea Aggregator debe tener 1 Slot de Salida");
        }
    }

    @Override
    public void ejecutar() {
        Slot entrada = listaEntradas.get(0);

        while (entrada.getLongitud() > 0) {
            try {
                Mensaje fragmento = entrada.leerSlot();
                UUID idCorrelacion = fragmento.getIdCorrelacion();

                if (idCorrelacion == null) {
                    Logger.getLogger(Aggregator.class.getName()).log(Level.WARNING,
                            "Aggregator: Fragmento sin idCorrelacion, se ignora");
                    continue;
                }

                procesarFragmento(idCorrelacion, fragmento);

            } catch (Exception ex) {
                Logger.getLogger(Aggregator.class.getName()).log(Level.SEVERE,
                        "Aggregator: Error procesando fragmento", ex);
            }
        }
    }

    /**
     * Procesa un fragmento individual y, en caso de tener todos, reconstruye el mensaje completo.
     */
    private void procesarFragmento(UUID idCorrelacion, Mensaje fragmento)
            throws ParserConfigurationException {

        // Obtenemos o creamos la lista de fragmentos para esta correlación
        List<Mensaje> listaFragmentos = mapaFragmentos.get(idCorrelacion);
        if (listaFragmentos == null) {
            listaFragmentos = new ArrayList<>();
            mapaFragmentos.put(idCorrelacion, listaFragmentos);
        }

        // Añadimos el nuevo fragmento al Mensaje
        listaFragmentos.add(fragmento);
        System.out.println("Aggregator: Fragmento agregado. Total para " + idCorrelacion + ": " + listaFragmentos.size());

        // Verificamos si tenemos todos los fragmentos
        int totalEsperado = fragmento.getLongitud();
        if (listaFragmentos.size() == totalEsperado) {
            System.out.println("Aggregator: Todos los fragmentos recibidos -> Reconstruyendo Mensaje");

            // Reconstruimos el Mensaje completo usando el Almacen
            Mensaje mensajeReconstruido = reconstruirMensaje(idCorrelacion, listaFragmentos);
            listaSalidas.get(0).escribirSlot(mensajeReconstruido);
            
            mapaFragmentos.remove(idCorrelacion); // Limpiamos la Cabecera del Mensaje con el 'idCorrelacion' introducido
        }
    }

    /**
     * Busca en el árbol del mensaje original el nodo contenedor de la lista (p.ej. <drinks>) que contiene los elementos fragmentados
     */
    private Node buscarContenedorLista(Element raizOriginal, String nombreElementoFragmento) {
        NodeList hijos = raizOriginal.getChildNodes();
        for (int i = 0; i < hijos.getLength(); i++) {
            Node hijo = hijos.item(i);
            if (hijo.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) hijo;
                if (contieneElemento(elem, nombreElementoFragmento)) {
                    return elem;
                }
            }
        }
        return null;
    }

    private boolean contieneElemento(Element nodo, String nombreElementoFragmento) {
        if (nodo.getTagName().equals(nombreElementoFragmento)) {
            return true;
        }
        NodeList hijos = nodo.getChildNodes();
        for (int i = 0; i < hijos.getLength(); i++) {
            Node hijo = hijos.item(i);
            if (hijo.getNodeType() == Node.ELEMENT_NODE) {
                if (contieneElemento((Element) hijo, nombreElementoFragmento)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determina cuál es el nombre del elemento de lista principal (el más frecuente entre los fragmentos) -> p.ej "drink".
     */
    private String obtenerNombreElementoLista(List<Mensaje> fragmentos) {
        Map<String, Integer> contador = new HashMap<>();

        for (Mensaje f : fragmentos) {
            Document doc = f.getCuerpo();
            if (doc == null || doc.getDocumentElement() == null) {
                continue;
            }
            String etiqueta = doc.getDocumentElement().getTagName();
            contador.put(etiqueta, contador.getOrDefault(etiqueta, 0) + 1);
        }

        // Devolvemos la etiqueta (tag) más frecuente
        String etiquetaMasFrecuente = null;
        int max = 0;
        for (Map.Entry<String, Integer> e : contador.entrySet()) {
            if (e.getValue() > max) {
                max = e.getValue();
                etiquetaMasFrecuente = e.getKey();
            }
        }

        return etiquetaMasFrecuente;
    }

    /**
     * Reconstruye el mensaje original a partir de los fragmentos y la información guardada en el Almacen
     */
    private Mensaje reconstruirMensaje(UUID idCorrelacion, List<Mensaje> fragmentos)
            throws ParserConfigurationException {

        // Ordenamos los fragmentos por 'idSecuencia' (posición de la lista)
        fragmentos.sort((f1, f2) -> Integer.compare(f1.getIdSecuencia(), f2.getIdSecuencia()));

        // Detectamos cuál es el elemento de lista principal (p.ej. "drink")
        String nombreElementoFragmento = obtenerNombreElementoLista(fragmentos);
        if (nombreElementoFragmento == null) {
            // Si NO se puede detectar, ponemos un valor por defecto
            nombreElementoFragmento = "drink";
        }
        
        // Recuperamos el Mensaje original del Almacen
        Almacen almacen = Almacen.getInstancia();
        Mensaje mensajeOriginal = null;
        try {
            List<Mensaje> originales = almacen.obtenerPorCorrelacion(idCorrelacion);
            if (originales != null && !originales.isEmpty()) {
                mensajeOriginal = originales.get(0);
            }
        } catch (Exception e) {
            Logger.getLogger(Aggregator.class.getName()).log(Level.WARNING,
                    "Aggregator: No se pudo recuperar el mensaje original del Almacen para " + idCorrelacion, e);
        }

        // Creamos el documento raíz para el mensaje reconstruido
        Document docReconstruido = javax.xml.parsers.DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .newDocument();

        Element raiz;

        if (mensajeOriginal != null && mensajeOriginal.getCuerpo() != null) {
            // Usamos la estructura del mensaje original como base
            Document docOriginal = mensajeOriginal.getCuerpo();
            Element raizOriginal = docOriginal.getDocumentElement();

            String nombreRaiz = (etiquetaRaiz != null && !etiquetaRaiz.isEmpty())
                    ? etiquetaRaiz
                    : raizOriginal.getTagName();

            raiz = docReconstruido.createElement(nombreRaiz);
            docReconstruido.appendChild(raiz);

            // Creamos el contenedor original de la lista de Mensajes (p.ej. <drinks>)
            Node contenedorLista = buscarContenedorLista(raizOriginal, nombreElementoFragmento);

            // Copiamos todos los hijos de la raíz original que NO pertenezcan al contenedor de la lista
            NodeList hijosOriginal = raizOriginal.getChildNodes();
            for (int i = 0; i < hijosOriginal.getLength(); i++) {
                Node hijo = hijosOriginal.item(i);
                if (hijo.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                if (hijo == contenedorLista) {
                    continue;
                }
                Node importado = docReconstruido.importNode(hijo, true);
                raiz.appendChild(importado);
            }

        } else {
            raiz = docReconstruido.createElement(etiquetaRaiz);
            docReconstruido.appendChild(raiz);
        }
        
        // Combinamos solo los fragmentos que sean del tipo de lista
        for (Mensaje fragmento : fragmentos) {
            Document cuerpoFragmento = fragmento.getCuerpo();
            if (cuerpoFragmento == null || cuerpoFragmento.getDocumentElement() == null) {
                continue;
            }

            String tag = cuerpoFragmento.getDocumentElement().getTagName();

            // Filtramos los que NO sean del tipo de lista (p.ej. <resultados>)
            if (!tag.equals(nombreElementoFragmento)) {
                Logger.getLogger(Aggregator.class.getName()).log(
                        Level.INFO,
                        "Aggregator: Ignorando fragmento con raíz <" + tag + "> para correlacion " + idCorrelacion
                );
                continue;
            }

            Node nodoImportado = docReconstruido.importNode(cuerpoFragmento.getDocumentElement(), true);
            raiz.appendChild(nodoImportado);
        }
        
        // Creamos el mensaje reconstruido
        Mensaje mensajeReconstruido = new Mensaje(docReconstruido);

        if (mensajeOriginal != null) {
            // Restauramos el 'id' y el 'idCorrelacion' usado como clave en el Almacen
            mensajeReconstruido.setId(mensajeOriginal.getId());
            mensajeReconstruido.setIdCorrelacion(idCorrelacion);
        } else {
            mensajeReconstruido.setId(idCorrelacion);
            mensajeReconstruido.setIdCorrelacion(idCorrelacion);
        }

        mensajeReconstruido.setLongitud(1);

        System.out.println("Aggregator: Mensaje reconstruido con correlacion " + idCorrelacion);

        return mensajeReconstruido;
    }
}

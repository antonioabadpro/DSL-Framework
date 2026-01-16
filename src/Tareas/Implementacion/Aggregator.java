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

public class Aggregator extends Tarea {

    private String etiquetaRaiz;
    private Map<UUID, List<Mensaje>> mapaFragmentos;

    public Aggregator(ArrayList<Slot> entradas, ArrayList<Slot> salidas, String etiquetaRaiz) {
        super(entradas, salidas, Tipo.TRANSFORMADORA);
        this.etiquetaRaiz = etiquetaRaiz;
        this.mapaFragmentos = new HashMap<>();

        if (listaEntradas == null || listaEntradas.size() != 1) {
            throw new IllegalArgumentException("Aggregator debe tener 1 Slot de Entrada");
        }
        if (listaSalidas == null || listaSalidas.size() != 1) {
            throw new IllegalArgumentException("Aggregator debe tener 1 Slot de Salida");
        }
    }

    @Override
    public void procesarMensaje() {
        Mensaje fragmento = listaEntradas.get(0).leerSlot();
        if (fragmento == null) {
            return;
        }

        UUID idCorrelacion = fragmento.getIdCorrelacion();
        if (idCorrelacion == null) {
            return;
        }

        try {
            List<Mensaje> lista = mapaFragmentos.computeIfAbsent(idCorrelacion, k -> new ArrayList<>());
            lista.add(fragmento);

            System.out.println("Aggregator: Fragmento recibido (" + lista.size() + "/" + fragmento.getLongitud() + ")");

            if (lista.size() == fragmento.getLongitud()) {
                System.out.println("Aggregator: Reconstruyendo Mensaje...");
                Mensaje reconstruido = reconstruirMensaje(idCorrelacion, lista);
                listaSalidas.get(0).escribirSlot(reconstruido);
                mapaFragmentos.remove(idCorrelacion);
            }
        } catch (Exception ex) {
            Logger.getLogger(Aggregator.class.getName()).log(Level.SEVERE, "Error aggregator", ex);
        }
    }

    private Mensaje reconstruirMensaje(UUID idCorrelacion, List<Mensaje> fragmentos) throws ParserConfigurationException {
        fragmentos.sort((f1, f2) -> Integer.compare(f1.getIdSecuencia(), f2.getIdSecuencia()));

        String nombreFragmento = "drink";
        for (Mensaje f : fragmentos) {
            if (f.getCuerpo() != null && f.getCuerpo().getDocumentElement() != null) {
                nombreFragmento = f.getCuerpo().getDocumentElement().getTagName();
                break;
            }
        }

        Almacen almacen = Almacen.getInstancia();
        Mensaje original = null;
        try {
            List<Mensaje> originales = almacen.obtenerPorCorrelacion(idCorrelacion);
            if (originales != null && !originales.isEmpty()) {
                original = originales.get(0);
            }
        } catch (Exception e) {
        }

        Document doc = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element raiz;
        Element contenedor = null;
        String nombreContenedorEsperado = null;

        if (original != null && original.getCuerpo() != null) {
            Element raizOriginal = original.getCuerpo().getDocumentElement();
            String nombreRaiz = (etiquetaRaiz != null && !etiquetaRaiz.isEmpty()) ? etiquetaRaiz : raizOriginal.getTagName();
            raiz = doc.createElement(nombreRaiz);
            doc.appendChild(raiz);

            NodeList candidatos = raizOriginal.getElementsByTagName(nombreFragmento);
            if (candidatos.getLength() > 0) {
                Node padre = candidatos.item(0).getParentNode();
                if (padre != null && padre.getNodeType() == Node.ELEMENT_NODE && !padre.isSameNode(raizOriginal)) {
                    nombreContenedorEsperado = padre.getNodeName();
                }
            }

            NodeList hijos = raizOriginal.getChildNodes();
            for (int i = 0; i < hijos.getLength(); i++) {
                Node hijo = hijos.item(i);
                if (hijo.getNodeType() != Node.ELEMENT_NODE) {
                    raiz.appendChild(doc.importNode(hijo, true));
                    continue;
                }
                if (nombreContenedorEsperado != null && hijo.getNodeName().equals(nombreContenedorEsperado)) {
                    contenedor = doc.createElement(nombreContenedorEsperado);
                    raiz.appendChild(contenedor);
                    continue;
                }
                raiz.appendChild(doc.importNode(hijo, true));
            }
        } else {
            raiz = doc.createElement(etiquetaRaiz);
            doc.appendChild(raiz);
        }

        if (contenedor == null) {
            contenedor = raiz;
        }

        for (Mensaje f : fragmentos) {
            if (f.getCuerpo() == null) {
                continue;
            }
            String tag = f.getCuerpo().getDocumentElement().getTagName();
            if (tag.equals(nombreFragmento)) {
                contenedor.appendChild(doc.importNode(f.getCuerpo().getDocumentElement(), true));
            }
        }

        Mensaje msjFinal = new Mensaje(doc);
        if (original != null) {
            msjFinal.setId(original.getId());
            msjFinal.setIdCorrelacion(idCorrelacion);
        } else {
            msjFinal.setId(idCorrelacion);
            msjFinal.setIdCorrelacion(idCorrelacion);
        }
        msjFinal.setLongitud(1);
        return msjFinal;
    }
}

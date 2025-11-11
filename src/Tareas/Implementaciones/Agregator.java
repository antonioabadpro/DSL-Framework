package Tareas.Implementaciones;

import Puertos.Slot;
import Mensajes.Mensaje; // CAMBIO: Importar Mensaje
import Mensajes.AlmacenMensajes; // CAMBIO: Importar Almacén
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 *
 * @author Sergio
 */
public class Agregator extends Tarea{
    private String etiquetaGrupo;   // Ej: "drinks"
    private String etiquetaElemento; // Ej: "drink"
    private String etiquetaId;      // Ej: "order_id"
    
    // CAMBIO: El constructor se simplifica, ya no necesita la lista de nombreTags
    public Agregator(ArrayList<Slot> entradas, ArrayList<Slot> salidas, String etiquetaGrupo, String etiquetaElemento, 
            String etiquetaId) {
        
        super(entradas, salidas, TipoTarea.TRANSFORMADORAS);
        
        // Limitar a 1 entrada y 1 salida
        ArrayList<Slot> nuevaEntradas = new ArrayList<>();
        nuevaEntradas.add(entradas.getFirst());
        this.setEntradas(nuevaEntradas);
        
        ArrayList<Slot> nuevaSalidas = new ArrayList<>();
        nuevaSalidas.add(salidas.getFirst());
        this.setSalidas(nuevaSalidas);
        
        this.etiquetaGrupo = etiquetaGrupo;
        this.etiquetaElemento = etiquetaElemento;
        this.etiquetaId = etiquetaId;
    }

    // <editor-fold defaultstate="collapsed" desc="Getters & Setters">
        public String getEtiquetaGrupo() {
        return etiquetaGrupo;
    }

    public void setEtiquetaGrupo(String etiquetaGrupo) {
        this.etiquetaGrupo = etiquetaGrupo;
    }

    public String getEtiquetaElemento() {
        return etiquetaElemento;
    }

    public void setEtiquetaElemento(String etiquetaElemento) {
        this.etiquetaElemento = etiquetaElemento;
    }

    public String getEtiquetaId() {
        return etiquetaId;
    }

    public void setEtiquetaId(String etiquetaId) {
        this.etiquetaId = etiquetaId;
    }
    // </editor-fold>


    
    
    @Override
    public void ejecutar() {
        Slot entrada = this.getEntradas().getFirst();
        Slot salida = this.getSalidas().getFirst();

        // CAMBIO: Procesamos todos los mensajes disponibles en la entrada
        while (!entrada.estaVacio()) {
            Mensaje msgEntrada = entrada.leer();
            if (msgEntrada == null) continue;

            Map<String, Object> cabecera = msgEntrada.getCabecera();

            // CAMBIO: Comprobar si el mensaje tiene la información de secuencia
            if (!cabecera.containsKey("id_secuencia") || 
                !cabecera.containsKey("tamanio_secuencia") ||
                !cabecera.containsKey("posicion_secuencia")) {
                
                System.out.println("Agregator: Mensaje recibido sin metadatos de secuencia. Ignorando.");
                continue;
            }

            // CAMBIO: Usar metadatos para gestionar el estado
            String idSecuencia = (String) cabecera.get("id_secuencia");
            int total = (Integer) cabecera.get("tamanio_secuencia");
            String claveAlmacen = "agregador_" + idSecuencia;

            // --- Lógica de Almacén Sincronizada ---
            List<Mensaje> partes;
            
            // Usamos 'almacen' (de Tarea) como monitor para sincronizar
            // Esto evita problemas si dos tareas Agregator (en hilos distintos)
            // intentan modificar la misma lista al mismo tiempo.
            synchronized (almacen) {
                partes = (List<Mensaje>) almacen.obtener(claveAlmacen);
                
                if (partes == null) {
                    // Si no existe la lista, la creamos y la guardamos
                    partes = Collections.synchronizedList(new ArrayList<>());
                    almacen.guardar(claveAlmacen, partes);
                }
            }
            
            // Añadimos la parte actual a la lista
            partes.add(msgEntrada);
            // --- Fin Lógica de Almacén ---

            // CAMBIO: Comprobar si la secuencia está COMPLETA
            if (partes.size() == total) {
                // ¡Completa! Eliminar del almacén y procesar
                almacen.eliminar(claveAlmacen);

                try {
                    // Construir el documento final
                    Document docFinal = construirDocumentoAgregado(partes, idSecuencia);
                    
                    // Crear el mensaje de salida
                    Map<String, Object> cabeceraSalida = new HashMap<>();
                    cabeceraSalida.put("id_agregado", idSecuencia);
                    Mensaje msgSalida = new Mensaje(cabeceraSalida, docFinal);
                    
                    salida.escribir(msgSalida);
                    
                } catch (Exception e) {
                    System.out.println("Agregator: Error al construir el documento agregado: " + e.getMessage());
                    // Opcional: Volver a guardar las partes en el almacén para reintentar
                    // almacen.guardar(claveAlmacen, partes);
                }
            }
            // Si no está completa, las partes simplemente se quedan en el almacén
        }
    }
    
    /**
     * CAMBIO: Método helper para construir el XML agregado a partir de las partes.
     */
    private Document construirDocumentoAgregado(List<Mensaje> partes, String idSecuencia) throws ParserConfigurationException {
        
        DocumentBuilderFactory fabricaDoc = DocumentBuilderFactory.newDefaultInstance();
        DocumentBuilder builder = fabricaDoc.newDocumentBuilder();
        Document docFinal = builder.newDocument();

        // Crear elemento raiz (ej: "order")
        Element raiz = docFinal.createElement("order"); // Asumimos 'order' como raíz genérica
        docFinal.appendChild(raiz);
        
        // Crear elemento id (ej: "order_id")
        Element id = docFinal.createElement(etiquetaId);
        id.setTextContent(idSecuencia);
        raiz.appendChild(id);
        
        // Crear etiqueta grupo (ej: "drinks")
        Element grupo = docFinal.createElement(etiquetaGrupo);
        raiz.appendChild(grupo);
        
        // CAMBIO: Ordenar las partes por su posicion_secuencia (IMPORTANTE)
        partes.sort((m1, m2) -> {
            Integer pos1 = (Integer) m1.getHeader("posicion_secuencia");
            Integer pos2 = (Integer) m2.getHeader("posicion_secuencia");
            return pos1.compareTo(pos2);
        });

        // CAMBIO: Añadir cada parte al grupo
        for (Mensaje msgParte : partes) {
            // El cuerpo de cada parte es el <drink>, <item>, etc.
            // (Gracias al nuevo Splitter)
            Node nodoElemento = msgParte.getCuerpo().getDocumentElement();
            
            // Importar el nodo al nuevo documento
            Node nodoImportado = docFinal.importNode(nodoElemento, true);
            
            // Re-etiquetar el nodo con el nombre de elemento configurado (ej: "drink")
            Element elementoAgregado = docFinal.createElement(etiquetaElemento);
            
            // Copiar hijos del nodo importado al nuevo elemento
            while (nodoImportado.hasChildNodes()) {
                elementoAgregado.appendChild(nodoImportado.getFirstChild());
            }
            
            grupo.appendChild(elementoAgregado);
        }
        
        return docFinal;
    }
}
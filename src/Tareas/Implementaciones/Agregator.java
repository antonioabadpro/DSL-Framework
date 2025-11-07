package Tareas.Implementaciones;

import Puertos.Slot;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 *
 * @author Sergio
 */
public class Agregator extends Tarea{
    private String etiquetaGrupo;   // Ej: "drinks", "productos", "items"
    private String etiquetaElemento; // Ej: "drink", "producto", "item"
    private String etiquetaId;      // Ej: "order_id", "pedido_id"
    private List<String> nombreTags; //Nombres de los atributos que tendrán cada elemeno: "nombre", "tipo", "stock"
    
    public Agregator(ArrayList<Slot> entradas, ArrayList<Slot> salidas, String etiquetaGrupo, String etiquetaElemento, 
            String etiquetaId, List<String> nombreTags) {
        //Definimos entrada y salida (1 entrada y 1 salida)
        ArrayList<Slot> nuevaEntradas = new ArrayList();
        nuevaEntradas.add(entradas.getFirst());
        this.setEntradas(nuevaEntradas);
        
        ArrayList<Slot> nuevaSalidas = new ArrayList();
        nuevaSalidas.add(salidas.getFirst());
        this.setSalidas(nuevaSalidas);
        
        this.setTipo(TipoTarea.TRANSFORMADORAS);
        
        this.etiquetaGrupo = etiquetaGrupo;
        this.etiquetaElemento = etiquetaElemento;
        this.etiquetaId = etiquetaId;
        this.nombreTags = nombreTags;
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

    public List<String> getNombreTags() {
        return nombreTags;
    }
    
    public void setNombreTags(List<String> nombreTags) {
        this.nombreTags = nombreTags;
    }

    // </editor-fold>
    
    
    @Override
    public void ejecutar() {
        //Crear un parser de documentos
        DocumentBuilderFactory fabricaDoc = DocumentBuilderFactory.newDefaultInstance();
        DocumentBuilder construyeDoc = null;
        try {
            construyeDoc = fabricaDoc.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            System.out.println("Agregator: No es posible crear el DocumentBuilder: " + ex);
        }
        
        //Crear Documento
        Document documento = construyeDoc.newDocument();
        
        //Crear elemento raiz
        Element raiz = documento.createElement("order");
        documento.appendChild(raiz);
        
        //Crear elemento id
        Element id = documento.createElement(etiquetaId);
        Text NodoId = documento.createTextNode(id.toString());
        documento.appendChild(NodoId);
        raiz.appendChild(id);
        
        //Crear etiqueta elemento
        Element elemento = documento.createElement(etiquetaGrupo);
        raiz.appendChild(elemento);
        
        int numeroElementosSlot = this.getEntradas().getFirst().getQueue().size();
        
        for (int i = 0; i < numeroElementosSlot; i++) {
            Document mensajeEntrada;
            try {
                mensajeEntrada = this.getEntradas().getFirst().leer();
            } catch (Exception ex) {
                System.out.println("Agregator: No se ha podido leer el mensaje "+i+": "+ex);
                continue;
            }
            
            if(mensajeEntrada != null){
                mensajeEntrada.getDocumentElement().normalize();
                
                //extraemos los nombres de las etiquetas
                int numTags = this.nombreTags.size();//Numero de atributos que hay en cada subElemento
                List<NodeList> listasNodos = new ArrayList();
                List<String> nombreEtiquetas = new ArrayList();
                for (int j = 0; j < numTags; j++) {
                    listasNodos.add(mensajeEntrada.getElementsByTagName(this.nombreTags.get(j))); //Obtenemos el nodeList de cada Atributo por su nombre de Tag
                    nombreEtiquetas.add(listasNodos.getLast().item(0).getTextContent()); //Obtenemos el nombre de cada atributo
                }
                
                //Creamos subElemento
                Element subElemento = documento.createElement(etiquetaElemento);
                elemento.appendChild(subElemento);
                
                //Añadimos los atributos al subElemento
                for (int j = 0; j < numTags; j++) {
                    Element atributo = documento.createElement(nombreEtiquetas.get(j)); //Obtenemos el nombre de la etiqueta
                    Text NombreNodo = documento.createTextNode(nombreEtiquetas.get(j)); //Obtenemos el nodo
                    atributo.appendChild(NombreNodo);
                    subElemento.appendChild(atributo);
                }
                
                //Escribimos en el Slot de Salida
                try {
                    this.getSalidas().getFirst().escribir(documento);
                } catch (Exception ex) {
                    System.out.println("Agreggator: No se pudo escribir en el Slot de salida: "+ex);
                }
            }else{
                System.out.println("El mensaje "+i+" es nulo");
            }
            
        }
    }
    
}

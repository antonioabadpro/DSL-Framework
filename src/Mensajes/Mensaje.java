package Mensajes;

import java.util.HashMap;
import java.util.UUID;
import java.util.Map;
import org.w3c.dom.Document;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;
import java.io.StringWriter;

/**
 * 
 */
public class Mensaje {

    // Cabecera del mensaje
    private Map<String, Object> cabecera;

    // Cuerpo del mensaje
    private Document cuerpo;

    // Contructores
    /**
     * Constructor de un Mensaje solo con Cuerpo (sin Cabecera)
     * @param cuerpo Cuerpo del Mensaje
     */    
    public Mensaje(Document cuerpo) {
        this.cabecera = new HashMap<>();
        this.cabecera.put("id", UUID.randomUUID());
        this.cabecera.put("idCorrelacion", UUID.randomUUID());
        this.cabecera.put("idSecuencia", 0);  // en vez de null
        this.cabecera.put("longitud", 0);
        this.cuerpo = cuerpo;
    }

    /**
     * Constructor de un Mensaje con Cuerpo y con id, idSecuencia
     * @param cuerpo Cuerpo del Mensaje
     * @param id id del Mensaje situado en la Cabecera
     */
    public Mensaje(Document cuerpo, UUID id) {
        this.cabecera = new HashMap<>();
        this.cabecera.put("id", id);
        this.cabecera.put("idCorrelacion", UUID.randomUUID());
        this.cabecera.put("idSecuencia", 0);
        this.cabecera.put("longitud", 0);
        this.cuerpo = cuerpo;
    }
    
    /**
     * Constructor de Copia
     * @param mensajeCompleto Mensaje con Cabecera y Cuerpo
     */
    public Mensaje(Mensaje mensajeCompleto) {
        this.cabecera = new HashMap<>(mensajeCompleto.cabecera);
        this.cuerpo = mensajeCompleto.cuerpo;
    }

    // Getters y Setters
    public UUID getId() {
        return (UUID) cabecera.get("id");
    }

    public UUID getIdCorrelacion() {
        return (UUID) cabecera.get("idCorrelacion");
    }
    
    public int getIdSecuencia() {
        Integer valor = (Integer) cabecera.get("idSecuencia");
        return (valor != null) ? valor : 0;  // o -1 si prefieres indicar “sin secuencia”
    }

    public int getLongitud() {
        Integer valor = (Integer) cabecera.get("longitud");
        return (valor != null) ? valor : 0;
    }

    public Map<String, Object> getCabecera() {
        return cabecera;
    }

    public Document getCuerpo() {
        return cuerpo;
    }

    public void setId(UUID id) {
        this.cabecera.put("id", id);
    }

    public void setIdCorrelacion(UUID idCorrelacion) {
        this.cabecera.put("idCorrelacion", idCorrelacion);
    }

    public void setIdSecuencia(int idSecuencia) {
        this.cabecera.put("idSecuencia", idSecuencia);
    }

    public void setLongitud(int longitud) {
        this.cabecera.put("longitud", longitud);
    }

    public void setCuerpo(Document cuerpo) {
        this.cuerpo = cuerpo;
    }

    /**
     * Metodo para duplicar/clonar un Mensaje utilizado en la Tarea Replicator
     * @param msj Mensaje que queremos duplicar/clonar
     * @return Devuelve el clon del Mensaje
     * @throws Exception Lanza una Excepcion si el Mensaje que queremos clonar/duplicar está vacío
     */
    public static Mensaje clonarMensaje(Mensaje msj) throws Exception {
        if (msj == null) {
            throw new Exception("\nError en 'clonarMensaje()': El Mensaje que queremos clonar esta vacio (es null)");
        }

        Mensaje msjClonado = new Mensaje(msj);
        return msjClonado;
    }

    /**
     * @return Devuelve el Mensaje con un formato personalizado
     */
    @Override
    public String toString() {
        String cuerpoStr;
        try {
            if (cuerpo != null) {

                // Formatear xml para que sea legible.
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

                StringWriter writer = new StringWriter();
                transformer.transform(new DOMSource(cuerpo), new StreamResult(writer));
                cuerpoStr = writer.toString();

            } else {
                cuerpoStr = "null";
            }
        } catch (Exception e) {
            cuerpoStr = "[Error al convertir el cuerpo XML: " + e.getMessage() + "]";
        }

        return "Mensaje ["
                + "id=" + getId()
                + ", idCorrelacion=" + getIdCorrelacion()
                + ", idSecuencia=" + getIdSecuencia()
                + ", longitud=" + getLongitud()
                + ", cuerpo=\n" + cuerpoStr
                + ']';
    }

}

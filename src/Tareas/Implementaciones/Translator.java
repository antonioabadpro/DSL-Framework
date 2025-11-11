package Tareas.Implementaciones;

import Puertos.Slot;
import Mensajes.Mensaje; // CAMBIO: Importar Mensaje
import java.io.File;
import java.util.ArrayList;
import java.util.Map; // CAMBIO: Importar Map para la cabecera
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;

/**
 *
 * @author Sergio
 */
public class Translator extends Tarea {

    private Source xslt;
    private Transformer transformador;

    public Translator(ArrayList<Slot> entradas, ArrayList<Slot> salidas, String archivoXSLT) {
        // CAMBIO: Asegurarse de que el constructor base se llame correctamente
        super(entradas, salidas, TipoTarea.TRANSFORMADORAS);
        
        // Limitar a 1 entrada y 1 salida
        ArrayList<Slot> nuevaEntradas = new ArrayList<>();
        nuevaEntradas.add(entradas.getFirst());
        this.setEntradas(nuevaEntradas);
        
        ArrayList<Slot> nuevaSalidas = new ArrayList<>();
        nuevaSalidas.add(salidas.getFirst());
        this.setSalidas(nuevaSalidas);

        TransformerFactory fabrica = TransformerFactory.newInstance();
        this.xslt = new StreamSource(new File(archivoXSLT));
        try {
            this.transformador = fabrica.newTransformer(xslt);
        } catch (TransformerConfigurationException ex) {
            System.out.println("Translator: Error al crear el transformador "+ ex);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Getter & Setters">
    public Source getXslt() {
        return xslt;
    }

    public void setXslt(Source xslt) {
        this.xslt = xslt;
    }

    public Transformer getTransformador() {
        return transformador;
    }

    public void setTransformador(Transformer transformador) {
        this.transformador = transformador;
    }
    // </editor-fold>
    
    @Override
    public void ejecutar() {
        Slot entrada = this.getEntradas().getFirst(); 
        Slot salida = this.getSalidas().getFirst();
        
        // CAMBIO: Usar el nuevo método estaVacio() del Slot
        while (!entrada.estaVacio()) {

            // CAMBIO: leer() ahora devuelve Mensaje y no lanza excepción
            Mensaje mensajeEntrada = entrada.leer();

            if (mensajeEntrada != null) {
                try {
                    // CAMBIO: Obtener el cuerpo y la cabecera del mensaje
                    Document docEntrada = mensajeEntrada.getCuerpo();
                    Map<String, Object> cabeceraOriginal = mensajeEntrada.getCabecera();

                    // Lógica de transformación original
                    Source fuente = new DOMSource(docEntrada);
                    DOMResult resultado = new DOMResult();
                    this.transformador.transform(fuente, resultado);

                    // CAMBIO: Este es el nuevo cuerpo
                    Document mensajeSalida = (Document) resultado.getNode();

                    // CAMBIO: Crear un nuevo Mensaje preservando la cabecera original
                    Mensaje msgSalida = new Mensaje(cabeceraOriginal, mensajeSalida);
                    
                    // CAMBIO: escribir() toma un Mensaje y no lanza excepción
                    salida.escribir(msgSalida);
                    
                } catch (TransformerException ex) {
                    System.out.println("Translator: Error al traducir MensajeEntrada: " + ex);
                } catch (Exception ex) {
                    System.out.println("Translator: Error al escribir: " + ex);
                }
            } else {
                System.out.println("Translator: Se leyo un null del slot de entrada");
            }
        }
    }
}
package Tareas.Implementaciones;

import Puertos.Slot;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;

/**
 *
 * @author Sergio
 */
public class Translator extends Tarea {

    private Source xslt;
    private Transformer transformador;

    public Translator(ArrayList<Slot> entradas, ArrayList<Slot> salidas, String archivoXSLT) throws TransformerConfigurationException {
        ArrayList<Slot> nuevaEntradas = new ArrayList();
        nuevaEntradas.add(entradas.getFirst());
        this.setEntradas(nuevaEntradas);
        ArrayList<Slot> nuevaSalidas = new ArrayList();
        nuevaSalidas.add(salidas.getFirst());
        this.setSalidas(nuevaSalidas);
        this.setTipo(TipoTarea.TRANSFORMADORAS);

        TransformerFactory fabrica = TransformerFactory.newInstance();
        this.xslt = new StreamSource(new File(archivoXSLT));
        this.transformador = fabrica.newTransformer(xslt);
    }

    // <editor-fold defaultstate="colapsed" desc="Getter & Setters">
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
        Slot entrada = this.getEntradas().getFirst(); //Obtengo solo 1 Slot. Translator solo tiene 1 entrada
        Slot salida = this.getSalidas().getFirst();
        int t = entrada.getQueue().size();

        for (int i = 0; i < t; i++) {

            //Obtengo el primer mensaje del slot
            Document mensajeEntrada;
            try {
                mensajeEntrada = entrada.leer();
            } catch (Exception ex) {
                System.out.println("Transalor: Error al leer: " + ex);
                continue; //Saltamos a la siguiente iteración si no ha podido leer
            }

            if (mensajeEntrada != null) {
                //Defino las fuentes de donde se va a leer y donde se va a escribir
                Source fuente = new DOMSource(mensajeEntrada);
                DOMResult resultado = new DOMResult();

                try {
                    this.transformador.transform(fuente, resultado);
                } catch (TransformerException ex) {
                    System.out.println("Translator: Error al traducir MensajeEntrada " + i + ": " + ex);
                }

                Document mensajeSalida = (Document) resultado.getNode();
                try {
                    salida.escribir(mensajeSalida);
                } catch (Exception ex) {
                    System.out.println("Translator: Error al salir ");
                }
            } else {
                System.out.println("Translator: Se leyo un null del slot de entrada");
            }
        }
    }

}

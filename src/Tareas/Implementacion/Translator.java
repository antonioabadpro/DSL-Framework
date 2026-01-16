package Tareas.Implementacion;

import Mensajes.Mensaje;
import Slots.Slot;
import Tareas.Tarea;
import Tareas.Tipo;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class Translator extends Tarea {

    private String rutaArchivoXSLT;

    public Translator(ArrayList<Slot> entradas, ArrayList<Slot> salidas, String rutaArchivoXSLT) {
        super(entradas, salidas, Tipo.TRANSFORMADORA);
        this.rutaArchivoXSLT = rutaArchivoXSLT;

        if (listaEntradas == null || listaEntradas.size() != 1) {
            throw new IllegalArgumentException("La Tarea Translator debe tener 1 Slot de Entrada");
        }
        if (listaSalidas == null || listaSalidas.size() != 1) {
            throw new IllegalArgumentException("La Tarea Translator debe tener 1 Slot de Salida");
        }
        if (rutaArchivoXSLT == null || rutaArchivoXSLT.trim().isEmpty()) {
            throw new IllegalArgumentException("La Tarea Translator requiere una ruta de archivo XSLT válida");
        }
    }

    @Override
    public void procesarMensaje() {
        // 1. Lectura bloqueante
        Mensaje mensajeEntrada = listaEntradas.get(0).leerSlot();
        if (mensajeEntrada == null) {
            return;
        }

        try {
            Document documentoTransformado = transformarDocumento(mensajeEntrada.getCuerpo());
            Mensaje mensajeSalida = crearMensajeSalida(mensajeEntrada, documentoTransformado);

            listaSalidas.get(0).escribirSlot(mensajeSalida);
            System.out.println("Translator: Transformación completada - Mensaje ID: " + mensajeEntrada.getId());

        } catch (Exception ex) {
            Logger.getLogger(Translator.class.getName()).log(Level.SEVERE,
                    "Translator: Error durante la transformación", ex);
        }
    }

    private Document transformarDocumento(Document documentoEntrada) throws Exception {
        TransformerFactory fabrica = TransformerFactory.newInstance();
        StreamSource fuenteXSLT = new StreamSource(new File(rutaArchivoXSLT));
        Transformer transformador = fabrica.newTransformer(fuenteXSLT);

        DOMSource fuenteXML = new DOMSource(documentoEntrada);
        StringWriter escritor = new StringWriter();
        StreamResult resultado = new StreamResult(escritor);

        transformador.transform(fuenteXML, resultado);
        return convertirStringADocumento(escritor.toString());
    }

    private Mensaje crearMensajeSalida(Mensaje mensajeEntrada, Document documentoTransformado) {
        Mensaje mensajeSalida = new Mensaje(documentoTransformado);
        mensajeSalida.setId(mensajeEntrada.getId());
        mensajeSalida.setIdCorrelacion(mensajeEntrada.getIdCorrelacion());
        mensajeSalida.setIdSecuencia(mensajeEntrada.getIdSecuencia());
        mensajeSalida.setLongitud(mensajeEntrada.getLongitud());
        return mensajeSalida;
    }

    private Document convertirStringADocumento(String xmlString) {
        Document documento = null;
        try {
            DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
            DocumentBuilder constructor = fabrica.newDocumentBuilder();
            InputSource fuente = new InputSource(new StringReader(xmlString));
            documento = constructor.parse(fuente);
        } catch (Exception ex) {
            Logger.getLogger(Translator.class.getName()).log(Level.SEVERE, "Translator: Error convirtiendo String a Document", ex);
        }
        return documento;
    }
}

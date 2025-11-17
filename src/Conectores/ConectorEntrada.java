package Conectores;

import Mensajes.Mensaje;
import Puertos.Puerto;
import Puertos.PuertoEntrada;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Importa mensajes desde un archivo XML específico
 * El Conector de Entrada lee un archivo XML y lo convierte en mensajes
 * Permite que el proceso consuma datos de sistemas externos a traves de un archivo XML
 */
public class ConectorEntrada extends Conector {

    public ConectorEntrada(Puerto puerto, String ficheroEntradaXml) {
        super(puerto, ficheroEntradaXml);
        
        if (!(puerto instanceof PuertoEntrada)) {
            throw new IllegalArgumentException("ConectorEntrada requiere un PuertoEntrada");
        }
    }

    @Override
    public void ejecutar() {
        File archivo = new File(configuracion);
        
        // Verificar que el archivo existe y es un archivo XML
        if (!archivo.exists()) {
            System.err.println("ConectorEntrada: El archivo no existe - " + configuracion);
            return;
        }
        
        if (!archivo.isFile()) {
            System.err.println("ConectorEntrada: La ruta no es un archivo - " + configuracion);
            return;
        }
        
        if (!archivo.getName().toLowerCase().endsWith(".xml")) {
            System.err.println("ConectorEntrada: El archivo no es XML - " + configuracion);
            return;
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document documento = builder.parse(archivo);
            documento.getDocumentElement().normalize();
            
            Mensaje mensaje = new Mensaje(documento);
            PuertoEntrada puertoEntrada = (PuertoEntrada) puerto;
            
            puertoEntrada.escribirMensajeEnSlot(mensaje);
            
            System.out.println("ConectorEntrada: Mensaje importado desde " + archivo.getName());
            
        } catch (IOException | ParserConfigurationException | SAXException ex) {
            Logger.getLogger(ConectorEntrada.class.getName()).log(Level.SEVERE, 
                "ConectorEntrada: Error procesando archivo " + archivo.getName(), ex);
        }
    }
    
    /**
     * @return Devuelve 'true' si existe el Archivo y 'false' en caso contrario
     */
    public boolean archivoExiste() {
        File archivo = new File(configuracion);
        return archivo.exists() && archivo.isFile();
    }
    
    /**
     * @return Devuelve la Información del Archivo
     */
    public String getInfoArchivo() {
        File archivo = new File(configuracion);
        if (archivo.exists()) {
            return String.format("Archivo: %s (Tamaño: %d bytes)", 
                archivo.getName(), archivo.length());
        }
        return "Archivo no encontrado: " + configuracion;
    }
}
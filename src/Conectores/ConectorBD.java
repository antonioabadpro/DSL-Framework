package Conectores;

import Mensajes.Mensaje;
import Puertos.Puerto;
import Puertos.PuertoSolicitud;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ConectorBD extends Conector {

    private Connection conexion;

    public ConectorBD(Puerto puerto, String urlBaseDatos) {
        super(puerto, urlBaseDatos);
        
        if (!(puerto instanceof PuertoSolicitud)) {
            throw new IllegalArgumentException("ConectorBD requiere un PuertoSolicitud");
        }
        
        establecerConexion();
    }

    @Override
    public void ejecutar() {
        System.out.println(">>> ConectorBD NUEVO: inicio ejecutar()");

        PuertoSolicitud puertoSolicitud = (PuertoSolicitud) puerto;
        int procesados = 0;

        // Procesar TODOS los mensajes que haya en el slot
        Mensaje mensajeEntrada;
        while ((mensajeEntrada = puertoSolicitud.leerMensajeDeSlot()) != null) {
            try {
                String consultaSQL = extraerConsultaSQL(mensajeEntrada.getCuerpo());
                if (consultaSQL == null || consultaSQL.isBlank()) {
                    System.err.println("ConectorBD: Consulta SQL vacía, se omite el mensaje");
                    continue;
                }

                consultaSQL = consultaSQL.trim();
                System.out.println("ConectorBD: Ejecutando consulta - " + consultaSQL);

                Document documentoResultado = ejecutarConsulta(consultaSQL);
                
                Mensaje mensajeSalida = new Mensaje(documentoResultado);
                // Propagar metadatos importantes
                mensajeSalida.setId(mensajeEntrada.getId());
                mensajeSalida.setIdCorrelacion(mensajeEntrada.getIdCorrelacion());
                mensajeSalida.setIdSecuencia(mensajeEntrada.getIdSecuencia());
                mensajeSalida.setLongitud(mensajeEntrada.getLongitud());
                
                puertoSolicitud.escribirMensajeEnSlot(mensajeSalida);
                
                System.out.println("ConectorBD: Consulta ejecutada - " + consultaSQL);
                procesados++;
                
            } catch (Exception ex) {
                System.err.println("ConectorBD: Error ejecutando consulta - " + ex.getMessage());
            }
        }

        if (procesados == 0) {
            System.out.println("ConectorBD NUEVO: No había consultas pendientes.");
        } else {
            System.out.println("ConectorBD NUEVO: Total de consultas procesadas = " + procesados);
        }
    }

    private void establecerConexion() {
        try {
            conexion = DriverManager.getConnection(configuracion);
            System.out.println("ConectorBD: Conexión establecida con " + configuracion);
        } catch (Exception ex) {
            System.err.println("ConectorBD: Error conectando a BD - " + ex.getMessage());
        }
    }

    private String extraerConsultaSQL(Document documento) {
        if (documento == null || documento.getElementsByTagName("sql").getLength() == 0) {
            return null;
        }
        return documento.getElementsByTagName("sql").item(0).getTextContent();
    }

    /**
     * Ejecuta la consulta y construye:
     * - Si hay filas:
     *   <resultados>
     *     <fila>...</fila>
     *   </resultados>
     * - Si NO hay filas:
     *   <resultados>
     *     <fila>
     *       <name>...nombre de la consulta...</name>
     *       <stock>0</stock>
     *     </fila>
     *   </resultados>
     */
    private Document ejecutarConsulta(String consultaSQL) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document documento = builder.newDocument();

        Element raiz = documento.createElement("resultados");
        documento.appendChild(raiz);

        boolean tieneFilas = false;

        try (PreparedStatement statement = conexion.prepareStatement(consultaSQL);
             ResultSet resultado = statement.executeQuery()) {

            ResultSetMetaData metadatos = (ResultSetMetaData) resultado.getMetaData();
            int numeroColumnas = metadatos.getColumnCount();

            while (resultado.next()) {
                tieneFilas = true;

                Element fila = documento.createElement("fila");
                raiz.appendChild(fila);

                for (int i = 1; i <= numeroColumnas; i++) {
                    String nombreColumna = metadatos.getColumnLabel(i);
                    Object valor = resultado.getObject(i);

                    Element columna = documento.createElement(nombreColumna);
                    columna.setTextContent(valor != null ? valor.toString() : "");
                    fila.appendChild(columna);
                }
            }
        }

        // Si la consulta no devolvió ninguna fila (nombre no existe en BD)
        // generamos una fila artificial con stock = 0
        if (!tieneFilas) {
            String nombre = extraerNombreDesdeSQL(consultaSQL);

            Element fila = documento.createElement("fila");
            raiz.appendChild(fila);

            Element nameElem = documento.createElement("name");
            nameElem.setTextContent(nombre != null ? nombre : "");
            fila.appendChild(nameElem);

            Element stockElem = documento.createElement("stock");
            stockElem.setTextContent("0");
            fila.appendChild(stockElem);

            System.out.println("ConectorBD: Sin filas en BD, generado resultado ficticio para name='" 
                               + nameElem.getTextContent() + "' con stock=0");
        }

        return documento;
    }

    /**
     * Extrae el valor del name de una consulta del tipo:
     *   Select  * from "BebidasFrias" where name='tonica'
     */
    private String extraerNombreDesdeSQL(String consultaSQL) {
        if (consultaSQL == null) return null;

        String lower = consultaSQL.toLowerCase();
        String patron = "where name='";
        int idx = lower.indexOf(patron);
        if (idx == -1) {
            return null;
        }

        int inicio = idx + patron.length();
        int fin = lower.indexOf("'", inicio);
        if (fin == -1) {
            return null;
        }

        // Usamos la cadena original (con mayúsculas/minúsculas) para no perder formato
        return consultaSQL.substring(inicio, fin).trim();
    }
}

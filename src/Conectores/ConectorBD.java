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
        // Aquí NO ponemos while(true). El hilo ya lo hace en el run() del padre.
        // Simplemente procesamos UN mensaje.

        PuertoSolicitud puertoSolicitud = (PuertoSolicitud) puerto;

        // 1. Leemos del slot (bloqueante)
        Mensaje mensajeEntrada = puertoSolicitud.leerMensajeDeSlot();
        if (mensajeEntrada == null) {
            return;
        }

        try {
            String consultaSQL = extraerConsultaSQL(mensajeEntrada.getCuerpo());
            if (consultaSQL == null || consultaSQL.isBlank()) {
                System.err.println("ConectorBD: Consulta SQL vacía, se omite el mensaje");
                return;
            }

            consultaSQL = consultaSQL.trim();
            System.out.println("ConectorBD: Ejecutando consulta - " + consultaSQL);

            Document documentoResultado = ejecutarConsulta(consultaSQL);

            Mensaje mensajeSalida = new Mensaje(documentoResultado);
            mensajeSalida.setId(mensajeEntrada.getId());
            mensajeSalida.setIdCorrelacion(mensajeEntrada.getIdCorrelacion());
            mensajeSalida.setIdSecuencia(mensajeEntrada.getIdSecuencia());
            mensajeSalida.setLongitud(mensajeEntrada.getLongitud());

            puertoSolicitud.escribirMensajeEnSlot(mensajeSalida);

            System.out.println("ConectorBD: Consulta ejecutada.");

        } catch (Exception ex) {
            System.err.println("ConectorBD: Error ejecutando consulta - " + ex.getMessage());
        }
    }

    private void establecerConexion() {
        try {
            conexion = DriverManager.getConnection(configuracion);
            System.out.println("ConectorBD: Conexión establecida");
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

    private Document ejecutarConsulta(String consultaSQL) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document documento = builder.newDocument();

        Element raiz = documento.createElement("resultados");
        documento.appendChild(raiz);

        boolean tieneFilas = false;

        try (PreparedStatement statement = conexion.prepareStatement(consultaSQL); ResultSet resultado = statement.executeQuery()) {

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
        }

        return documento;
    }

    private String extraerNombreDesdeSQL(String consultaSQL) {
        if (consultaSQL == null) {
            return null;
        }
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
        return consultaSQL.substring(inicio, fin).trim();
    }
}

package Aplicaciones;

import Conectores.*;
import Puertos.*;
import Slots.Slot;
import Tareas.Implementacion.*;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppCorrelationIdSetter {

    public static void main(String[] args) throws SQLException {
        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8")); // Consola con caracteres UTF-8
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        // 1. Configuración de BD
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("Driver PostgreSQL cargado correctamente.");
        } catch (ClassNotFoundException e) {
            System.out.println("NO se encontró el driver PostgreSQL.");
        }

        // 2. Creación de Slots
        Slot sEntradaSplitter = new Slot();
        Slot sSplitterDistributor = new Slot();
        Slot sDistRep_F = new Slot();
        Slot sDistRep_C = new Slot();
        Slot sRepTrans_F = new Slot();
        Slot sRepTrans_C = new Slot();
        Slot sRepCorr_F = new Slot();
        Slot sRepCorr_C = new Slot();
        Slot sTransPuerto_F = new Slot();
        Slot sTransPuerto_C = new Slot();
        Slot sPuertoCorr_F = new Slot();
        Slot sPuertoCorr_C = new Slot();
        Slot sCorrEnrich_1_F = new Slot();
        Slot sCorrEnrich_1_C = new Slot();
        Slot sEnrichMerger_F = new Slot();
        Slot sEnrichMerger_C = new Slot();
        Slot sMergerAgg = new Slot();
        Slot sAggSalida = new Slot();

        // 3. Creación de Puertos
        PuertoEntrada pEntrada = new PuertoEntrada(sEntradaSplitter);
        PuertoSolicitud pBD_F = new PuertoSolicitud(sTransPuerto_F, sPuertoCorr_F);
        PuertoSolicitud pBD_C = new PuertoSolicitud(sTransPuerto_C, sPuertoCorr_C);
        PuertoSalida pSalida = new PuertoSalida(sAggSalida);

        // 4. CONECTORES (CORREGIDO AQUÍ TAMBIÉN)
        // Pasamos solo la carpeta, sin preguntar nada al usuario
        ConectorEntrada conectorEntrada = new ConectorEntrada(pEntrada, "src/Comandas/");

        String urlBD = "jdbc:postgresql://aws-1-eu-north-1.pooler.supabase.com:5432/postgres?user=postgres.wttznbvrlqmioczuafnx&password=bdiia202512345";
        ConectorBD conectorBD_F = new ConectorBD(pBD_F, urlBD);
        ConectorBD conectorBD_C = new ConectorBD(pBD_C, urlBD);
        ConectorSalida conectorSalida = new ConectorSalida(pSalida, "./salidas/");

        // 5. TAREAS
        // Splitter
        ArrayList<Slot> slIn = new ArrayList<>();
        slIn.add(sEntradaSplitter);
        ArrayList<Slot> slOut = new ArrayList<>();
        slOut.add(sSplitterDistributor);
        Splitter splitter = new Splitter(slIn, slOut, "/cafe_order/drinks/drink");

        // Distributor
        ArrayList<Slot> dsIn = new ArrayList<>();
        dsIn.add(sSplitterDistributor);
        ArrayList<Slot> dsOut = new ArrayList<>();
        dsOut.add(sDistRep_F);
        dsOut.add(sDistRep_C);
        ArrayList<String> reglas = new ArrayList<>();
        reglas.add("drink/type = 'cold'");
        reglas.add("drink/type = 'hot'");
        Distributor distributor = new Distributor(dsIn, dsOut, reglas);

        // Replicators
        ArrayList<Slot> rfIn = new ArrayList<>();
        rfIn.add(sDistRep_F);
        ArrayList<Slot> rfOut = new ArrayList<>();
        rfOut.add(sRepTrans_F);
        rfOut.add(sRepCorr_F);
        Replicator repF = new Replicator(rfIn, rfOut);

        ArrayList<Slot> rcIn = new ArrayList<>();
        rcIn.add(sDistRep_C);
        ArrayList<Slot> rcOut = new ArrayList<>();
        rcOut.add(sRepTrans_C);
        rcOut.add(sRepCorr_C);
        Replicator repC = new Replicator(rcIn, rcOut);

        // Translators
        ArrayList<Slot> tfIn = new ArrayList<>();
        tfIn.add(sRepTrans_F);
        ArrayList<Slot> tfOut = new ArrayList<>();
        tfOut.add(sTransPuerto_F);
        Translator transF = new Translator(tfIn, tfOut, "src/Formatos/BebidasFrias.xsl");

        ArrayList<Slot> tcIn = new ArrayList<>();
        tcIn.add(sRepTrans_C);
        ArrayList<Slot> tcOut = new ArrayList<>();
        tcOut.add(sTransPuerto_C);
        Translator transC = new Translator(tcIn, tcOut, "src/Formatos/BebidasCalientes.xsl");

        // CorrelationIdSetters
        ArrayList<Slot> cidInF = new ArrayList<>();
        cidInF.add(sRepCorr_F);
        ArrayList<Slot> cidOutF = new ArrayList<>();
        cidOutF.add(sCorrEnrich_1_F);
        CorrelationIdSetter cidSetterF = new CorrelationIdSetter(cidInF, cidOutF);

        ArrayList<Slot> cidInC = new ArrayList<>();
        cidInC.add(sRepCorr_C);
        ArrayList<Slot> cidOutC = new ArrayList<>();
        cidOutC.add(sCorrEnrich_1_C);
        CorrelationIdSetter cidSetterC = new CorrelationIdSetter(cidInC, cidOutC);

        // ContextEnrichers
        ArrayList<Slot> efIn = new ArrayList<>();
        efIn.add(sCorrEnrich_1_F);
        efIn.add(sPuertoCorr_F);
        ArrayList<Slot> efOut = new ArrayList<>();
        efOut.add(sEnrichMerger_F);
        ContextEnricher enrichF = new ContextEnricher(efIn, efOut);

        ArrayList<Slot> ecIn = new ArrayList<>();
        ecIn.add(sCorrEnrich_1_C);
        ecIn.add(sPuertoCorr_C);
        ArrayList<Slot> ecOut = new ArrayList<>();
        ecOut.add(sEnrichMerger_C);
        ContextEnricher enrichC = new ContextEnricher(ecIn, ecOut);

        // Merger
        ArrayList<Slot> mIn = new ArrayList<>();
        mIn.add(sEnrichMerger_F);
        mIn.add(sEnrichMerger_C);
        ArrayList<Slot> mOut = new ArrayList<>();
        mOut.add(sMergerAgg);
        Merger merger = new Merger(mIn, mOut);

        // Aggregator
        ArrayList<Slot> agIn = new ArrayList<>();
        agIn.add(sMergerAgg);
        ArrayList<Slot> agOut = new ArrayList<>();
        agOut.add(sAggSalida);
        Aggregator aggregator = new Aggregator(agIn, agOut, "cafe_order");

        // 6. ARRANQUE DE HILOS
        System.out.println(">>> SISTEMA CORRELATION ID SETTER ARRANCADO (Modo Carpeta) <<<");

        new Thread(splitter).start();
        new Thread(distributor).start();
        new Thread(repF).start();
        new Thread(repC).start();
        new Thread(transF).start();
        new Thread(transC).start();
        new Thread(conectorBD_F).start();
        new Thread(conectorBD_C).start();

        // Aquí arrancamos los CorrelationIdSetters en vez de Correlators
        new Thread(cidSetterF).start();
        new Thread(cidSetterC).start();

        new Thread(enrichF).start();
        new Thread(enrichC).start();
        new Thread(merger).start();
        new Thread(aggregator).start();
        new Thread(conectorSalida).start();

        // Arrancamos el vigilante de carpeta
        new Thread(conectorEntrada).start();
    }
}

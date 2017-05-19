package org.valuereporter;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.valuereporter.helper.DatabaseMigrationHelper;
import org.valuereporter.helper.EmbeddedDatabaseHelper;
import org.valuereporter.helper.PropertiesHelper;
import org.valuereporter.helper.StatusType;

import java.io.File;
import java.net.BindException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;


public class Main {
    public static final int DEFAULT_PORT_NO = 4901;
    public static final String CONTEXT_PATH = "/reporter";
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static final java.lang.String DATABASE_URL = "jdbc.url";

    private Server server;
    private String resourceBase;
    private int jettyPort = -1;

    public Main() {
        Properties resources = PropertiesHelper.findProperties();

        try{
            new EmbeddedDatabaseHelper(resources).initializeDatabase();
            if (useLocalDatabase(resources) && DatabaseMigrationHelper.isFlywaySupported(resources)) {
                new DatabaseMigrationHelper(resources).upgradeDatabase();
            }
            jettyPort = PropertiesHelper.findHttpPort(resources);
        } catch (ValuereporterException tde) {
            log.error("Could not initalize the service. Exiting. ", tde);
            System.exit(0);
        } catch (Exception e) {
            log.error("Unexpected error. Exiting. ", e);
            System.exit(0);
        }

        server = new Server(jettyPort);

        URL url = ClassLoader.getSystemResource("webapp/WEB-INF/web.xml");
        resourceBase = url.toExternalForm().replace("/WEB-INF/web.xml", "");
    }

    public static void main(String[] arguments) throws Exception {
        // Jersey uses java.util.logging - bridge to slf4
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        //Enable openness in JerseyApplication logging.
        LogManager.getLogManager().getLogger("").setLevel(Level.FINEST);

        Main main = new Main();
        try {
            main.start();
            main.join();
        } catch (ValuereporterException e) {
            log.error("Failed to start the server. Reason {}. Port {} ", e.getMessage(), main.getPortNumber());
            main.stopOnError();
        }
    }

    public void start() throws RuntimeException {
        WebAppContext context = new WebAppContext();
        log.debug("Start Jetty using resourcebase={}", resourceBase);
        context.setDescriptor(resourceBase + "/WEB-INF/web.xml");
        context.setResourceBase(resourceBase);
        context.setContextPath(CONTEXT_PATH);
        context.setParentLoaderPriority(true);

        HandlerCollection handlers = new HandlerCollection();
        RequestLogHandler requestLogHandler = new RequestLogHandler();
        handlers.setHandlers(new Handler[]{context,new DefaultHandler(),requestLogHandler});
        server.setHandler(handlers);


        enableRequestLogging(requestLogHandler);

        try {
            server.start();
        } catch (BindException be) {
            throw new ValuereporterException("Failed to start the server. Ther port is already in use.", StatusType.RETRY_NOT_POSSIBLE);
        } catch (Exception e) {
            throw new ValuereporterException("Failed to start." ,e, StatusType.RETRY_NOT_POSSIBLE);
        }
        Throwable springStartupFailed = context.getUnavailableException();
        if (springStartupFailed != null) {
            throw new ValuereporterException("Failed to initialize Spring Application Context." , springStartupFailed, StatusType.RETRY_NOT_POSSIBLE);
        }
        int localPort = getPortNumber();
        log.info("Jetty server started on port {}, context path {}", localPort, CONTEXT_PATH);
    }

    private void enableRequestLogging(RequestLogHandler requestLogHandler) {
        String logDir = "./logs";
        ensureLogDirexist(logDir);
        NCSARequestLog requestLog = new NCSARequestLog(logDir + "/jetty-yyyy_mm_dd.request.log");
        requestLog.setRetainDays(90);
        requestLog.setAppend(true);
        requestLog.setExtended(false);
        requestLog.setLogLatency(true);
        requestLog.setLogTimeZone("GMT");
        requestLogHandler.setRequestLog(requestLog);
    }

    private void ensureLogDirexist(String logDirPath) {
        File logDir = new File(logDirPath);
        if (!logDir.exists()) {
            logDir.mkdir();
        }
    }

    public void stopOnError() throws Exception {
        server.stop();
        System.exit(1);
    }

    public void join() throws InterruptedException {
        server.join();
    }
    private boolean useLocalDatabase(Properties resources) {
        boolean useEmbedded = EmbeddedDatabaseHelper.useEmbeddedDb(resources);
        boolean useLocal = !useEmbedded;
        return useLocal;
    }



    public int getPortNumber() {
        return ((ServerConnector) server.getConnectors()[0]).getLocalPort();
    }

    public void setResourceBase(String resourceBase) {
        this.resourceBase = resourceBase;
    }

    public String getResourceBase() {
        return resourceBase;
    }

}

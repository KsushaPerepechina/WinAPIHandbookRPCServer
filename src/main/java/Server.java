import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;
import thrift.WinAPIHandbookService;

import java.util.ResourceBundle;

public class Server {
    private static final Logger log = LogManager.getLogger(Server.class);

    public void start() {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("config");
            Integer port = Integer.parseInt(bundle.getString("server.port"));
            TServerSocket serverTransport = new TServerSocket(port);

            WinAPIHandbookHandler handler = new  WinAPIHandbookHandler();
            WinAPIHandbookService.Processor<WinAPIHandbookService.Iface> processor =
                    new WinAPIHandbookService.Processor<WinAPIHandbookService.Iface>(handler);
            TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));

            log.info("Service started on " + serverTransport.getServerSocket().getInetAddress() + ":" + serverTransport.getServerSocket().getLocalPort());
            server.serve();
        } catch (TTransportException e) {
            log.error(e.getMessage());
        }
    }
}

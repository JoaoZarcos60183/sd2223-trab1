package sd2223.trab1.api.server;

import java.net.InetAddress;
import java.net.URI;
import java.util.logging.Logger;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import sd2223.trab1.api.api.Discovery;
import sd2223.trab1.api.server.resources.FeedResource;

public class FeedsServer {
    private static Logger Log = Logger.getLogger(FeedsServer.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static final int PORT = 8080;
    private static final String SERVER_URI_FMT = "http://%s:%s/rest";

    public static void main(String[] args) {
        Discovery disc = Discovery.getInstance();

        try {

            if (args.length != 2) {
                System.err.println("Use: domain");
                System.exit(0);
                return;
            }

            String service ="feeds." + args[0];

            String uri = String.format(SERVER_URI_FMT, InetAddress.getLocalHost().getHostAddress(), PORT);
            disc.announce(service, uri);

            ResourceConfig config = new ResourceConfig();

            config.register(FeedResource.class);
            
            String ip = InetAddress.getLocalHost().getHostAddress();
            String serverURI = String.format(SERVER_URI_FMT, ip, PORT);
            JdkHttpServerFactory.createHttpServer(URI.create(serverURI), config);

            Log.info(String.format("%s Server ready @ %s\n", service, serverURI));

            // More code can be executed here...
        } catch (Exception e) {
            Log.severe(e.getMessage());
        }
    }
}
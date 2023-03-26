package sd2223.trab1.api.server;

import java.net.InetAddress;
import java.net.URI;
import java.util.logging.Logger;

import sd2223.trab1.api.api.Discovery;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import sd2223.trab1.api.server.resources.AppResource;

public class UsersServer {

	private static Logger Log = Logger.getLogger(UsersServer.class.getName());

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
	}

	public static final int PORT = 8080;
	public static final String SERVICE = "UsersService";
	private static final String SERVER_URI_FMT = "http://%s:%s/rest";

	public static void main(String[] args) {
		Discovery disc = Discovery.getInstance();

		try {

			String uri = String.format(SERVER_URI_FMT, InetAddress.getLocalHost().getHostAddress(), PORT);
			disc.announce(SERVICE, uri);

			ResourceConfig config = new ResourceConfig();
			config.register(AppResource.class);
			// config.register(CustomLoggingFilter.class);

			String ip = InetAddress.getLocalHost().getHostAddress();
			String serverURI = String.format(SERVER_URI_FMT, ip, PORT);
			JdkHttpServerFactory.createHttpServer(URI.create(serverURI), config);

			Log.info(String.format("%s Server ready @ %s\n", SERVICE, serverURI));

			// More code can be executed here...
		} catch (Exception e) {
			Log.severe(e.getMessage());
		}
	}
}

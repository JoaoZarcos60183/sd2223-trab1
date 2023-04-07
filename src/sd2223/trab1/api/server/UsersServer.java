package sd2223.trab1.api.server;

import java.net.InetAddress;
import java.net.URI;
import java.util.logging.Logger;

import sd2223.trab1.api.api.Discovery;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import sd2223.trab1.api.server.resources.UserResource;

public class UsersServer {

	private static Logger Log = Logger.getLogger(UsersServer.class.getName());

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
	}

	public static final int PORT = 8080;
	private static final String SERVER_URI_FMT = "http://%s:%s/rest";

	public static void main(String[] args) {
		Discovery disc = Discovery.getInstance();

		try {

			if (args.length != 1) {
				System.err.println("Use: java aula3.clients.UsersServer domain");
				System.exit(0);
				return;
			}

			String service = "users." + args[0];

			String uri = String.format(SERVER_URI_FMT, InetAddress.getLocalHost().getHostAddress(), PORT);
			disc.announce(service, uri); //Com dois servidores, anunciar repetido? e Qual Ã© o servidor que o Cliente vai buscar? Cada um vai ter nome diferente?

			ResourceConfig config = new ResourceConfig();
			config.register(UserResource.class);
			// config.register(CustomLoggingFilter.class);

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
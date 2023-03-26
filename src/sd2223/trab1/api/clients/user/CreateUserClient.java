package sd2223.trab1.api.clients.user;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

import sd2223.trab1.api.api.Discovery;
import sd2223.trab1.api.api.User;
import sd2223.trab1.api.server.UsersServer;

public class CreateUserClient {
	
	private static Logger Log = Logger.getLogger(CreateUserClient.class.getName());

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
	}
	
	public static void main(String[] args) throws IOException {
				
		if (args.length != 4) {
			System.err.println("Use: java aula3.clients.CreateUserClient name pwd domain displayName");
			return;
		}

		Discovery discovery = Discovery.getInstance();
		URI[] uris = discovery.knownUrisOf(UsersServer.SERVICE, 1);		

		String serverUrl = uris[0].toString();
		String name = args[1];
		String pwd = args[2];
		String domain = args[3];
		String displayName = args[4];

		User u = new User(name, pwd, domain, displayName);

		Log.info("Sending request to server.");

		var result = new RestUsersClient(URI.create(serverUrl)).createUser(u);
		System.out.println("Result: " + result);
	}

}

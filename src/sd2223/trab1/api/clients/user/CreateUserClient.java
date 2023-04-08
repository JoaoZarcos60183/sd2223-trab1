package sd2223.trab1.api.clients.user;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

import sd2223.trab1.api.api.Discovery;
import sd2223.trab1.api.api.User;

public class CreateUserClient {
	
	private static Logger Log = Logger.getLogger(CreateUserClient.class.getName());

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
	}
	
	public static void main(String[] args) throws IOException {
				
		if (args.length != 3) {
			System.err.println("Use: java aula3.clients.CreateUserClient name pwd displayName");
			return;
		}

		Discovery discovery = Discovery.getInstance();

		String[] userAndDomain = args[0].split("@");
		String pwd = args[1];
		String displayName = args[2];
		String domain = "users." + userAndDomain[1];

		URI[] uris = discovery.knownUrisOf(domain, 1);

		User u = new User(args[0], pwd, userAndDomain[1], displayName);

		Log.info("Sending request to server.");

		var result = new RestUsersClient(uris[uris.length-1]).createUser(u);
		System.out.println("Result: " + result);

		System.exit(0);
	}

}

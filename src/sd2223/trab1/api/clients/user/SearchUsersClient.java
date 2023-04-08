package sd2223.trab1.api.clients.user;

import java.io.IOException;
import java.net.URI;

import sd2223.trab1.api.api.Discovery;


public class SearchUsersClient {
	
	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
	}
	
	public static void main(String[] args) throws IOException {
		
		if (args.length != 2) {
			System.err.println("Use: java SearchUsersClient pattern domain");
			return;
		}

		Discovery discovery = Discovery.getInstance();

		String pattern = args[0];
		String domain = "users." + args[1];

		URI[] uris = discovery.knownUrisOf(domain, 1); //Como ir buscar os restantes users de domains diferentes, ou seja sem passar os domains nos argumentos
		System.out.println("Sending request to server.");

		var result = new RestUsersClient(uris[uris.length-1]).searchUsers(pattern);
		System.out.println("Success: (" + result.size() + " users)");
		result.stream().forEach( u -> System.out.println( u));

		System.exit(0);
	}

}

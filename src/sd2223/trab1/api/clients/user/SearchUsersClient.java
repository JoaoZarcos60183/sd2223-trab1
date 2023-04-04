package sd2223.trab1.api.clients.user;

import java.io.IOException;
import java.net.URI;

import sd2223.trab1.api.api.Discovery;


public class SearchUsersClient {
	
	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
	}
	
	public static void main(String[] args) throws IOException {
		
		if (args.length != 1) {
			System.err.println("Use: java aula3.clients.SearchUsersClient pattern ");
			return;
		}

		Discovery discovery = Discovery.getInstance();

		String[] userAndDomain = args[0].split("@");
		String pattern = args[1];
		String domain = "users." + userAndDomain[1];

		URI[] uris = discovery.knownUrisOf(domain, 1);
		System.out.println("Sending request to server.");

		var result = new RestUsersClient(uris[uris.length-1]).searchUsers(pattern);
		System.out.println("Success: (" + result.size() + " users)");
		result.stream().forEach( u -> System.out.println( u));

	}

}

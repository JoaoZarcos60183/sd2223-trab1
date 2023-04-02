package sd2223.trab1.api.clients.user;

import java.io.IOException;
import java.net.URI;

import sd2223.trab1.api.api.Discovery;
import sd2223.trab1.api.server.UsersServer;


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
		URI[] uris = discovery.knownUrisOf(UsersServer.SERVICE, 1);		

		String serverUrl = uris[0].toString();
		String pattern = args[0];


		System.out.println("Sending request to server.");

		var result = new RestUsersClient(URI.create(serverUrl)).searchUsers(pattern);
		System.out.println("Success: (" + result.size() + " users)");
		result.stream().forEach( u -> System.out.println( u));

	}

}

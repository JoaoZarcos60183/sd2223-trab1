package sd2223.trab1.api.clients.user;

import java.io.IOException;
import java.net.URI;

import sd2223.trab1.api.api.Discovery;
import sd2223.trab1.api.server.UsersServer;

public class GetUserClient {
	public static void main(String[] args) throws IOException {
		
		if( args.length != 2) {
			System.err.println( "Use: java aula2.clients.GetUserClient name pwd");
			return;
		}
		
		Discovery discovery = Discovery.getInstance();
		URI[] uris = discovery.knownUrisOf(UsersServer.SERVICE, 1);		

		String serverUrl = uris[0].toString();
		String name = args[0];
		String pwd = args[1];
		
		System.out.println("Sending request to server.");
		
		var result = new RestUsersClient(URI.create(serverUrl)).getUser(name, pwd);
		System.out.println("Result: " + result);

		System.exit(0);
	}
	
}

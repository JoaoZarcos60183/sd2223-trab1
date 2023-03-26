package sd2223.trab1.api.clients.user;

import java.io.IOException;
import java.net.URI;

import sd2223.trab1.api.api.Discovery;

public class GetUserClient {

	public static final String SERVICE = "UsersService";
	public static void main(String[] args) throws IOException {
		
		if( args.length != 2) {
			System.err.println( "Use: java aula2.clients.GetUserClient name pwd");
			return;
		}
		
		Discovery discovery = Discovery.getInstance();
		URI[] uris = discovery.knownUrisOf(SERVICE, 1);		

		String serverUrl = uris[0].toString();
		String name = args[1];
		String pwd = args[2];
		
		System.out.println("Sending request to server.");
		
		var result = new RestUsersClient(URI.create(serverUrl)).getUser(name, pwd);
		System.out.println("Result: " + result);

		System.exit(0);
	}
	
}

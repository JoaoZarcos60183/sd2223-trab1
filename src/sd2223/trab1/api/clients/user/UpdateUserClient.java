package sd2223.trab1.api.clients.user;

import java.io.IOException;
import java.net.URI;


import sd2223.trab1.api.api.Discovery;
import sd2223.trab1.api.api.User;

public class UpdateUserClient {

	public static final String SERVICE = "UsersService";

	public static void main(String[] args) throws IOException {
		
		if( args.length != 5) {
			System.err.println( "Use: java aula2.clients.UpdateUserClient name oldpwd pwd domain displayName");
			return;
		}
		
		Discovery discovery = Discovery.getInstance();
		URI[] uris = discovery.knownUrisOf(SERVICE, 1);		

		String serverUrl = uris[0].toString();
		String name = args[1];
		String oldpwd = args[2];
		String pwd = args[3];
		String domain = args[4];
		String displayName = args[5];
		
		
		var u = new User( name, pwd, domain, displayName);
		
		System.out.println("Sending request to server.");
		
		//TODO complete this client code

		var result = new RestUsersClient(URI.create(serverUrl)).updateUser(name, oldpwd, u);
		System.out.println("Result: " + result);

			System.exit(0);
	}
	
}

package sd2223.trab1.api.clients.user;

import java.io.IOException;
import java.net.URI;


import sd2223.trab1.api.api.Discovery;
import sd2223.trab1.api.api.User;

public class UpdateUserClient {

	public static void main(String[] args) throws IOException {
		
		if( args.length != 4) {
			System.err.println( "Use: java aula2.clients.UpdateUserClient name oldpwd pwd displayName");
			return;
		}
		
		Discovery discovery = Discovery.getInstance();

		String[] userAndDomain = args[0].split("@");
		String oldpwd = args[1];
		String pwd = args[2];
		String displayName = args[3];

		String name = userAndDomain[0];
		String domain = "users." + userAndDomain[1];
		URI[] uris = discovery.knownUrisOf(domain, 1);

		var u = new User( args[0], pwd, userAndDomain[1], displayName);
		
		System.out.println("Sending request to server.");
		
		//TODO complete this client code

		var result = new RestUsersClient(uris[uris.length-1]).updateUser(args[0], oldpwd, u);
		System.out.println("Result: " + result);

		System.exit(0);
	}
	
}

package sd2223.trab1.api.clients.user;

import java.io.IOException;
import java.net.URI;
import sd2223.trab1.api.api.Discovery;

public class DeleteUserClient {

	public static void main(String[] args) throws IOException {
		
		if( args.length != 2) {
			System.err.println( "Use: java aula2.clients.DeleteUserClient name pwd");
			return;
		}
		
		Discovery discovery = Discovery.getInstance();

		String[] userAndDomain = args[0].split("@");
		String pwd = args[1];
		String domain = "users." + userAndDomain[1];

		URI[] uris = discovery.knownUrisOf(domain, 1);
		
		System.out.println("Sending request to server.");
		
		var result = new RestUsersClient(uris[uris.length-1]).deleteUser(args[0], pwd);
		System.out.println("Result: " + result);
	
		System.exit(0);
	}
}

package sd2223.trab1.api.clients.feed;

import java.io.IOException;
import java.net.URI;
import sd2223.trab1.api.api.Discovery;
import sd2223.trab1.api.server.UsersServer;

public class GetMessageClient {
	public static void main(String[] args) throws IOException {
		
		if( args.length != 2) {
			System.err.println( "Use: java aula2.clients.GetUserClient name mid");
			return;
		}
		
		Discovery discovery = Discovery.getInstance();
		URI[] uris = discovery.knownUrisOf(UsersServer.SERVICE, 1);		

		String serverUrl = uris[0].toString();
		String name = args[0];
		long mid = Long.parseLong(args[1]);
		
		System.out.println("Sending request to server.");
		
		var result = new RestMessageClient(URI.create(serverUrl)).getMessage(name, mid);
		System.out.println("Result: " + result);

		System.exit(0);
	}
	
}

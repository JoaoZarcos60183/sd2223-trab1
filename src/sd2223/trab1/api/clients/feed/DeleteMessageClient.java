package sd2223.trab1.api.clients.feed;

import java.io.IOException;
import java.net.URI;
import sd2223.trab1.api.api.Discovery;
import sd2223.trab1.api.server.UsersServer;

public class DeleteMessageClient {

	public static void main(String[] args) throws IOException {
		
		if( args.length != 3) {
			System.err.println( "Use: java aula2.clients.DeleteUserClient name pwd mid");
			return;
		}
		
		Discovery discovery = Discovery.getInstance();
		URI[] uris = discovery.knownUrisOf(UsersServer.SERVICE, 1);		

		String serverUrl = uris[0].toString();
		String name = args[0];
		String pwd = args[1];
        long mid = Long.parseLong(args[2]);
		
		System.out.println("Sending request to server.");
		
		//TODO complete this client code
		new RestMessageClient(URI.create(serverUrl)).removeFromPersonalFeed(name, mid, pwd);
		System.out.println("Post was deleted.");
	
		System.exit(0);
	}
}

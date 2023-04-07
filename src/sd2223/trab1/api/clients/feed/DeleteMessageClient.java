package sd2223.trab1.api.clients.feed;

import java.io.IOException;
import java.net.URI;
import sd2223.trab1.api.api.Discovery;

public class DeleteMessageClient {

	public static void main(String[] args) throws IOException {
		
		if( args.length != 3) {
			System.err.println( "Use: java aula2.clients.DeleteUserClient name pwd mid");
			return;
		}
		
		Discovery discovery = Discovery.getInstance();

		String[] userAndDomain = args[0].split("@");
		String pwd = args[1];
        long mid = Long.parseLong(args[2]);

		String user = userAndDomain[0];
		String domain = "feeds." + userAndDomain[1];
		URI[] uris = discovery.knownUrisOf(domain, 1);
		
		System.out.println("Sending request to server.");
		
		//TODO complete this client code
		new RestMessageClient(uris[uris.length-1]).removeFromPersonalFeed(args[0], mid, pwd);
		System.out.println("Post was deleted.");
	
		System.exit(0);
	}
}

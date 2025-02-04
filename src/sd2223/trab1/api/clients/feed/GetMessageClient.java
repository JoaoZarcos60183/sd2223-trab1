package sd2223.trab1.api.clients.feed;

import java.io.IOException;
import java.net.URI;
import sd2223.trab1.api.api.Discovery;

public class GetMessageClient {
	public static void main(String[] args) throws IOException {
		
		if( args.length != 2) {
			System.err.println( "Use: java aula2.clients.GetUserClient name mid");
			return;
		}
		
		Discovery discovery = Discovery.getInstance();

		String[] userAndDomain = args[0].split("@");
		long mid = Long.parseLong(args[1]);

		String domain ="feeds." + userAndDomain[1];
		URI[] uris = discovery.knownUrisOf(domain, 1);
		
		System.out.println("Sending request to server.");
		
		var result = new RestMessageClient(uris[uris.length-1]).getMessage(args[0], mid);
		System.out.println("Result: " + result);

		System.exit(0);
	}
	
}

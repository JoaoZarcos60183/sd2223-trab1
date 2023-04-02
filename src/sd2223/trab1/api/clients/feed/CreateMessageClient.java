package sd2223.trab1.api.clients.feed;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;
import sd2223.trab1.api.api.Message;
import sd2223.trab1.api.api.Discovery;
import sd2223.trab1.api.server.UsersServer;

public class CreateMessageClient {
	
	private static Logger Log = Logger.getLogger(CreateMessageClient.class.getName());

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
	}
	
	public static void main(String[] args) throws IOException {
				
		if (args.length != 5) {
			System.err.println("Use: java aula3.clients.CreateUserClient id name pwd domain message");
			return;
		}

		Discovery discovery = Discovery.getInstance();
		URI[] uris = discovery.knownUrisOf(UsersServer.SERVICE, 1);		

		String serverUrl = uris[0].toString();
        long id = Long.parseLong(args[0]);
		String name = args[1];
		String pwd = args[2];
        String domain = args[3];
		String message = args[4];

        Message m = new Message(id, name, domain, message);

		Log.info("Sending request to server.");

		var result = new RestMessageClient(URI.create(serverUrl)).postMessage(name, pwd, m);
		System.out.println("Result: " + result);
	}

}
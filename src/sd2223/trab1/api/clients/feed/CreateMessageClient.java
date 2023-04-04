package sd2223.trab1.api.clients.feed;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;
import sd2223.trab1.api.api.Message;
import sd2223.trab1.api.api.Discovery;

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

        long id = Long.parseLong(args[0]);
		String name = args[1];
		String pwd = args[2];
        String domain = args[3];
		String message = args[4];

		URI[] uris = discovery.knownUrisOf(domain, 1);	

        Message m = new Message(id, name, domain, message);

		Log.info("Sending request to server.");

		var result = new RestMessageClient(uris[0]).postMessage(name, pwd, m);
		System.out.println("Result: " + result);
	}

}
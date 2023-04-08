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
				
		if (args.length != 3) {
			System.err.println("Use: java aula3.clients.CreateUserClient name message pwd ");
			return;
		}

		Discovery discovery = Discovery.getInstance();	

		String[] userAndDomain = args[0].split("@");
		String msg = args[1];
		String pwd = args[2];
		String domain = "feeds." + userAndDomain[1];

		URI[] uris = discovery.knownUrisOf(domain, 1);	

        Message m = new Message(-1, args[0], userAndDomain[1], msg);

		Log.info("Sending request to server.");

		System.out.println(uris[uris.length-1]);
		var result = new RestMessageClient(uris[uris.length-1]).postMessage(args[0], pwd, m);
		System.out.println("Result: " + result);

		System.exit(0);
	}

}
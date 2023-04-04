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
		String[] userAndDomain = args[1].split("@");
		String msg = args[2];
		String pwd = args[3];

        String user = userAndDomain[0];
		String domain = "feeds." + userAndDomain[1];

		URI[] uris = discovery.knownUrisOf(domain, 1);	

        Message m = new Message(-1, user, userAndDomain[1], msg);

		Log.info("Sending request to server.");

		var result = new RestMessageClient(uris[uris.length-1]).postMessage(user, pwd, m);
		System.out.println("Result: " + result);
	}

}
package sd2223.trab1.api.clients.feed;

import sd2223.trab1.api.api.Discovery;

import java.io.IOException;
import java.net.URI;

public class SubUserClient {
    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 3) {
            System.err.println("Use: java aula3.clients.SubUserClient user userSub pwd");
            return;
        }

        Discovery discovery = Discovery.getInstance();

        String[] userAndDomain = args[0].split("@");
        String[] userSubAndDomain = args[1].split("@");
        String pwd = args[2];

        String user = userAndDomain[0];
        String userSub = userSubAndDomain[0];
        String domain = "feeds." + userAndDomain[1];

        URI[] uris = discovery.knownUrisOf(domain, 1);

        System.out.println("Sending request to server.");

        new RestMessageClient(uris[uris.length-1]).subUser(args[0], args[1], pwd);
        System.out.println("Success: " + user + " subscribed " + userSub);

        System.exit(0);
    }
    
}

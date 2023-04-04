package sd2223.trab1.api.clients.feed;

import sd2223.trab1.api.api.Discovery;

import java.io.IOException;
import java.net.URI;

public class SearchSubsClient {
    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.err.println("Use: java aula3.clients.SearchSubsClient user");
            return;
        }

        Discovery discovery = Discovery.getInstance();

        String[] userAndDomain = args[0].split("@");

        String user = userAndDomain[0];
        String domain = "feeds." + userAndDomain[1];

        URI[] uris = discovery.knownUrisOf(domain, 1);

        System.out.println("Sending request to server.");

        var result = new RestMessageClient(uris[uris.length-1]).listSubs(user);
        System.out.println("Success: (" + result.size() + " users)");
        result.stream().forEach(u -> System.out.println(u));
    }
    
}

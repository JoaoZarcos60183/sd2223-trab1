package sd2223.trab1.api.clients.feed;

import java.io.IOException;
import java.net.URI;
import sd2223.trab1.api.api.Discovery;

public class GetMessagesClient {
    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println("Use: java aula3.clients.GetMessagesClient name time");
            return;
        }

        Discovery discovery = Discovery.getInstance();

        String[] userAndDomain = args[0].split("@");
        long time = Long.parseLong(args[1]);
        String user = userAndDomain[0];
        String domain = "feeds." + userAndDomain[1];

        URI[] uris = discovery.knownUrisOf(domain, 1);

        System.out.println("Sending request to server.");

        var result = new RestMessageClient(uris[uris.length-1]).getMessages(user, time);
        System.out.println("Success: (" + result.size() + " users)");
        result.stream().forEach(u -> System.out.println(u));
    }
}
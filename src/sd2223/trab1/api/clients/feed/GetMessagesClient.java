package sd2223.trab1.api.clients.feed;

import java.io.IOException;
import java.net.URI;
import sd2223.trab1.api.api.Discovery;
import sd2223.trab1.api.server.UsersServer;

public class GetMessagesClient {
    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println("Use: java aula3.clients.SearchUsersClient name ");
            return;
        }

        Discovery discovery = Discovery.getInstance();
        URI[] uris = discovery.knownUrisOf(UsersServer.SERVICE, 1);

        String serverUrl = uris[0].toString();
        String name = args[0];
        long time = Long.parseLong(args[1]);

        System.out.println("Sending request to server.");

        var result = new RestMessageClient(URI.create(serverUrl)).getMessages(name, time);
        System.out.println("Success: (" + result.size() + " users)");
        result.stream().forEach(u -> System.out.println(u));
    }
}
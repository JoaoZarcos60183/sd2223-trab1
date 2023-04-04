package sd2223.trab1.api.server.resources;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import sd2223.trab1.api.api.Discovery;
import sd2223.trab1.api.api.Message;
import sd2223.trab1.api.api.User;
import sd2223.trab1.api.api.rest.FeedsService;
import sd2223.trab1.api.api.rest.UsersService;

public class FeedResource implements FeedsService{
    
    private final Map<String, Map<Long,Message>> feeds = new HashMap<>(); //perguntar ao prof qual e a melhor opcao: so String ou User
	private final Map<String, Map<String, User>> subs = new HashMap<>();
    private static Logger Log = Logger.getLogger(UserResource.class.getName());
	private Discovery discovery = Discovery.getInstance();
    final WebTarget target;
    public final Client client;
	final ClientConfig config;
    protected static final int READ_TIMEOUT = 5000;
	protected static final int CONNECT_TIMEOUT = 5000;

    public FeedResource(String domain){
        String[] arr = domain.split(".");
        String aux = "users." + arr[1];
        URI[] uris = discovery.knownUrisOf(aux, 1);

        this.config = new ClientConfig();

		config.property(ClientProperties.READ_TIMEOUT, READ_TIMEOUT);
		config.property( ClientProperties.CONNECT_TIMEOUT, CONNECT_TIMEOUT);
		
		this.client = ClientBuilder.newClient(config);

        target = client.target( uris[0] ).path( UsersService.PATH );
    }

    @Override
	public long postMessage(String user, String pwd, Message msg) {
		Log.info("postMessage : " + user);
		
		// Check if user data is valid
		if(user == null || pwd == null) {
			Log.info("User object invalid.");
			throw new WebApplicationException( Status.BAD_REQUEST );
		}

        Response r = target.path( user )
                    .queryParam(UsersService.PWD, pwd).request()
                    .accept(MediaType.APPLICATION_JSON)
                    .get();

        User userAux = r.readEntity(User.class);

		// Insert user, checking if name already exists
		if(userAux == null || !userAux.getPwd().equals(pwd)) {
			Log.info("Invalid credentials.");
			throw new WebApplicationException( Status.FORBIDDEN );
		}

        if (feeds.containsKey(userAux.getName()))
		    feeds.get(user).put(msg.getId(), msg);
        else{
            Map<Long, Message> auxMap = new HashMap<>();
            auxMap.put(msg.getId(), msg);
            feeds.put(user, auxMap);
        }

		return msg.getId();
	}

	@Override
	public void removeFromPersonalFeed(String user, long mid, String pwd) {
		Log.info("removeFromPersonalFeed : " + user);

		// Check if user data is valid PERGUNTAR PROF
		if(user == null || pwd == null) {
			Log.info("User object invalid.");
			throw new WebApplicationException( Status.BAD_REQUEST );
		}

		Response r = target.path( user )
                    .queryParam(UsersService.PWD, pwd).request()
                    .accept(MediaType.APPLICATION_JSON)
                    .get();

        User userAux = r.readEntity(User.class);

		// Insert user, checking if name already exists
		if(userAux == null || !userAux.getPwd().equals(pwd)) {
			Log.info("Invalid credentials.");
			throw new WebApplicationException( Status.FORBIDDEN );
		}

		Message msg = feeds.get(user).get(mid);

		if (msg == null) {
			Log.info("Message does not exist in the server.");
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		feeds.get(user).remove(mid);

	}

	@Override
	public Message getMessage(String user, long mid) {
		Log.info("getMessage : " + user);

		// Check if user data is valid PERGUNTAR PROF
		if(user == null) {
			Log.info("User object invalid.");
			throw new WebApplicationException( Status.BAD_REQUEST );
		}

        Response r = target.path("/").queryParam( UsersService.QUERY, user).request()
				.accept(MediaType.APPLICATION_JSON)
				.get();

        List<User> userL = r.readEntity(new GenericType<List<User>>() {});

        boolean aux = false;
        for (User u: userL)
            if (u.getName().equals(user))
            aux = true;

		Message msg = feeds.get(user).get(mid);

		if (!aux || msg == null) {
			Log.info("Message or User does not exist in the server.");
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		return msg;
	}

	@Override
	public List<Message> getMessages(String user, long time) {
		//Fazer Remote

		Log.info("getMessages : " + user);

		// Check if user data is valid PERGUNTAR PROF
		if(user == null) {
			Log.info("User object invalid.");
			throw new WebApplicationException( Status.BAD_REQUEST );
		}

		Map<Long, Message> msgs = feeds.get(user);

		if (msgs == null) {
			Log.info("User does not exist.");
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		List<Message> msgsToReturn = new ArrayList<>();

		for (Message m: msgs.values()) {
			if (m.getCreationTime() >= time)
				msgsToReturn.add(m);
		}

		return msgsToReturn;
	}

	@Override
	public void subUser(String user, String userSub, String pwd) {
		//Fazer Remote
		Log.info("subUser : " + user);

		if(user == null) {
			Log.info("User object invalid.");
			throw new WebApplicationException( Status.BAD_REQUEST );
		}

		User userToSub = users.get(userSub);

		if(userToSub == null) {
			Log.info("User to subscribe does not exist.");
			throw new WebApplicationException( Status.NOT_FOUND);
		}

		User userSubbing = users.get(user);
		if (userSubbing==null || !userSubbing.getPwd().equals(pwd)){
			Log.info("Invalid Credentials.");
			throw new WebApplicationException(Status.FORBIDDEN);
		}

		subs.get(user).put(userSub, userToSub);

	}

	@Override
	public void unsubscribeUser(String user, String userSub, String pwd) {
		//Fazer Remote
		Log.info("subUser : " + user);

		if(user == null) {
			Log.info("User object invalid.");
			throw new WebApplicationException( Status.BAD_REQUEST );
		}

		User userUnsubbing = users.get(user);

		if (userUnsubbing==null || !userUnsubbing.getPwd().equals(pwd)){
			Log.info("Invalid Credentials.");
			throw new WebApplicationException(Status.FORBIDDEN);
		}

		if (!subs.get(user).containsKey(userSub)){
			Log.info("User is not subscribed.");
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		subs.get(user).remove(userSub);
	}

	@Override
	public List<String> listSubs(String user) {
		return new LinkedList<>(subs.get(user).keySet());
	}

    
}

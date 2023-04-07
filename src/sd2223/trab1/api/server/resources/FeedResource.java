package sd2223.trab1.api.server.resources;

import java.net.URI;
import java.util.*;
import java.util.logging.Logger;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;
import sd2223.trab1.api.api.Discovery;
import sd2223.trab1.api.api.Message;
import sd2223.trab1.api.api.User;
import sd2223.trab1.api.api.rest.FeedsService;
import sd2223.trab1.api.clients.user.RestUsersClient;

public class FeedResource implements FeedsService{
    
    private final Map<String, Map<Long,Message>> feeds = new HashMap<>(); //perguntar ao prof qual e a melhor opcao: so String ou User
	private final Map<String, Map<String, User>> subs = new HashMap<>();
    private static Logger Log = Logger.getLogger(UserResource.class.getName());
	private Discovery discovery = Discovery.getInstance();
	private long number, counter;

    public FeedResource(){
		this.number = (long) (Math.random() * Math.random() * 170000);
		counter = 0;
    }

    @Override
	public long postMessage(String user, String pwd, Message msg) {
		Log.info("postMessage : " + user);
		
		// Check if user data is valid
		if(user == null || pwd == null) {
			Log.info("User object invalid.");
			throw new WebApplicationException( Status.BAD_REQUEST );
		}

		String[] arr = user.split("@");
        String aux = "users." + arr[1];
		URI[] uris = discovery.knownUrisOf(aux, 1);

        User userAux = new RestUsersClient(uris[uris.length-1]).getUser(arr[0], pwd);

		// Insert user, checking if name already exists
		if(userAux == null || !userAux.getPwd().equals(pwd)) {
			Log.info("Invalid credentials.");
			throw new WebApplicationException( Status.FORBIDDEN );
		}

		msg.setId((counter++) * 256 + number);

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

		// Check if user data is valid
		if(user == null || pwd == null) {
			Log.info("User object invalid.");
			throw new WebApplicationException( Status.BAD_REQUEST );
		}

		String[] arr = user.split("@");
        String aux = "users." + arr[1];
		URI[] uris = discovery.knownUrisOf(aux, 1);

		User userAux = new RestUsersClient(uris[uris.length-1]).getUser(arr[0], pwd);

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

		// Check if user data is valid
		if(user == null) {
			Log.info("User object invalid.");
			throw new WebApplicationException( Status.BAD_REQUEST );
		}

		String[] arr = user.split("@");
        String aux = "users." + arr[1];
		URI[] uris = discovery.knownUrisOf(aux, 0);

        List<User> listUsers = new RestUsersClient(uris[uris.length-1]).searchUsers(arr[0]);

		Message msg = feeds.get(user).get(mid);

		if (searchUser(listUsers, user) == null || msg == null) {
			Log.info("Message or User does not exist in the server.");
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		return msg;
	}

	private User searchUser(List<User> listUsers, String user) {
		for (User u: listUsers)
			if (u.getName().equals(user))
				return u;

		return null;
	}

	@Override
	public List<Message> getMessages(String user, long time) {
		//Fazer Remote

		Log.info("getMessages : " + user);

		// Check if user data is valid
		if(user == null) {
			Log.info("User object invalid.");
			throw new WebApplicationException( Status.BAD_REQUEST );
		}

		String[] arr = user.split("@");
        String aux = "users." + arr[1];
		URI[] uris = discovery.knownUrisOf(aux, 0);

		List<User> listUsers = new RestUsersClient(uris[uris.length-1]).searchUsers(arr[0]);

		if (searchUser(listUsers, user) == null) {
			Log.info("User does not exist.");
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		Map<Long, Message> msgs = feeds.get(user);

		List<Message> msgsToReturn = new LinkedList<>();

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

		String[] arr = userSub.split("@");
        String aux = "users." + arr[1];
		URI[] uris = discovery.knownUrisOf(aux, 0);

		List<User> listUsers = new RestUsersClient(uris[uris.length-1]).searchUsers(arr[0]);
		User userToSub = searchUser(listUsers, userSub);

		if(userToSub == null) {
			Log.info("User to subscribe does not exist.");
			throw new WebApplicationException( Status.NOT_FOUND);
		}

		arr = user.split("@");
        aux = "users." + arr[1];
		uris = discovery.knownUrisOf(aux, 0);

		User userSubbing = new RestUsersClient(uris[uris.length-1]).getUser(arr[0], pwd);
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

		String[] arr = user.split("@");
        String aux = "users." + arr[1];
		URI[] uris = discovery.knownUrisOf(aux, 0);

		User userUnsubbing = new RestUsersClient(uris[uris.length-1]).getUser(arr[0], pwd);

		arr = userSub.split("@");
        aux = "users." + arr[1];
		uris = discovery.knownUrisOf(aux, 0);

		User userToUnsub = new RestUsersClient(uris[uris.length-1]).getUser(arr[0], pwd);

		if (userUnsubbing == null || userToUnsub == null){
			Log.info("User is not subscribed.");
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		
		if (userUnsubbing == null || !userUnsubbing.getPwd().equals(pwd)){
			Log.info("Invalid Credentials.");
			throw new WebApplicationException(Status.FORBIDDEN);
		}

		subs.get(user).remove(userSub);
	}

	@Override
	public List<String> listSubs(String user) {
		return new LinkedList<>(subs.get(user).keySet());
	}

    
}

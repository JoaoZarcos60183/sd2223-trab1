package sd2223.trab1.api.server.resources;

import java.util.*;
import java.util.logging.Logger;

import sd2223.trab1.api.api.Discovery;
import sd2223.trab1.api.api.Message;
import sd2223.trab1.api.api.User;
import sd2223.trab1.api.api.rest.UsersService;
import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;
import sd2223.trab1.api.api.rest.FeedsService;

@Singleton
public class AppResource implements UsersService, FeedsService {

	private final Map<String,User> users = new HashMap<>();
	private final Map<String, Map<Long,Message>> feeds = new HashMap<>(); //perguntar ao prof qual e a melhor opcao: so String ou User
	private final Map<String, Map<String, User>> subs = new HashMap<>();
	private static Logger Log = Logger.getLogger(AppResource.class.getName());
	Discovery discovery = Discovery.getInstance();
	
	public AppResource() {
	}

	@Override
	public String createUser(User user) {
		Log.info("createUser : " + user);
		
		// Check if user data is valid
		if(user.getName() == null || user.getPwd() == null || user.getDisplayName() == null || user.getDomain() == null) {
			Log.info("User object invalid.");
			throw new WebApplicationException( Status.BAD_REQUEST );
		}
		
		// Insert user, checking if name already exists
		if( users.putIfAbsent(user.getName(), user) != null ) {
			Log.info("User already exists.");
			throw new WebApplicationException( Status.CONFLICT );
		}

		feeds.put(user.getName(), new HashMap<>());
		subs.put(user.getName(), new HashMap<>());

		return user.getName();
	}
	
	@Override
	public User getUser(String name, String pwd) {
		Log.info("getUser : user = " + name + "; pwd = " + pwd);

		// Check if user is valid
		if(name == null || pwd == null) {
			Log.info("Name or Password null.");
			throw new WebApplicationException( Status.BAD_REQUEST );
		}

		User user = users.get(name);
		// Check if user exists
		if( user == null ) {
			Log.info("User does not exist.");
			throw new WebApplicationException( Status.NOT_FOUND );
		}

		//Check if the password is correct
		if( !user.getPwd().equals( pwd)) {
			Log.info("Password is incorrect.");
			throw new WebApplicationException( Status.FORBIDDEN );
		}

		return user;
	}

	@Override
	public User updateUser(String userId, String password, User user) {
		Log.info("updateUser : user = " + userId + "; pwd = " + password + " ; user = " + user);
		// TODO Complete method
		// Check if user is valid
		if(userId == null || password == null) {
			Log.info("UserId or password null.");
			throw new WebApplicationException( Status.BAD_REQUEST );
		}

		var u = users.get(userId);

		// Check if user exists
		if( u == null ) {
			Log.info("User does not exist.");
			throw new WebApplicationException( Status.NOT_FOUND );
		}

		//Check if the password is correct
		if( !u.getPwd().equals( password)) {
			Log.info("Password is incorrect.");
			throw new WebApplicationException( Status.FORBIDDEN );
		}

		users.replace(userId, user);

		return users.get(userId);
	}


	@Override
	public User deleteUser(String userId, String password) {
		Log.info("deleteUser : user = " + userId + "; pwd = " + password);
		// TODO Complete method
		// Check if user is valid
		if(userId == null || password == null) {
			Log.info("UserId or password null.");
			throw new WebApplicationException( Status.BAD_REQUEST );
		}

		var user = users.get(userId);

		// Check if user exists
		if( user == null ) {
			Log.info("User does not exist.");
			throw new WebApplicationException( Status.NOT_FOUND );
		}

		//Check if the password is correct
		if( !user.getPwd().equals( password)) {
			Log.info("Password is incorrect.");
			throw new WebApplicationException( Status.FORBIDDEN );
		}

		users.remove(userId);
		feeds.remove(userId);
		subs.remove(userId);

		return user;
	}


	@Override
	public List<User> searchUsers(String pattern) {
		Log.info("searchUsers : pattern = " + pattern);
		// TODO Complete method
		List<User> aux = new ArrayList<>();
		String p = pattern.toUpperCase();

		for (String ID: users.keySet()) {
			User u = users.get(ID);

			if (u.getName().toUpperCase().contains(p))
				aux.add(users.get(ID));
		}

		if (!aux.isEmpty())
			return aux;
		else
			throw new WebApplicationException(Status.NO_CONTENT);
	}

	@Override
	public long postMessage(String user, String pwd, Message msg) {
		Log.info("postMessage : " + user);

		// Check if user data is valid
		if(user == null || pwd == null) {
			Log.info("User object invalid.");
			throw new WebApplicationException( Status.BAD_REQUEST );
		}

		User userAux = users.get(user);

		// Insert user, checking if name already exists
		if(userAux == null || !userAux.getPwd().equals(pwd)) {
			Log.info("Invalid credentials.");
			throw new WebApplicationException( Status.FORBIDDEN );
		}

		feeds.get(user).put(msg.getId(), msg);

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

		User userAux = users.get(user);

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

		Message msg = feeds.get(user).get(mid);

		if (msg == null) {
			Log.info("Message does not exist in the server.");
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

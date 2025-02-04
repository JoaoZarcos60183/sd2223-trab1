package sd2223.trab1.api.server.resources;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import sd2223.trab1.api.api.Discovery;
import sd2223.trab1.api.api.User;
import sd2223.trab1.api.api.rest.UsersService;
import sd2223.trab1.api.clients.feed.RestMessageClient;
import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;

@Singleton
public class UserResource implements UsersService { // Servidor e cliente comunicam através das interfaces

	private final ConcurrentMap<String,User> users = new ConcurrentHashMap<>();
	private static Logger Log = Logger.getLogger(UserResource.class.getName());
	
	public UserResource() {
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

		System.out.println(user.getName());
		return user.getName() + "@" + user.getDomain();
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

		if (!userId.equals(user.getName()))
			throw new WebApplicationException(Status.BAD_REQUEST);


		if (user.getDisplayName() == null)
			user.setDisplayName(u.getDisplayName());

		if (user.getDomain() == null)
			user.setDomain(u.getDomain());

		if (user.getPwd() == null)
			user.setPwd(u.getPwd());


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

		Discovery discovery = Discovery.getInstance();
		String aux = "feeds." + user.getDomain();
		URI[] uris = discovery.knownUrisOf(aux, 0);
		new RestMessageClient(uris[uris.length-1]).deleteInfo(userId + "@" + user.getDomain());

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
			return new LinkedList<>();
		
	}

}

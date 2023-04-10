package sd2223.trab1.api.server.resources;

import java.net.URI;
import java.util.*;
import java.util.logging.Logger;

import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;
import sd2223.trab1.api.api.Discovery;
import sd2223.trab1.api.api.Message;
import sd2223.trab1.api.api.User;
import sd2223.trab1.api.api.rest.FeedsService;
import sd2223.trab1.api.clients.feed.RestMessageClient;
import sd2223.trab1.api.clients.user.RestUsersClient;

@Singleton
public class FeedResource implements FeedsService{
    
    private Map<String, Map<Long,Message>> feeds = new HashMap<>();
	private Map<String, Map<String, User>> subs = new HashMap<>();
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
		
		String[] arr = user.split("@");

		// Check if user data is valid
		if(user == null || pwd == null || !arr[0].equals(msg.getUser()) || !arr[1].equals(msg.getDomain())) {
			Log.info("Something is not right.");
			throw new WebApplicationException( Status.BAD_REQUEST );
		}

		
        String aux = "users." + arr[1];
		URI[] uris = discovery.knownUrisOf(aux, 1);

		List<User> list = new RestUsersClient(uris[uris.length-1]).searchUsers(arr[0]);

        User userAux = searchUser(list, arr[0]);

		// Insert user, checking if name already exists
		if(userAux == null) {
			Log.info("User does not exist in current domain.");
			throw new WebApplicationException( Status.NOT_FOUND );
		}

		if (!userAux.getPwd().equals(pwd)){
			Log.info("Wrong Passsword.");
			throw new WebApplicationException( Status.FORBIDDEN );
		}

		msg.setId((counter++) * 256 + number);

        if (feeds.containsKey(userAux.getName()))
		    feeds.get(arr[0]).put(msg.getId(), msg);
        else{
            Map<Long, Message> auxMap = new HashMap<>();
            auxMap.put(msg.getId(), msg);
            feeds.put(arr[0], auxMap);
        }

		System.out.println(msg.getId());
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

		Message msg = feeds.get(arr[0]).get(mid);

		if (msg == null) {
			Log.info("Message does not exist in the server.");
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		feeds.get(arr[0]).remove(mid);

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

		Map<Long, Message> mMsg = feeds.get(arr[0]);

		Message msg = null;

		if (searchUser(listUsers, arr[0]) == null || mMsg == null || (msg = mMsg.get(mid))  == null) {

			if(searchUser(listUsers, arr[0]) != null){
				Map<String, User> userSubs = subs.get(arr[0]);

				if (userSubs != null)
					for (User u: userSubs.values()){
						System.out.println(u.getName());
						aux = "feeds." + u.getDomain();
						uris = discovery.knownUrisOf(aux, 1);
						List<Message> auxList = new RestMessageClient(uris[uris.length-1]).getSelfMessages(u.getName() + "@" + u.getDomain(), 0);
						msg = searchMessage(auxList, mid);
						if (msg != null){
							break;
						}
					}
			}

			if (msg == null){

				aux = "feeds." + arr[1];
				uris = discovery.knownUrisOf(aux, 0);
				msg = new RestMessageClient(uris[uris.length - 1]).getMessage(user, mid);

				if (msg == null){
					Log.info("Message or User does not exist in the server.");
					throw new WebApplicationException(Status.NOT_FOUND);
				}
			}
		}

		return msg;
	}

	private User searchUser(List<User> listUsers, String user) {
		for (User u: listUsers)
			if (u.getName().equals(user))
				return u;

		return null;
	}

	private Message searchMessage(List<Message> listMessages, long id) {
		for (Message m: listMessages){
			System.out.println(m.getId());
			if (m.getId() == id)
				return m;
	}

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

		if (searchUser(listUsers, arr[0]) == null) {
			Log.info("User does not exist.");
			throw new WebApplicationException(Status.NOT_FOUND);
		}

        aux = "feeds." + arr[1];
		uris = discovery.knownUrisOf(aux, 0);

		return new RestMessageClient(uris[uris.length - 1]).getRealMessages(user, time);
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
		String subbed = arr[0];

		List<User> listUsers = new RestUsersClient(uris[uris.length-1]).searchUsers(subbed);
		User userToSub = searchUser(listUsers, subbed);

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


		if (subs.containsKey(arr[0]))
			subs.get(arr[0]).put(subbed, userToSub);
        else{
            Map<String, User> auxMap = new HashMap<>();
            auxMap.put(subbed, userToSub);
            subs.put(arr[0], auxMap);
        }

	}

	@Override
	public void unsubscribeUser(String user, String userSub, String pwd) {
		//Fazer Remote
		Log.info("UnsubUser : " + user);

		if(user == null) {
			Log.info("User object invalid.");
			throw new WebApplicationException( Status.BAD_REQUEST );
		}

		String[] arr = user.split("@");
        String aux = "users." + arr[1];
		URI[] uris = discovery.knownUrisOf(aux, 0);
		String unsubbing = arr[0];

		User userUnsubbing = new RestUsersClient(uris[uris.length-1]).getUser(unsubbing, pwd);

		arr = userSub.split("@");
        aux = "users." + arr[1];
		uris = discovery.knownUrisOf(aux, 0);

		List<User> list = new RestUsersClient(uris[uris.length-1]).searchUsers(arr[0]);
		User userToUnsub = searchUser(list, arr[0]);

		if (userUnsubbing == null || userToUnsub == null){
			Log.info("User is not subscribed.");
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		
		if (userUnsubbing == null || !userUnsubbing.getPwd().equals(pwd)){
			Log.info("Invalid Credentials.");
			throw new WebApplicationException(Status.FORBIDDEN);
		}

		subs.get(unsubbing).remove(arr[0]);
	}

	@Override
	public List<String> listSubs(String user) {
		Log.info("ListSubs of : " + user);
		String[] arr = user.split("@");
		String aux = "users." + arr[1];
		URI[] uris = discovery.knownUrisOf(aux, 0);

		List<User> list = new RestUsersClient(uris[uris.length-1]).searchUsers(arr[0]);

        User userAux = searchUser(list, arr[0]);

		// Insert user, checking if name already exists
		if(userAux == null) {
			Log.info("User does not exist in current domain.");
			throw new WebApplicationException( Status.NOT_FOUND );
		}

		Map<String, User> auxMap = subs.get(arr[0]);

		if (auxMap != null){
			List<String> auxList = new LinkedList<>();
			for (User u: auxMap.values())
				auxList.add(u.getName() + "@" + u.getDomain());
			
			return auxList;
		}
			
		else
			//throw new WebApplicationException(Status.NOT_FOUND);
		return new LinkedList<>();
		}

	@Override
	public List<Message> getSelfMessages(String user, long time) {
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

		if (searchUser(listUsers, arr[0]) == null) {
			Log.info("User does not exist.");
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		Map<Long, Message> msgs = feeds.get(arr[0]);

		List<Message> msgsToReturn = new LinkedList<>();

		if (msgs != null)
			for (Message m: msgs.values()) {
				if (m.getCreationTime() > time)
					msgsToReturn.add(m);
			}

		return msgsToReturn;
	}

	@Override
	public List<Message> getRealMessages(String user, long time) {

		String[] arr = user.split("@");

		Map<Long, Message> msgs = feeds.get(arr[0]);

		List<Message> msgsToReturn = new LinkedList<>();

		if (msgs !=null)
			for (Message m: msgs.values()) {
				if (m.getCreationTime() > time)
					msgsToReturn.add(m);
			}

		Map<String, User> userSubs = subs.get(arr[0]);

		if (userSubs != null)
			for (User u: userSubs.values()){
				String aux = "feeds." + u.getDomain();
				URI[] uris = discovery.knownUrisOf(aux, 1);
				List<Message> auxList = new RestMessageClient(uris[uris.length-1]).getSelfMessages(u.getName() + "@" + u.getDomain(), time);
				msgsToReturn.addAll(auxList);
			}

			return msgsToReturn;
	}

    
}

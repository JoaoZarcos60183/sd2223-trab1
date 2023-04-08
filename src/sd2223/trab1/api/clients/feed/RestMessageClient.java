package sd2223.trab1.api.clients.feed;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import sd2223.trab1.api.api.Message;
import sd2223.trab1.api.api.rest.FeedsService;
import sd2223.trab1.api.clients.RestClient;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import sd2223.trab1.api.api.User;

public class RestMessageClient extends RestClient implements FeedsService {

	final WebTarget target;
	
	RestMessageClient( URI serverURI ) {
		super( serverURI );
		target = client.target( serverURI ).path( FeedsService.PATH );
	}

    private long clt_createMessage(String user, String pwd, Message message) {
		Response r = target.path( user )
                .queryParam(FeedsService.PWD, pwd).request()
				.post(Entity.entity(message, MediaType.APPLICATION_JSON));

		if( r.getStatus() == Status.OK.getStatusCode() && r.hasEntity() )
			return r.readEntity(Long.class);
		else if (r.getStatus() == Status.FORBIDDEN.getStatusCode())
			System.out.println("Error, HTTP error status: " + r.getStatus() );
        else 
            System.out.println("Error, HTTP error status: " + Status.BAD_REQUEST);
		
		return -1;
	}

    private int clt_removeFromPersonalFeed(String user, long mid, String pwd) {
        Response r = target.path( user ).path(String.valueOf(mid))
                .queryParam(FeedsService.PWD, pwd).request()
				.delete();
        
        if( r.getStatus() == Status.NO_CONTENT.getStatusCode())
            System.out.println("Deleted Post.");
        else if (r.getStatus() == Status.FORBIDDEN.getStatusCode())
            System.out.println("Error, HTTP error status: " + r.getStatus() );
        else 
            System.out.println("Error, HTTP error status: " + Status.NOT_FOUND);

        return 0;
    }

    private Message clt_getMessage(String user, long mid) {
        Response r = target.path( user + "/" + mid ).request()
                    .accept(MediaType.APPLICATION_JSON)
                    .get();
        
        if( r.getStatus() == Status.OK.getStatusCode() && r.hasEntity() ){
            System.out.println("Message Obtained."); 
            return r.readEntity(Message.class);
        }
        else if (r.getStatus() == Status.FORBIDDEN.getStatusCode())
            System.out.println("Error, HTTP error status: " + r.getStatus() );
        else 
            System.out.println("Error, HTTP error status: " + Status.NOT_FOUND);

        return null;
    }

    private List<Message> clt_getMessages(String user, long time) {
        Response r = target.path( user )
                    .queryParam(FeedsService.TIME, time).request()
                    .accept(MediaType.APPLICATION_JSON)
                    .get();
        
        if( r.getStatus() == Status.OK.getStatusCode() && r.hasEntity() ){
            System.out.println("Messages Obtained."); 
            return r.readEntity(new GenericType<List<Message>>() {});
        }
        else if (r.getStatus() == Status.NOT_FOUND.getStatusCode())
            System.out.println("Error, HTTP error status: " + r.getStatus() );

        return new LinkedList<>();
    }

    private int clt_subUser(String user, String userSub, String pwd) {
        Response r = target.path( "/sub/" + user + "/" + userSub )
                    .queryParam(FeedsService.PWD, pwd).request()
                    .accept(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(User.class, MediaType.APPLICATION_JSON));
        
        if( r.getStatus() == Status.NO_CONTENT.getStatusCode()){
            System.out.println("User was subed");
        }
        else if (r.getStatus() == Status.NOT_FOUND.getStatusCode())
            System.out.println("Error, HTTP error status: " + r.getStatus() );
        else if (r.getStatus() == Status.FORBIDDEN.getStatusCode())
            System.out.println("Error, HTTP error status: " + r.getStatus());
        
            System.out.println(r.getStatus());

        return 0;
    }

    private int clt_unsubUser(String user, String userSub, String pwd) {
        Response r = target.path( "/sub/" + user + "/" + userSub )
                    .queryParam(FeedsService.PWD, pwd).request()
                    .accept(MediaType.APPLICATION_JSON)
                    .delete();
        
        if( r.getStatus() == Status.NO_CONTENT.getStatusCode()){
            System.out.println("User was unsubed");
        }
        else if (r.getStatus() == Status.NOT_FOUND.getStatusCode())
            System.out.println("Error, HTTP error status: " + r.getStatus() );
        else if (r.getStatus() == Status.FORBIDDEN.getStatusCode())
            System.out.println("Error, HTTP error status: " + r.getStatus());

        System.out.println(r.getStatus());

        return 0;
    }

    private List<String> clt_listSubs(String user) {
        Response r = target.path("/sub/list/" + user)
                    .request()
                    .accept(MediaType.APPLICATION_JSON)
                    .get();
        
        if( r.getStatus() == Status.OK.getStatusCode() && r.hasEntity() ){
            System.out.println("The list:");
            return r.readEntity(new GenericType<List<String>>() {});
        }
        else if (r.getStatus() == Status.NOT_FOUND.getStatusCode())
            System.out.println("Error, HTTP error status: " + r.getStatus() );

        System.out.println(r.getStatus());
        
        return new LinkedList<>();
    }

    @Override
    public long postMessage(String user, String pwd, Message msg) {
        return super.reTry(() -> clt_createMessage(user, pwd, msg));
    }

    @Override
    public void removeFromPersonalFeed(String user, long mid, String pwd) {
        super.reTry(() -> clt_removeFromPersonalFeed(user, mid, pwd));
    }

    @Override
    public Message getMessage(String user, long mid) {
        return super.reTry(() -> clt_getMessage(user, mid));
    }

    @Override
    public List<Message> getMessages(String user, long time) {
        return super.reTry(() -> clt_getMessages(user, time));
    }

    @Override
    public void subUser(String user, String userSub, String pwd) {
        super.reTry(() -> clt_subUser(user, userSub, pwd));
    }

    @Override
    public void unsubscribeUser(String user, String userSub, String pwd) {
        super.reTry(() -> clt_unsubUser(user, userSub, pwd));
    }

    @Override
    public List<String> listSubs(String user) {
        return super.reTry(() -> clt_listSubs(user));
    }
}

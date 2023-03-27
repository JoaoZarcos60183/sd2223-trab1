package sd2223.trab1.api.clients.feed;

import java.net.URI;
import java.util.List;

import sd2223.trab1.api.api.Message;
import sd2223.trab1.api.api.rest.FeedsService;
import sd2223.trab1.api.api.rest.UsersService;
import sd2223.trab1.api.clients.RestClient;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

public class RestMessageClient extends RestClient implements FeedsService {

	final WebTarget target;
	
	RestMessageClient( URI serverURI ) {
		super( serverURI );
		target = client.target( serverURI ).path( FeedsService.PATH );
	}

    private long clt_createMeesage(String user, String pwd, Message message) {
		
		Response r = target.path( user )
                .queryParam(UsersService.PWD, pwd).request()
				.post(Entity.entity(message, MediaType.APPLICATION_JSON));

		if( r.getStatus() == Status.OK.getStatusCode() && r.hasEntity() )
			return r.readEntity(Long.class);
		else if (r.getStatus() == Status.FORBIDDEN.getStatusCode())
			System.out.println("Error, HTTP error status: " + r.getStatus() );
        else 
            System.out.println("Error, HTTP error status: " + Status.BAD_REQUEST);
		
		return -1;
	}

    public int clt_removeFromPersonalFeed(String user, long mid, String pwd) {
        Response r = target.path( user + "/" + mid )
                .queryParam(UsersService.PWD, pwd).request()
				.delete();
        
        if( r.getStatus() == Status.NO_CONTENT.getStatusCode() && r.hasEntity() )
            System.out.println("Deleted Post.");
        else if (r.getStatus() == Status.FORBIDDEN.getStatusCode())
            System.out.println("Error, HTTP error status: " + r.getStatus() );
        else 
            System.out.println("Error, HTTP error status: " + Status.NOT_FOUND);

        return 0;
    }

    @Override
    public long postMessage(String user, String pwd, Message msg) {
        return super.reTry(() -> clt_createMeesage(user, pwd, msg));
    }

    @Override
    public void removeFromPersonalFeed(String user, long mid, String pwd) {
        // TODO Auto-generated method stub
        super.reTry(() -> clt_removeFromPersonalFeed(user, mid, pwd));
    }

    @Override
    public Message getMessage(String user, long mid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMessage'");
    }

    @Override
    public List<Message> getMessages(String user, long time) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMessages'");
    }

    @Override
    public void subUser(String user, String userSub, String pwd) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'subUser'");
    }

    @Override
    public void unsubscribeUser(String user, String userSub, String pwd) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'unsubscribeUser'");
    }

    @Override
    public List<String> listSubs(String user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listSubs'");
    }
}

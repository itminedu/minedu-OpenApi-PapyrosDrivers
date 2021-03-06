package gr.mimedu.papyros.protocol;

import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import gr.mimedu.papyros.protocol.exceptions.AuthenticateException;
import gr.minedu.papyros.protocol.dto.ApiKey;
import gr.minedu.papyros.protocol.dto.Credentials;
import gr.minedu.papyros.protocol.dto.ErrorReport;
import gr.mineedu.papyros.protocol.idto.Config;
import gr.mineedu.papyros.protocol.idto.OpenPapyrosServices;

public class EAuthClient {
	Config conf = new Config();
	private static final Logger logger = Logger.getLogger(EAuthClient.class.getName());
	
	
	public ApiKey auth(String username, String password) throws AuthenticateException{
		ApiKey apikey = null;
		Client client = ClientBuilder.newClient();
		String targetHost=conf.getServerurl();
		String path = OpenPapyrosServices.EAuth.getValue();
		logger.fine("path:"+path);
		WebTarget target = client.target(targetHost).path(path);
		logger.fine("target:"+target);
        Builder builder =   target.request();
        Credentials c = new Credentials ();c.setUsername(username); c.setPassword(password);
        Response response  =  builder.accept(MediaType.APPLICATION_JSON).put(Entity.entity(new Gson().toJson(c),MediaType.APPLICATION_JSON));// put(String.class);
        String responseStr = response.readEntity(String.class);
        logger.finest(responseStr);
        if(response.getStatus()==Response.Status.OK.getStatusCode()){
        	apikey = new Gson().fromJson(responseStr,ApiKey.class) ;
        	logger.finest("apikey:"+apikey);
        }
        else{
        	ErrorReport errorReport =  new Gson().fromJson(responseStr,ErrorReport.class) ;	
        	throw new AuthenticateException(errorReport.getErrorCode(),errorReport.getErrorMessage());
        }
        return apikey;
	}
}

package SomeGame.TestSimulationService;

import java.net.URI;

import micronet.model.ID;	
import micronet.network.Context;
import micronet.network.IPeer;
import micronet.network.Request;
import micronet.network.Response;
import micronet.network.StatusCode;
import micronet.network.factory.PeerFactory;

public class TestSimulationService {

	public static void main(String[] args) {
		
		IPeer peer = PeerFactory.createPeer();
		
		peer.listen("mn://instance/free", request -> {
			
			ID regionID = new ID(request.getParameters().getString("REGION_ID"));
			int port = request.getParameters().getInt("PORT");
			
			Context context = new Context(peer);
			
			Response regionResponse = context.sendRequestBlocking("mn://region/get", new Request(regionID.toString()));
			if (regionResponse.getStatus() != StatusCode.OK) 
				return new Response(StatusCode.NOT_FOUND, "Region unknown");

			Request openRequest = new Request();
			openRequest.getParameters().set("ID", context.getPeer().getConnectionID());
			openRequest.getParameters().set("REGION_ID", regionID.toString());
			openRequest.getParameters().set("HOST", "localhost");
			openRequest.getParameters().set("PORT", port);
			
			context.getPeer().sendRequest(URI.create("mn://world/instance/open"), openRequest, response -> {
				System.out.println("Instance opened " + response);
			});
			return regionResponse;
		});
		
		
		
		



	}
}

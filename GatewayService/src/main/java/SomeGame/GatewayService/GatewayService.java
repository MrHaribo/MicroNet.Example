package SomeGame.GatewayService;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import micronet.activemq.AMQGatewayPeer;
import micronet.annotation.MessageListener;
import micronet.annotation.MessageService;
import micronet.annotation.OnStart;
import micronet.annotation.OnStop;
import micronet.model.CredentialValues;
import micronet.model.ParameterCode;
import micronet.model.UserValues;
import micronet.network.Context;
import micronet.network.Request;
import micronet.network.Response;
import micronet.network.StatusCode;
import micronet.serialization.Serialization;

@MessageService(uri="mn://gateway")
public class GatewayService {

	private AMQGatewayPeer gatewayPeer; 

	// TODO: Synchronize these
	private Map<String, ClientConnection> connections = Collections.synchronizedMap(new HashMap<String, ClientConnection>());

	@OnStart
	public void onStart(Context context) {
		gatewayPeer = new AMQGatewayPeer((String id) -> clientDisconnected(context, id));
		gatewayPeer.listen(URI.create("mn://cmd"), (String clientId, Request request) -> clientCmd(context, clientId, request));
		gatewayPeer.listen(URI.create("mn://request"), (String clientId, Request request) -> clientRequest(context, clientId, request));
	}
	
	@OnStop
	public void onStop(Context context) {
		
	}
	
	@MessageListener(uri="/forward/event")
	public void forwardEvent(Context context, Request request) {
		// TODO: Dont send back userId (security)
		int userId = request.getParameters().getInt(ParameterCode.USER_ID);

		// TODO: Speed up lookup
		for (ClientConnection connection : connections.values()) {
			if (connection.getUserId() == userId) {
				gatewayPeer.sendRequest(URI.create(connection.getQueueURI() + "/event"), request);
				break;
			}
		}
	}
	
	@MessageListener(uri="/broadcast/event")
	public void broadcastEvent(Context context, Request request) {
		for (ClientConnection connection : connections.values()) {
			gatewayPeer.sendRequest(URI.create(connection.getQueueURI() + "/event"), request);
		}
	}

	private void clientDisconnected(Context context, String clientId) {
		ClientConnection client = connections.remove(clientId);
		if (client != null) {
			context.getAdvisory().send("User.Disconnected", Integer.toString(client.getUserId()));
			System.out.println("Client Disconnected: " + clientId + " Connection Left: " + connections.size());
		}
	}

	private void clientCmd(Context context, String clientId, Request request) {
		ClientConnection connection = connections.get(clientId);
		if (connection == null)
			return;

		System.out.println("CMD RequestorId: " + connection.getUserId());
		Request forwardRequest = new Request(request.getData());
		forwardRequest.getParameters().set(ParameterCode.USER_ID, connection.getUserId());
		String userRequest = request.getParameters().getString(ParameterCode.USER_REQUEST);
		context.sendRequest(userRequest, forwardRequest);
	}

	private Response clientRequest(Context context, String connectionId, Request request) {

		ClientConnection connection = connections.get(connectionId);
		if (connection == null) {
			connection = new ClientConnection(connectionId);
			connections.put(connectionId, connection);
			// TODO: timeout for unauthenticated connections
		}

		String userRequest = request.getParameters().getString(ParameterCode.USER_REQUEST);
		System.out.println("REQUEST " + userRequest + ": " + request.getData() + " -> " + connectionId);

		// Connected
		if (!connection.isAuthenticated()) {
			switch (userRequest) {
			case "mn://account/register":
				return gatewayPeer.sendRequestBlocking(URI.create(userRequest), request);
			case "mn://account/login":
				CredentialValues credentials = Serialization.deserialize(request.getData(), CredentialValues.class);
				if (isAlreadyLoggedIn(credentials))
					return new Response(StatusCode.FORBIDDEN, "Someone with the same name already logged in");
				
				Response loginResponse = context.sendRequestBlocking(userRequest, request);
				if (loginResponse.getStatus() == StatusCode.OK) {
					UserValues user = Serialization.deserialize(loginResponse.getData(), UserValues.class);
					connection.setAuthenticated(true);
					connection.setUserId(user.getId());
					connection.setCredentials(credentials);

					context.getAdvisory().send("User.Connected", Integer.toString(connection.getUserId()));

					System.out.println("Client connected: " + user.getName() + "-" + connection.getUserId());
					System.out.println("Connection Left: " + connections.size());
					return new Response(StatusCode.OK);
				}
				return loginResponse;
			default:
				return new Response(StatusCode.UNAUTHORIZED, "Only register and login possible");
			}
		} else {
			if (userRequest.equals("mn://account/login"))
				return new Response(StatusCode.FORBIDDEN, "Already logged in");
			if (userRequest.equals("mn://logout/")) {
				clientDisconnected(context, connection.getConnectionId());
				return new Response(StatusCode.OK, "Logged Out");
			}

			System.out.println("Request from Id: " + connection.getUserId());
			request.getParameters().set(ParameterCode.USER_ID, connection.getUserId());
			return context.sendRequestBlocking(userRequest, request);
		}
	}
	
	private boolean isAlreadyLoggedIn(CredentialValues credentials) {
		for (Map.Entry<String, ClientConnection> user : connections.entrySet()){
			CredentialValues existingCredentials = user.getValue().getCredentials();
			if (existingCredentials != null && existingCredentials.getUsername().equals(credentials.getUsername()))
				return true;
		}
		return false;
	}
}

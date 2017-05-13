package SomeGame.WorldService;

import java.net.URI;
import java.util.UUID;

import micronet.annotation.MessageListener;
import micronet.annotation.MessageService;
import micronet.annotation.OnStart;
import micronet.annotation.OnStop;
import micronet.model.AvatarValues;
import micronet.model.ID;
import micronet.model.IDType;
import micronet.model.ParameterCode;
import micronet.network.Context;
import micronet.network.IAdvisory;
import micronet.network.Request;
import micronet.network.Response;
import micronet.network.StatusCode;
import micronet.serialization.Serialization;

@MessageService(uri="mn://world")
public class WorldService {
	
	InstanceStore instanceStore;

	@OnStart
	public void onStart(Context context) {
		instanceStore = new InstanceStore();
		
		context.getAdvisory().registerConnectionStateListener((String id, IAdvisory.ConnectionState state) -> {
			if (state == IAdvisory.ConnectionState.DISCONNECTED) {
				ID regionID = instanceStore.getRegionFromInstance(id);
				if (regionID != null) {
					removeRegion(context, regionID);
				}
			}
		});
	}
	

	
	@OnStop
	public void onStop(Context context) {
	}

	@MessageListener(uri = "/join")
	public Response joinWorld(Context context, Request request) {
		int userID = request.getParameters().getInt(ParameterCode.USER_ID);
		Response avatarResponse = context.sendRequestBlocking("mn://avatar/current/set", request);
		AvatarValues avatar = Serialization.deserialize(avatarResponse.getData(), AvatarValues.class);
		
		//Avatar is in an old battle
		if (isMatchID(avatar.getRegionID())) {
			return joinWorld(context, userID, avatar.getHomeRegionID(), avatarResponse.getData());
		}
		return joinWorld(context, userID, avatar.getRegionID(), avatarResponse.getData());
	}
	
	@MessageListener(uri = "/travel/home")
	public Response homestone(Context context, Request request) {
		int userID = request.getParameters().getInt(ParameterCode.USER_ID);
		Response avatarResponse = context.sendRequestBlocking("mn://avatar/current/get", request);
		AvatarValues avatar = Serialization.deserialize(avatarResponse.getData(), AvatarValues.class);
		return joinWorld(context, userID, avatar.getHomeRegionID(), avatarResponse.getData());
	}

	@MessageListener(uri = "/travel")
	public Response travel(Context context, Request request) {
		int userID = request.getParameters().getInt(ParameterCode.USER_ID);
		Response avatarResponse = context.sendRequestBlocking("mn://avatar/current/get", request);
		AvatarValues avatar = Serialization.deserialize(avatarResponse.getData(), AvatarValues.class);
		if (avatar.isLanded())
			return new Response(StatusCode.FORBIDDEN, "Must be in Space to Travel");
		return joinWorld(context, userID, new ID(request.getData()), avatarResponse.getData());
	}

	@MessageListener(uri = "/instance/open")
	public Response readyInstance(Context context, Request request) {

		String instanceID = request.getParameters().getString(ParameterCode.ID);
		String regionID = request.getParameters().getString(ParameterCode.REGION_ID);
		String host = request.getParameters().getString(ParameterCode.HOST);
		int port = request.getParameters().getInt(ParameterCode.PORT);
		
		instanceStore.openInstance(new ID(regionID), instanceID, host, port);

		//TODO: Notify Player that they can join
		for (int userID : instanceStore.getQueuedUsers(new ID(regionID))) {
			
			Request avatarRequest = new Request();
			avatarRequest.getParameters().set(ParameterCode.USER_ID, userID);
			Response avatarResponse = context.sendRequestBlocking("mn://avatar/current/get", avatarRequest);
			
			Response joinRegionResponse = joinRegion(context, userID, new ID(regionID), avatarResponse.getData());
			
			Request regionReadyRequest = new Request(avatarResponse.getData());
			regionReadyRequest.getParameters().set(ParameterCode.TOKEN, joinRegionResponse.getParameters().getString(ParameterCode.TOKEN));
			regionReadyRequest.getParameters().set(ParameterCode.REGION_ID, regionID);
			regionReadyRequest.getParameters().set(ParameterCode.HOST, host);
			regionReadyRequest.getParameters().set(ParameterCode.PORT, port);
			context.sendEvent(userID, "OnRegionReady", regionReadyRequest);
		}
		
		System.out.println("Instance Opened: " + regionID + " on: " + instanceID);
		return new Response(StatusCode.OK, "Instance Added");
	}


	@MessageListener(uri = "/instance/close")
	public Response resetInstance(Context context, Request request) {
		String regionID = request.getParameters().getString(ParameterCode.REGION_ID);
		removeRegion(context, new ID(regionID));
		return new Response(StatusCode.OK, "Instance Closed");
	}

	private Response joinWorld(Context context, int userID, ID regionID, String avatarData) {
		// RegionID id = new RegionID(124554051589L);
		// RegionValues region = new RegionValues(id);

		System.out.println("World Join: " + regionID);

		if (instanceStore.isRegionOpen(regionID)) {
			return joinRegion(context, userID, regionID, avatarData);
		} else if (instanceStore.isRegionOpening(regionID)) {
			instanceStore.queueUser(regionID, userID);
		} else {
			Response regionResponse = context.sendRequestBlocking("mn://region/get", new Request(regionID.toString()));
			if (regionResponse.getStatus() != StatusCode.OK) 
				return new Response(StatusCode.NOT_FOUND, "Region unknown");
			
			Response portResponse = context.sendRequestBlocking("mn://port/reserve", new Request());
			if (portResponse.getStatus() != StatusCode.OK)
				return new Response(StatusCode.NO_CONTENT, "No free Ports available");
			
			instanceStore.addInstance(regionID);
			instanceStore.queueUser(regionID, userID);
			
			Request openRequest = new Request(regionResponse.getData());
			openRequest.getParameters().set(ParameterCode.REGION_ID, regionID);
			openRequest.getParameters().set(ParameterCode.PORT, portResponse.getData());
			context.sendRequest("mn://instance-open", openRequest, openResponse -> { 
				System.out.println("Direct Region open Response: " + openResponse.toString());
			});
		}
		return new Response(StatusCode.TEMPORARY_REDIRECT, "Wait for Region to be loaded"); 
	}
	

	private Response joinRegion(Context context, int userID, ID regionID, String avatarData) {
		// Generate PlayerToken (random UUIDs)
		UUID token = UUID.randomUUID();
		Request joinRequest = new Request(avatarData);
		joinRequest.getParameters().set(ParameterCode.USER_ID, userID);
		joinRequest.getParameters().set(ParameterCode.TOKEN, token.toString());

		System.out.println("SENDING JOIN: " + regionID.getAddress() + "/join");
		URI destination = URI.create(regionID.getAddress() + "/join");
		Response joinResponse = context.sendRequestBlocking(destination.toString(), joinRequest);
		if (joinResponse.getStatus() != StatusCode.OK)
			return new Response(StatusCode.UNAUTHORIZED);

		Request setRegionRequest = new Request();
		setRegionRequest.getParameters().set(ParameterCode.USER_ID, userID);
		setRegionRequest.getParameters().set(ParameterCode.REGION_ID, regionID.toString());
		context.sendRequest("mn://avatar/current/update", setRegionRequest);

		Response response = new Response(StatusCode.OK, avatarData);
		response.getParameters().set(ParameterCode.REGION_ID, regionID.toString());
		response.getParameters().set(ParameterCode.TOKEN, token.toString());
		response.getParameters().set(ParameterCode.HOST, instanceStore.getInstanceHost(regionID));
		response.getParameters().set(ParameterCode.PORT, instanceStore.getInstancePort(regionID));

		return response;
	}
	
	private void removeRegion(Context context, ID regionID) {
		int port = instanceStore.getInstancePort(regionID);
		instanceStore.removeInstance(regionID);
		context.sendRequest("mn://port/release", new Request(Integer.toString(port)));
	}
	
	private boolean isMatchID(ID id)
    {
        return id.getType() == IDType.Deathmath || 
    		id.getType()  == IDType.TeamDeathmatch || 
    		id.getType()  == IDType.Domination;
    }
}

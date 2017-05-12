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
import micronet.model.RegionValues;
import micronet.network.Context;
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
			return JoinWorld(context, userID, avatar.getHomeRegionID(), avatarResponse.getData());
		}
		return JoinWorld(context, userID, avatar.getRegionID(), avatarResponse.getData());
	}
	
	@MessageListener(uri = "/travel/home")
	public Response homestone(Context context, Request request) {
		int userID = request.getParameters().getInt(ParameterCode.USER_ID);
		Response avatarResponse = context.sendRequestBlocking("mn://avatar/current/get", request);
		AvatarValues avatar = Serialization.deserialize(avatarResponse.getData(), AvatarValues.class);
		return JoinWorld(context, userID, avatar.getHomeRegionID(), avatarResponse.getData());
	}

	@MessageListener(uri = "/travel")
	public Response travel(Context context, Request request) {
		int userID = request.getParameters().getInt(ParameterCode.USER_ID);
		Response avatarResponse = context.sendRequestBlocking("mn://avatar/current/get", request);
		AvatarValues avatar = Serialization.deserialize(avatarResponse.getData(), AvatarValues.class);
		if (avatar.isLanded())
			return new Response(StatusCode.FORBIDDEN, "Must be in Space to Travel");
		return JoinWorld(context, userID, new ID(request.getData()), avatarResponse.getData());
	}
	
	@MessageListener(uri = "/instance/add")
	public Response addInstance(Context context, Request request) {
		String instanceID = request.getData();
		instanceStore.addInstance(instanceID);
		return new Response(StatusCode.OK, "Instance added");
	}
	
	@MessageListener(uri = "/instance/ready")
	public Response readyInstance(Context context, Request request) {

		String instanceID = request.getParameters().getString(ParameterCode.ID);
		String regionID = request.getParameters().getString(ParameterCode.REGION_ID);
		String host = request.getParameters().getString(ParameterCode.HOST);
		int port = request.getParameters().getInt(ParameterCode.PORT);
		
		instanceStore.readyInstance(instanceID, host, port);

		//TODO: Notify Player that they can join
		for (int userID : instanceStore.getQueuedUsers(new ID(regionID))) {
			Response avatarResponse = context.sendRequestBlocking("mn://avatar/current/get", request);
			
			Response joinRegionResponse = JoinRegion(context, userID, new ID(regionID), avatarResponse.getData());
			
			Request regionReadyRequest = new Request();
			regionReadyRequest.getParameters().set(ParameterCode.REGION_ID, joinRegionResponse.getParameters().getString(ParameterCode.REGION_ID));
			regionReadyRequest.getParameters().set(ParameterCode.TOKEN, joinRegionResponse.getParameters().getString(ParameterCode.TOKEN));
			regionReadyRequest.getParameters().set(ParameterCode.HOST, joinRegionResponse.getParameters().getString(ParameterCode.HOST));
			regionReadyRequest.getParameters().set(ParameterCode.PORT, joinRegionResponse.getParameters().getInt(ParameterCode.REGION_ID));
			context.sendEvent(userID, "OnRegionReady", regionReadyRequest);
		}
		
		System.out.println("Region Opened: " + regionID);
		return new Response(StatusCode.OK, "Instance Added");
	}


	@MessageListener(uri = "/instance/reset")
	public Response resetInstance(Context context, Request request) {
		
		String instanceID = request.getParameters().getString(ParameterCode.ID);
		
		int instancePort = instanceStore.getInstancePort(instanceID);
		context.sendRequest("mn://port/release", new Request(Integer.toString(instancePort)));
		
		instanceStore.resetInstance(instanceID);
		return new Response(StatusCode.OK, "Instance Reused");
	}
	
	@MessageListener(uri = "/instance/open")
	public Response openInstance(Context context, Request request) {
		
		String instanceID = request.getParameters().getString(ParameterCode.ID);
		String regionID = request.getParameters().getString(ParameterCode.REGION_ID);
		
		Response portResponse = context.sendRequestBlocking("mn://port/reserve", new Request());
		if (portResponse.getStatus() != StatusCode.OK) 
			return new Response(StatusCode.SERVICE_UNAVAILABLE, "No free port is available.");
				
		Response regionResponse = context.sendRequestBlocking("mn://region/get", new Request(regionID));
		if (portResponse.getStatus() != StatusCode.OK) 
			return new Response(StatusCode.NOT_FOUND, "Region unknown");
		
		RegionValues region = Serialization.deserialize(regionResponse.getData(), RegionValues.class);

		Request instanceRequest = new Request(regionResponse.getData());
		instanceRequest.getParameters().set(ParameterCode.PORT, portResponse.getData());
		context.sendRequest("mn://" + instanceID + "/open", instanceRequest, response -> {
			System.out.println("Open Response: " + response.getStatus() + " : " + response.getData());
		});

		return new Response(StatusCode.OK, "Started Instance Opening " + region.getID());
	}

	private Response JoinWorld(Context context, int userID, ID regionID, String avatarData) {
		// RegionID id = new RegionID(124554051589L);
		// RegionValues region = new RegionValues(id);

		System.out.println("World Join: " + regionID);

		if (instanceStore.isRegionOpen(regionID)) {
			return JoinRegion(context, userID, regionID, avatarData);
		} else if (instanceStore.isRegionOpening(regionID)) {
			instanceStore.queueUser(regionID, userID);
		} else {
			String instanceID = instanceStore.reserveInstance(regionID);
			//TODO: Start new Instances
			if (instanceID == null)
				return new Response(StatusCode.SERVICE_UNAVAILABLE, "No free instance is available.");
			instanceStore.queueUser(regionID, userID);

			Request openRequest = new Request(regionID.toString());
			openRequest.getParameters().set(ParameterCode.ID, instanceID);
			openRequest.getParameters().set(ParameterCode.REGION_ID, regionID);
			context.sendRequest("mn://world/instance/open", openRequest, openResponse -> { 
				System.out.println("Region open: " + openResponse.toString());
			});
		}
		return new Response(StatusCode.TEMPORARY_REDIRECT, "Wait for Region to be loaded"); 
	}
	

	private Response JoinRegion(Context context, int userID, ID regionID, String avatarData) {
		String regionInstanceID = instanceStore.getRegionInstance(regionID);

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
		response.getParameters().set(ParameterCode.HOST, instanceStore.getInstanceHost(regionInstanceID));
		response.getParameters().set(ParameterCode.PORT, instanceStore.getInstancePort(regionInstanceID));

		return response;
	}
	
	private boolean isMatchID(ID id)
    {
        return id.getType() == IDType.Deathmath || 
    		id.getType()  == IDType.TeamDeathmatch || 
    		id.getType()  == IDType.Domination;
    }
}

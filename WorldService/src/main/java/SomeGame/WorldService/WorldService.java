package SomeGame.WorldService;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import micronet.annotation.MessageListener;
import micronet.annotation.MessageService;
import micronet.annotation.OnStart;
import micronet.annotation.OnStop;
import micronet.model.AvatarValues;
import micronet.model.ID;
import micronet.model.IDType;
import micronet.model.ParameterCode;
import micronet.model.RegionInstanceValues;
import micronet.model.RegionValues;
import micronet.network.Context;
import micronet.network.IAdvisory;
import micronet.network.IAdvisory.QueueState;
import micronet.network.Request;
import micronet.network.Response;
import micronet.network.StatusCode;
import micronet.serialization.Serialization;

@MessageService(uri="mn://world")
public class WorldService {

	private static final ID[] confederateBattleRegions = { new ID(IDType.MasterRegion, (short) 37), new ID(IDType.MasterRegion, (short) 38), };
	private static final ID[] rebelBattleRegions = { new ID(IDType.MasterRegion, (short) 31), new ID(IDType.MasterRegion, (short) 34), };
	
	int battleRegionCount = 42;
	Set<RegionValues> battleRegions = Collections.synchronizedSet(new HashSet<>());

	@OnStart
	public void onStart(Context context) {
		context.getAdvisory().registerQueueStateListener("mn://region", (QueueState state) -> {
			if (state == IAdvisory.QueueState.OPEN) {
				CreateBattleRegions(context);
			}
		});
	}
	
	@OnStop
	public void onStop(Context context) {

	}

	@MessageListener(uri = "/battles/all")
	public Response getAllBattles(Context context, Request request) {
		return new Response(StatusCode.OK, Serialization.serialize(battleRegions));
	}

	@MessageListener(uri = "/join")
	public Response joinWorld(Context context, Request request) {
		int userID = request.getParameters().getInt(ParameterCode.USER_ID);
		Response avatarResponse = context.sendRequestBlocking("mn://avatar/current/set", request);
		AvatarValues avatar = Serialization.deserialize(avatarResponse.getData(), AvatarValues.class);
		return JoinWorld(context, userID, avatar.getRegionID(), avatarResponse.getData());
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

	private void CreateBattleRegions(Context context) {
		battleRegions = Collections.synchronizedSet(new HashSet<>());
		CreateBattleRegions(context, new ID(IDType.Deathmath, (short) battleRegionCount++, confederateBattleRegions[0]));
		CreateBattleRegions(context, new ID(IDType.Deathmath, (short) battleRegionCount++, confederateBattleRegions[1]));
		CreateBattleRegions(context, new ID(IDType.Deathmath, (short) battleRegionCount++, rebelBattleRegions[0]));
		CreateBattleRegions(context, new ID(IDType.Deathmath, (short) battleRegionCount++, rebelBattleRegions[1]));

	}

	private void CreateBattleRegions(Context context, ID battleRegionID) {
		Request getRegionRequest = new Request(battleRegionID.toString());
		context.sendRequest("mn://region/get", getRegionRequest, (Response getRegionResponse) -> {
			RegionValues battleRegion = Serialization.deserialize(getRegionResponse.getData(), RegionValues.class);
			battleRegions.add(battleRegion);
		});
	}

	private Response JoinWorld(Context context, int userID, ID regionID, String avatarData) {
		// RegionID id = new RegionID(124554051589L);
		// RegionValues region = new RegionValues(id);

		System.out.println("World Join: " + regionID);

		Request openRegionRequest = new Request(regionID.toString());
		Response openRegionResponse = context.sendRequestBlocking("mn://region/open", openRegionRequest, 60000);
		if (openRegionResponse.getStatus() != StatusCode.OK)
			return new Response(StatusCode.INTERNAL_SERVER_ERROR, "Failed to Open Region");

		String data = openRegionResponse.getData();
		RegionInstanceValues instance = Serialization.deserialize(data, RegionInstanceValues.class);

		// Generate PlayerToken (random UUIDs)
		UUID token = UUID.randomUUID();
		Request joinRequest = new Request(avatarData);
		joinRequest.getParameters().set(ParameterCode.USER_ID, userID);
		joinRequest.getParameters().set(ParameterCode.TOKEN, token.toString());

		URI destination = URI.create(regionID.getAddress() + "/join");
		Response joinResponse = context.sendRequestBlocking(destination.toString(), joinRequest);
		if (joinResponse.getStatus() != StatusCode.OK)
			return new Response(StatusCode.UNAUTHORIZED);

		Request setRegionRequest = new Request();
		setRegionRequest.getParameters().set(ParameterCode.USER_ID, userID);
		setRegionRequest.getParameters().set(ParameterCode.REGION_ID, regionID.toString());
		context.sendRequest("mn://avatar/update", setRegionRequest);

		Response response = new Response(StatusCode.OK, avatarData);
		response.getParameters().set(ParameterCode.REGION_ID, regionID.toString());
		response.getParameters().set(ParameterCode.TOKEN, token.toString());
		response.getParameters().set(ParameterCode.HOST, instance.getHost());
		response.getParameters().set(ParameterCode.PORT, instance.getPort());

		return response;
	}
}

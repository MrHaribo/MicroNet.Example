package SomeGame.RegionService;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import micronet.annotation.MessageListener;
import micronet.annotation.MessageService;
import micronet.annotation.OnStart;
import micronet.annotation.OnStop;
import micronet.model.ID;
import micronet.model.IDType;
import micronet.model.RegionValues;
import micronet.network.Context;
import micronet.network.Request;
import micronet.network.Response;
import micronet.network.StatusCode;
import micronet.network.IAdvisory.QueueState;
import micronet.serialization.Serialization;

@MessageService(uri="mn://region")
public class RegionService {

	private static final ID[] confederateBattleRegions = { new ID(IDType.MasterRegion, (short) 37), new ID(IDType.MasterRegion, (short) 38), };
	private static final ID[] rebelBattleRegions = { new ID(IDType.MasterRegion, (short) 31), new ID(IDType.MasterRegion, (short) 34), };
	
	int battleRegionCount = 42;
	Set<RegionValues> battleRegions = Collections.synchronizedSet(new HashSet<>());
	
	private RegionDatabase database;

	@OnStart
	public void onStart(Context context) {
		
		database = new RegionDatabase();
		if (System.getenv("region_pool_enabled") != null)
			new RegionPool(context.getAdvisory());
		
		//TODO: Make scalable with Couchbase
		CreateBattleRegions(context);
	}
	
	@OnStop
	public void onStop(Context context) {
		database.shutdown();
	}

	// This at the moment only returns the master region because region
	// generation does not work in java yet
	@MessageListener(uri = "/get")
	public Response getRegion(Context context, Request request) {
		ID regionID = new ID(request.getData());

		RegionValues region = database.getMasterRegion(regionID.getMasterID());
		if (region == null)
			return new Response(StatusCode.NOT_IMPLEMENTED, "Master Region not found");

		region.setID(regionID);
		Response regionResponse = new Response(StatusCode.OK, Serialization.serialize(region));
		return regionResponse;
	}

	@MessageListener(uri = "/all")
	public Response getAllRegions(Context context, Request request) {
		List<String> regions = database.getAllMasterRegionsRaw();
		String data = Serialization.serialize(regions);
		return new Response(StatusCode.OK, data);
	}

	@MessageListener(uri = "/add")
	public Response addRegion(Context context, Request request) {
		RegionValues region = Serialization.deserialize(request.getData(), RegionValues.class);
		database.addMasterRegion(region);
		return new Response(StatusCode.OK);
	}
	
	@MessageListener(uri = "/battles/all")
	public Response getAllBattles(Context context, Request request) {
		return new Response(StatusCode.OK, Serialization.serialize(battleRegions));
	}
	
	private void CreateBattleRegions(Context context) {
		battleRegions = Collections.synchronizedSet(new HashSet<>());
		CreateBattleRegion(context, IDType.Deathmath, confederateBattleRegions[0]);
		CreateBattleRegion(context, IDType.Deathmath, confederateBattleRegions[1]);
		CreateBattleRegion(context, IDType.Deathmath, rebelBattleRegions[0]);
		CreateBattleRegion(context, IDType.Deathmath, rebelBattleRegions[1]);
	}
	
	private void CreateBattleRegion(Context context, byte type, ID masterBattleRegionID) {
		CreateBattleRegion(context, new ID(type, (short) battleRegionCount++, masterBattleRegionID));
	}

	private void CreateBattleRegion(Context context, ID battleRegionID) {
		Response getRegionResponse = getRegion(context, new Request(battleRegionID.toString()));
		RegionValues battleRegion = Serialization.deserialize(getRegionResponse.getData(), RegionValues.class);
		battleRegions.add(battleRegion);
		
		context.broadcastEvent("OnBattleRegionsChanged", Serialization.serialize(battleRegions));
		
		context.getAdvisory().registerQueueStateListener(battleRegion.getID().getURI().toString(), (QueueState state) -> {
			if (state == QueueState.CLOSE) {
				battleRegions.remove(battleRegion);
				context.getAdvisory().unregisterQueueStateListener(battleRegion.getID().getURI().toString());
				CreateBattleRegion(context, battleRegion.getID().getType(), battleRegion.getID().getMasterID());
			}
		});
	}
}

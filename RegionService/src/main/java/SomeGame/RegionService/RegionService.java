package SomeGame.RegionService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import micronet.annotation.MessageListener;
import micronet.annotation.MessageService;
import micronet.annotation.OnStart;
import micronet.annotation.OnStop;
import micronet.model.ID;
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

@MessageService(uri="mn://region")
public class RegionService {

	private Map<String, RegionInstanceValues> openRegions = Collections.synchronizedMap(new HashMap<>());

	private Semaphore regionMapLock = new Semaphore(1, false);
	private Map<String, Semaphore> openingRegionLocks = Collections.synchronizedMap(new HashMap<>());

	private RegionDatabase database;

	@OnStart
	protected void onStart(Context context) {

		database = new RegionDatabase();
		if (System.getenv("region_pool_enabled") != null)
			new RegionPool(context.getAdvisory());
	}
	
	@OnStop
	public void onStop(Context context) {
		database.shutdown();
	}

	@MessageListener(uri = "/open")
	public Response openRegion(Context context, Request request) {
		ID regionID = new ID(request.getData());
		try {
			regionMapLock.acquire();
			RegionInstanceValues regionInstance = openRegions.get(regionID.toString());
			if (regionInstance != null) {
				regionMapLock.release();
				return new Response(StatusCode.OK, Serialization.serialize(regionInstance));
			}

			Semaphore regionLock = openingRegionLocks.get(regionID.toString());
			if (regionLock == null) {
				regionLock = new Semaphore(1, false);
				regionLock.acquire();
				openingRegionLocks.put(regionID.toString(), regionLock);
				regionMapLock.release();

				regionInstance = openRegion(context, regionID);
				if (regionInstance != null) {

					regionMapLock.acquire();
					openingRegionLocks.remove(regionID.toString());
					regionLock.release();
					regionMapLock.release();
					return new Response(StatusCode.OK, Serialization.serialize(regionInstance));
				}
			} else {
				regionMapLock.release();
				regionLock.acquire();

				regionMapLock.acquire();
				regionInstance = openRegions.get(regionID.toString());
				if (regionInstance != null) {
					regionLock.release();
					regionMapLock.release();
					return new Response(StatusCode.OK, Serialization.serialize(regionInstance));
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Response(StatusCode.INTERNAL_SERVER_ERROR, "Error Opening Instance");
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

	public RegionInstanceValues openRegion(Context context, ID regionID) {
		RegionValues region = database.getMasterRegion(regionID.getMasterID());
		region.setID(regionID);

		Request openRequest = new Request(Serialization.serialize(region));
		Response openResponse = context.sendRequestBlocking("mn://freeinstance", openRequest, 60000);
		if (openResponse.getStatus() != StatusCode.OK)
			return null;

		String host = openResponse.getParameters().getString(ParameterCode.HOST);
		int port = openResponse.getParameters().getInt(ParameterCode.PORT);
		RegionInstanceValues regionInstance = new RegionInstanceValues();
		regionInstance.setRegionID(regionID);
		regionInstance.setHost(host);
		regionInstance.setPort(port);

		listenOnRegionClose(context, regionID);
		openRegions.put(regionID.toString(), regionInstance);
		System.out.println("Open region added: " + regionID.getURI().toString());

		return regionInstance;
	}

	private void listenOnRegionClose(Context context, ID regionID) {
		// TODO: Make Thread safe
		context.getAdvisory().registerQueueStateListener(regionID.getURI().toString(), (QueueState state) -> {
			if (state == IAdvisory.QueueState.CLOSE && openRegions.containsKey(regionID.toString())) {
				context.getAdvisory().unregisterQueueOpenListener(regionID.getURI().toString());
				openRegions.remove(regionID.toString());
				System.out.println(
						"Open region removed: " + regionID.getURI().toString() + "regions open: " + openRegions.size());
			}
		});
	}
}

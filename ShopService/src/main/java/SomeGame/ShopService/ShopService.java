package SomeGame.ShopService;

import micronet.annotation.MessageListener;
import micronet.annotation.MessageService;
import micronet.annotation.OnStart;
import micronet.annotation.OnStop;
import micronet.model.AvatarValues;
import micronet.model.ID;
import micronet.model.ItemValues;
import micronet.model.RegionValues;
import micronet.network.Context;
import micronet.network.Request;
import micronet.network.Response;
import micronet.network.StatusCode;
import micronet.serialization.Serialization;

@MessageService(uri="mn://shop")
public class ShopService {
	private ShopDatabase database;

	@OnStart
	public void onStart(Context context) {
		database = new ShopDatabase();
	}
	
	@OnStop
	public void onStop(Context context) {
		database.shutdown();
	}

	@MessageListener(uri = "/shops/add")
	public Response addShop(Context context, Request request) {
		String faction = request.getParameters().getString(ParameterCode.FACTION);
		ItemValues[] items = Serialization.deserialize(request.getData(), ItemValues[].class);
		String rankRestrictionsData = request.getParameters().getString(ParameterCode.INDEX);
		Integer[] rankRestrictions = Serialization.deserialize(rankRestrictionsData, Integer[].class);
		database.addShop(faction, items, rankRestrictions);
		return new Response(StatusCode.OK);
	}

	@MessageListener(uri = "/shops/get")
	public Response getShop(Context context, Request request) {
		String faction = request.getParameters().getString(ParameterCode.FACTION);
		ItemValues[] items = database.getShop(faction);
		return new Response(StatusCode.OK, Serialization.serialize(items));
	}

	@MessageListener(uri = "/buy")
	public Response buy(Context context, Request request) {
		int userId = request.getParameters().getInt(ParameterCode.USER_ID);
		int type = request.getParameters().getInt(ParameterCode.ID);
		Integer[] indices = Serialization.deserialize(request.getData(), Integer[].class);

		Request avatarRequest = new Request();
		avatarRequest.getParameters().set(ParameterCode.USER_ID, userId);
		Response avatarResponse = context.sendRequestBlocking("mn://avatar/current/get", avatarRequest);
		AvatarValues avatar = Serialization.deserialize(avatarResponse.getData(), AvatarValues.class);

		if (!avatar.isLanded())
			return new Response(StatusCode.FORBIDDEN, "Must be landed");

		ID regionID = avatar.getRegionID();
		Request regionRequest = new Request(regionID.toString());
		Response regionResponse = context.sendRequestBlocking("mn://region/get", regionRequest);
		RegionValues region = Serialization.deserialize(regionResponse.getData(), RegionValues.class);
		
		if (avatar.getFaction().isHostile(region.getFaction()))
			return new Response(StatusCode.FORBIDDEN, "Buy Forbidden: Hostile Planet");

		int[] rankRestrictions = database.getRankRestrictions(region.getFaction());
		int restriction = rankRestrictions[indices[0]];
		if (restriction > 0) {
			if (!avatar.getFaction().getName().equals(region.getFaction()))
				return new Response(StatusCode.FORBIDDEN, "Buy Forbidden: You have the wrong Faction");
			if (avatar.getFaction().getRank() < restriction)
				return new Response(StatusCode.FORBIDDEN, "Insufficient Rank: Required " + restriction + " (Current " + avatar.getFaction().getRank() + ")");
		}
		
		ItemValues[] shopItems = database.getShop(region.getFaction());
		ItemValues itemToBuy = shopItems[indices[0]];

		// TODO: Start Transaction here. Is complicated because of Inventory
		if (itemToBuy.getPrice() > avatar.getCredits())
			return new Response(StatusCode.FORBIDDEN, "Insufficient Credits");

		System.out.println("Buy Request: " + itemToBuy.getName());

		Response inventoryResponse = null;
		if (type == ItemValues.ItemType.Vehicle) {
			inventoryResponse = buyVehicle(context, userId, itemToBuy);
		} else if (indices.length == 2) {
			inventoryResponse = buyItem(context, userId, itemToBuy, indices[1]);
		} else {
			inventoryResponse = buyItem(context, userId, itemToBuy);
		}

		if (inventoryResponse.getStatus() != StatusCode.OK)
			return inventoryResponse;

		Request creditsRequest = new Request(Integer.toString(itemToBuy.getPrice()));
		creditsRequest.getParameters().set(ParameterCode.USER_ID, userId);
		Response creditsResponse = context.sendRequestBlocking("mn://avatar/credits/remove", creditsRequest);

		// This should never happen
		if (creditsResponse.getStatus() != StatusCode.OK)
			return new Response(StatusCode.INTERNAL_SERVER_ERROR, "Money vanished during transaction");

		return new Response(StatusCode.OK);
	}

	Response buyVehicle(Context context, int userId, ItemValues item) {
		Request request = new Request(Serialization.serialize(item));
		request.getParameters().set(ParameterCode.USER_ID, userId);
		return context.sendRequestBlocking("mn://vehicle/add", request);
	}

	Response buyItem(Context context, int userId, ItemValues item) {
		Request request = new Request(Serialization.serialize(item));
		request.getParameters().set(ParameterCode.USER_ID, userId);
		return context.sendRequestBlocking("mn://item/inventory/add", request);
	}

	Response buyItem(Context context, int userId, ItemValues item, int inventoryIndex) {
		Request request = new Request(Serialization.serialize(item));
		request.getParameters().set(ParameterCode.USER_ID, userId);
		request.getParameters().set(ParameterCode.INDEX, inventoryIndex);
		return context.sendRequestBlocking("mn://item/inventory/set", request);
	}
}

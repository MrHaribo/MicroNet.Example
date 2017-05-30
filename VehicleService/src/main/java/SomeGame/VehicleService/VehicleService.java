package SomeGame.VehicleService;

import java.util.HashMap;
import java.util.Map;

import micronet.annotation.MessageListener;
import micronet.annotation.MessageService;
import micronet.annotation.OnStart;
import micronet.annotation.OnStop;
import micronet.model.AvatarValues;
import micronet.model.ItemValues;
import micronet.model.ItemValues.ItemType;
import micronet.model.VehicleValues;
import micronet.network.Context;
import micronet.network.Request;
import micronet.network.Response;
import micronet.network.StatusCode;
import micronet.serialization.Serialization;

@MessageService(uri="mn://vehicle")
public class VehicleService {
	private VehicleDatabase database;

	private Map<String, VehicleValues> defaultVehicles;

	@OnStart
	public void onStart(Context context) {
		database = new VehicleDatabase();
		defaultVehicles = new HashMap<>();
		defaultVehicles.put("Rebel", database.loadVehicleConfiguration("Mice"));
		defaultVehicles.put("Confederate", database.loadVehicleConfiguration("Beetle"));
		defaultVehicles.put("Neutral", database.loadVehicleConfiguration("Drone"));
	}

	@OnStop
	public void onStop(Context context) {
		database.shutdown();
	}

	@MessageListener(uri = "/configuration/all/upload")
	public Response uploadAllConfigurations(Context context, Request request) {
		VehicleValues[] vehicles = Serialization.deserialize(request.getData(), VehicleValues[].class);
		database.saveVehicleConfigurations(vehicles);
		return new Response(StatusCode.OK);
	}

	@MessageListener(uri = "/change")
	public Response changeVehicle(Context context, Request request) {
		int userID = request.getParameters().getInt(ParameterCode.USER_ID);
		Response avatarQuery = context.sendRequestBlocking("mn://avatar/current/get", request);
		AvatarValues avatar = Serialization.deserialize(avatarQuery.getData(), AvatarValues.class);

		if (!avatar.isLanded())
			return new Response(StatusCode.FORBIDDEN, "Must be landed to change Ship");

		database.setCurrentVehicle(userID, avatar.getName(), Integer.parseInt(request.getData()));
		sendVehicleChangedEvent(context, userID, avatar.getName());
		return new Response(StatusCode.OK);
	}

	@MessageListener(uri = "/sell")
	public Response sellVehicle(Context context, Request request) {
		int userID = request.getParameters().getInt(ParameterCode.USER_ID);
		Response avatarQuery = context.sendRequestBlocking("mn://avatar/current/name/get", request);
		String avatarName = avatarQuery.getData();

		int vehicleIndex = Integer.parseInt(request.getData());
		int currentVehicleIndex = database.getCurrentVehicleIndex(userID, avatarName);
		if (currentVehicleIndex == vehicleIndex)
			return new Response(StatusCode.FORBIDDEN, "Cannot Sell Ship in use");

		database.deleteVehicle(userID, avatarName, vehicleIndex);

		if (vehicleIndex < currentVehicleIndex)
			database.setCurrentVehicle(userID, avatarName, currentVehicleIndex - 1);

		// TODO: Sell Item via ShopService

		sendAvailableVehiclesChangedEvent(context, userID, avatarName);
		return new Response(StatusCode.OK);
	}

	@MessageListener(uri = "/current")
	public Response getCurrentVehicle(Context context, Request request) {
		int userID = request.getParameters().getInt(ParameterCode.USER_ID);
		Response avatarQuery = context.sendRequestBlocking("mn://avatar/current/name/get", request);

		VehicleValues vehicle = database.getCurrentVehicle(userID, avatarQuery.getData());
		if (vehicle == null)
			return new Response(StatusCode.FORBIDDEN, "You have no Ship");

		return new Response(StatusCode.OK, Serialization.serialize(vehicle));
	}

	@MessageListener(uri = "/add")
	public Response addVehicle(Context context, Request request) {
		int userID = request.getParameters().getInt(ParameterCode.USER_ID);
		Response avatarQuery = context.sendRequestBlocking("mn://avatar/current/name/get", request);
		String avatarName = avatarQuery.getData();
		ItemValues vehicleItem = Serialization.deserialize(request.getData(), ItemValues.class);
		VehicleValues vehicle = database.loadVehicleConfiguration(vehicleItem.getName());
		database.addVehicle(userID, avatarName, vehicle);

		sendAvailableVehiclesChangedEvent(context, userID, avatarName);
		return new Response(StatusCode.OK);
	}

	@MessageListener(uri = "/available")
	public Response getAvailableVehicles(Context context, Request request) {
		int userID = request.getParameters().getInt(ParameterCode.USER_ID);
		Response avatarQuery = context.sendRequestBlocking("mn://avatar/current/name/get", request);
		String avatarName = avatarQuery.getData();
		VehicleValues[] vehicles = database.getVehicles(userID, avatarName);
		
		String data = Serialization.serialize(vehicles);
		Response response = new Response(StatusCode.OK, data);
		response.getParameters().set(ParameterCode.INDEX, database.getCurrentVehicleIndex(userID, avatarName));
		return response;
	}

	@MessageListener(uri = "/collection/remove")
	public Response deleteVehicleCollection(Context context, Request request) {
		int userID = request.getParameters().getInt(ParameterCode.USER_ID);
		database.deleteVehicleCollection(userID, request.getData());
		return new Response(StatusCode.OK);
	}

	@MessageListener(uri = "/collection/create")
	public Response createVehicleCollection(Context context, Request request) {
		int userID = request.getParameters().getInt(ParameterCode.USER_ID);
		String faction = request.getParameters().getString(ParameterCode.FACTION);
		database.createVehicleCollection(userID, request.getData());
		database.addVehicle(userID, request.getData(), defaultVehicles.get(faction));
		return new Response(StatusCode.OK);
	}

	@MessageListener(uri = "/weapon/equip")
	public Response listenerName(Context context, Request request) {
		Integer[] indices = Serialization.deserialize(request.getData(), Integer[].class);

		Response avatarQuery = context.sendRequestBlocking("mn://avatar/current/get", request);
		AvatarValues avatar = Serialization.deserialize(avatarQuery.getData(), AvatarValues.class);

		if (!avatar.isLanded())
			return new Response(StatusCode.FORBIDDEN, "Weapons can only be changed when landed");

		int userID = request.getParameters().getInt(ParameterCode.USER_ID);
		int weaponType = request.getParameters().getInt(ParameterCode.ID);
		VehicleValues vehicle = database.getCurrentVehicle(userID, avatar.getName());

		Request itemRequest = new Request();
		itemRequest.getParameters().set(ParameterCode.USER_ID, userID);
		itemRequest.getParameters().set(ParameterCode.INDEX, indices[0]);
		Response getItemResponse = context.sendRequestBlocking("mn://item/inventory/get", itemRequest);
		ItemValues item = Serialization.deserialize(getItemResponse.getData(), ItemValues.class);

		if (weaponType != item.getType())
			return new Response(StatusCode.CONFLICT, "WeaponType Missmatch");

		switch (weaponType) {
		case ItemType.PrimaryWeapon:
			if (vehicle.getPrimaryWeapons()[indices[1]] != null)
				return new Response(StatusCode.CONFLICT, "Unequip Weapon first");
			vehicle.getPrimaryWeapons()[indices[1]] = item.getName();
			break;
		case ItemType.HeavyWeapon:
			if (vehicle.getHeavyWeapons()[indices[1]] != null)
				return new Response(StatusCode.CONFLICT, "Unequip Weapon first");
			vehicle.getHeavyWeapons()[indices[1]] = item.getName();
			break;
		}

		Response removeResponse = context.sendRequestBlocking("mn://item/inventory/remove", itemRequest);
		if (removeResponse.getStatus() != StatusCode.OK)
			return new Response(StatusCode.INTERNAL_SERVER_ERROR, "Weapon missing in inventory");

		int currentVehicleIndex = database.getCurrentVehicleIndex(userID, avatar.getName());
		database.updateVehicle(userID, avatar.getName(), currentVehicleIndex, vehicle);
		sendVehicleChangedEvent(context, userID, avatar.getName());
		return new Response(StatusCode.OK);
	}

	@MessageListener(uri = "/weapon/unequip")
	public Response unequipWeapon(Context context, Request request) {
		Integer[] indices = Serialization.deserialize(request.getData(), Integer[].class);

		Response avatarQuery = context.sendRequestBlocking("mn://avatar/current/get", request);
		AvatarValues avatar = Serialization.deserialize(avatarQuery.getData(), AvatarValues.class);

		if (!avatar.isLanded())
			return new Response(StatusCode.FORBIDDEN, "Weapons can only be changed when landed");

		int userID = request.getParameters().getInt(ParameterCode.USER_ID);
		String weaponType = request.getParameters().getString(ParameterCode.ID);
		VehicleValues vehicle = database.getCurrentVehicle(userID, avatar.getName());

		ItemValues item = null;

		switch (weaponType) {
		case "PrimaryWeapon":
			item = new ItemValues(vehicle.getPrimaryWeapons()[indices[0]], ItemType.PrimaryWeapon);
			break;
		case "HeavyWeapon":
			item = new ItemValues(vehicle.getHeavyWeapons()[indices[0]], ItemType.HeavyWeapon);
			break;
		}

		Request addItemRequest = new Request(Serialization.serialize(item));
		addItemRequest.getParameters().set(ParameterCode.USER_ID, userID);
		addItemRequest.getParameters().set(ParameterCode.INDEX, indices[1]);
		Response addItemResponse = context.sendRequestBlocking("mn://item/inventory/set", addItemRequest);

		if (addItemResponse.getStatus() != StatusCode.OK)
			return new Response(StatusCode.FORBIDDEN, "Inventory Slot not empty");

		switch (weaponType) {
		case "PrimaryWeapon":
			vehicle.getPrimaryWeapons()[indices[0]] = null;
			break;
		case "HeavyWeapon":
			vehicle.getHeavyWeapons()[indices[0]] = null;
			break;
		}
		int currentVehicleIndex = database.getCurrentVehicleIndex(userID, avatar.getName());
		database.updateVehicle(userID, avatar.getName(), currentVehicleIndex, vehicle);
		sendVehicleChangedEvent(context, userID, avatar.getName());
		return new Response(StatusCode.OK);
	}

	private void sendVehicleChangedEvent(Context context, int userID, String avatarName) {
		VehicleValues vehicle = database.getCurrentVehicle(userID, avatarName);
		Request eventRequest = new Request(Serialization.serialize(vehicle));
		eventRequest.getParameters().set(ParameterCode.INDEX, database.getCurrentVehicleIndex(userID, avatarName));
		context.sendEvent(userID, "OnVehicleChanged", eventRequest);
	}

	private void sendAvailableVehiclesChangedEvent(Context context, int userID, String name) {
		// TODO: Don't serialize whole ship list, only delta
		VehicleValues[] vehicles = database.getVehicles(userID, name);
		Request eventRequest = new Request(Serialization.serialize(vehicles));
		eventRequest.getParameters().set(ParameterCode.INDEX, database.getCurrentVehicleIndex(userID, name));
		context.sendEvent(userID, "OnAvailableVehiclesChanged", eventRequest);
	}
}

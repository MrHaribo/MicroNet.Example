package SomeGame.ItemService;

import micronet.annotation.MessageListener;
import micronet.annotation.MessageService;
import micronet.annotation.OnStart;
import micronet.model.ItemValues;
import micronet.network.Context;
import micronet.network.Request;
import micronet.network.Response;
import micronet.network.StatusCode;
import micronet.serialization.Serialization;

@MessageService(uri="mn://item")
public class ItemService {

	private static final String INVENTORY_ID = "Inventory";
	ItemDatabase database;

	@OnStart
	public void onStart(Context context) {
		database = new ItemDatabase();
	}
	
	@MessageListener(uri="/inventory/create")
	public Response createInventory(Context context, Request request) {
		int userId = request.getParameters().getInt(ParameterCode.USER_ID);
		database.createInventory(userId, INVENTORY_ID, 10);
		return new Response(StatusCode.OK);
	}
	
	@MessageListener(uri="/inventory/all")
	public Response getInventory(Context context, Request request) {
		int userId = request.getParameters().getInt(ParameterCode.USER_ID);
		ItemValues[] inventory = database.getInventory(userId, INVENTORY_ID);
		if (inventory == null) {
			database.createInventory(userId, request.getData(), 10);
			inventory = database.getInventory(userId, INVENTORY_ID);
		}
		String data = Serialization.serialize(inventory);
		return new Response(StatusCode.OK, data);
	}

	@MessageListener(uri="/inventory/get")
	public Response getInventorItem(Context context, Request request) {
		int userId = request.getParameters().getInt(ParameterCode.USER_ID);
		int index = request.getParameters().getInt(ParameterCode.INDEX);
		ItemValues item = database.getItem(userId, INVENTORY_ID, index);

		String data = Serialization.serialize(item);
		return new Response(StatusCode.OK, data);
	}

	@MessageListener(uri = "/inventory/remove")
	public Response removeFromInventory(Context context, Request request) {
		int userId = request.getParameters().getInt(ParameterCode.USER_ID);
		int index = request.getParameters().getInt(ParameterCode.INDEX);
		database.deleteItem(userId, INVENTORY_ID, index);

		sendInventoryChangedEvent(context, userId);
		return new Response(StatusCode.OK);
	}

	@MessageListener(uri = "/inventory/move")
	public Response moveInventoryItem(Context context, Request request) {
		int userId = request.getParameters().getInt(ParameterCode.USER_ID);

		Integer[] indices = Serialization.deserialize(request.getData(), Integer[].class);

		ItemValues item1 = database.getItem(userId, INVENTORY_ID, indices[0]);
		ItemValues item2 = database.getItem(userId, INVENTORY_ID, indices[1]);

		if (item2 == null) {
			database.setItem(userId, INVENTORY_ID, indices[1], item1);
			database.deleteItem(userId, INVENTORY_ID, indices[0]);
		} else {
			database.setItem(userId, INVENTORY_ID, indices[0], item2);
			database.setItem(userId, INVENTORY_ID, indices[1], item1);
		}

		sendInventoryChangedEvent(context, userId);
		return new Response(StatusCode.OK);
	}

	@MessageListener(uri = "/inventory/add")
	public Response addInventoryItem(Context context, Request request) {
		int userId = request.getParameters().getInt(ParameterCode.USER_ID);

		ItemValues item = Serialization.deserialize(request.getData(), ItemValues.class);
		if (!database.addItem(userId, INVENTORY_ID, item))
			return new Response(StatusCode.REQUESTED_RANGE_NOT_SATISFIABLE, "Inventory Full");

		sendInventoryChangedEvent(context, userId);
		return new Response(StatusCode.OK);
	}

	@MessageListener(uri = "/inventory/set")
	public Response setInventoryItem(Context context, Request request) {
		int userId = request.getParameters().getInt(ParameterCode.USER_ID);
		int index = request.getParameters().getInt(ParameterCode.INDEX);

		if (database.hasItem(userId, INVENTORY_ID, index))
			return new Response(StatusCode.CONFLICT, "Target slot NOT empty");

		ItemValues item = Serialization.deserialize(request.getData(), ItemValues.class);
		database.setItem(userId, INVENTORY_ID, index, item);

		sendInventoryChangedEvent(context, userId);
		return new Response(StatusCode.OK);
	}

	@MessageListener(uri = "/inventory/refresh")
	public Response refreshInventory(Context context, Request request) {
		int userId = request.getParameters().getInt(ParameterCode.USER_ID);
		sendInventoryChangedEvent(context, userId);
		return new Response(StatusCode.OK);
	}
	

	private void sendInventoryChangedEvent(Context context, int userId) {
		// TODO: DOnt serialize whole inventory, only delta
		ItemValues[] inventory = database.getInventory(userId, INVENTORY_ID);
		String data = Serialization.serialize(inventory);

		Request event = new Request(data);
		event.getParameters().set(ParameterCode.USER_ID, userId);
		event.getParameters().set(ParameterCode.EVENT, "OnInventoryChanged");
		context.sendRequest("mn://gateway/forward/event", event);
	}
}

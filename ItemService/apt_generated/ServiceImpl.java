import micronet.activemq.AMQPeer;
import micronet.network.Request;
import micronet.network.Context;

import SomeGame.ItemService.ItemService;



public class ServiceImpl {

	public static void main(String[] args) {
		try {
			System.out.println("Starting ItemService...");
	
			AMQPeer peer = new AMQPeer();
			Context context = new Context(peer, "mn://item");
	
			ItemService service = new ItemService();
	
			peer.listen("/inventory/add", (Request request) -> service.addInventoryItem(context, request));
peer.listen("/inventory/set", (Request request) -> service.setInventoryItem(context, request));
peer.listen("/inventory/refresh", (Request request) -> service.refreshInventory(context, request));
peer.listen("/inventory/all", (Request request) -> service.getInventory(context, request));
peer.listen("/inventory/move", (Request request) -> service.moveInventoryItem(context, request));
peer.listen("/inventory/remove", (Request request) -> service.removeFromInventory(context, request));
peer.listen("/inventory/get", (Request request) -> service.getInventorItem(context, request));
peer.listen("/inventory/create", (Request request) -> service.createInventory(context, request));

			
			System.out.println("ItemService started...");
service.onStart(context);

			
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					System.out.println("ItemService stopped...");

				}
			});
		} catch (Exception e) {
			System.err.print("ItemService crushed...\n\n");
			e.printStackTrace();
		}
	}
}

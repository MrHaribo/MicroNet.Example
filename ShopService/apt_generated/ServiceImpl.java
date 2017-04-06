import micronet.activemq.AMQPeer;
import micronet.network.Request;
import micronet.network.Context;

import SomeGame.ShopService.ShopService;



public class ServiceImpl {

	public static void main(String[] args) {
		try {
			System.out.println("Starting ShopService...");
	
			AMQPeer peer = new AMQPeer();
			Context context = new Context(peer, "mn://shop");
	
			ShopService service = new ShopService();
	
			peer.listen("/shops/get", (Request request) -> service.getShop(context, request));
peer.listen("/buy", (Request request) -> service.buy(context, request));
peer.listen("/shops/add", (Request request) -> service.addShop(context, request));

			
			System.out.println("ShopService started...");
service.onStart(context);

			
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					System.out.println("ShopService stopped...");
service.onStop(context);

				}
			});
		} catch (Exception e) {
			System.err.print("ShopService crushed...\n\n");
			e.printStackTrace();
		}
	}
}

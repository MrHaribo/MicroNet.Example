import micronet.activemq.AMQPeer;
import micronet.network.Request;
import micronet.network.Context;

import SomeGame.WorldService.WorldService;



public class ServiceImpl {

	public static void main(String[] args) {
		try {
			System.out.println("Starting WorldService...");
	
			AMQPeer peer = new AMQPeer();
			Context context = new Context(peer, "mn://world");
	
			WorldService service = new WorldService();
	
			peer.listen("/join", (Request request) -> service.joinWorld(context, request));
peer.listen("/travel", (Request request) -> service.travel(context, request));
peer.listen("/battles/all", (Request request) -> service.getAllBattles(context, request));

			
			System.out.println("WorldService started...");
service.onStart(context);

			
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					System.out.println("WorldService stopped...");
service.onStop(context);

				}
			});
		} catch (Exception e) {
			System.err.print("WorldService crushed...\n\n");
			e.printStackTrace();
		}
	}
}

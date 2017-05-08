import micronet.activemq.AMQPeer;
import micronet.network.Request;
import micronet.network.Context;

import SomeGame.GatewayService.GatewayService;



public class ServiceImpl {

	public static void main(String[] args) {
		try {
			System.out.println("Starting GatewayService...");
	
			AMQPeer peer = new AMQPeer();
			Context context = new Context(peer, "mn://gateway");
	
			GatewayService service = new GatewayService();
	
			peer.listen("/broadcast/event", (Request request) -> service.broadcastEvent(context, request));
peer.listen("/forward/event", (Request request) -> service.forwardEvent(context, request));

			
			System.out.println("GatewayService started...");
service.onStart(context);

			
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					System.out.println("GatewayService stopped...");
service.onStop(context);

				}
			});
		} catch (Exception e) {
			System.err.print("GatewayService crushed...\n\n");
			e.printStackTrace();
		}
	}
}

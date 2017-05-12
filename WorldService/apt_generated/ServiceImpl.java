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
	
			peer.listen("/instance/open", (Request request) -> service.openInstance(context, request));
peer.listen("/instance/add", (Request request) -> service.addInstance(context, request));
peer.listen("/instance/ready", (Request request) -> service.readyInstance(context, request));
peer.listen("/join", (Request request) -> service.joinWorld(context, request));
peer.listen("/travel", (Request request) -> service.travel(context, request));
peer.listen("/instance/reset", (Request request) -> service.resetInstance(context, request));
peer.listen("/travel/home", (Request request) -> service.homestone(context, request));

			
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

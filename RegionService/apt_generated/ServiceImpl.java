import micronet.activemq.AMQPeer;
import micronet.network.Request;
import micronet.network.Context;

import SomeGame.RegionService.RegionService;



public class ServiceImpl {

	public static void main(String[] args) {
		try {
			System.out.println("Starting RegionService...");
	
			AMQPeer peer = new AMQPeer();
			Context context = new Context(peer, "mn://region");
	
			RegionService service = new RegionService();
	
			peer.listen("/add", (Request request) -> service.addRegion(context, request));
peer.listen("/battles/all", (Request request) -> service.getAllBattles(context, request));
peer.listen("/get", (Request request) -> service.getRegion(context, request));
peer.listen("/all", (Request request) -> service.getAllRegions(context, request));

			
			System.out.println("RegionService started...");
service.onStart(context);

			
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					System.out.println("RegionService stopped...");
service.onStop(context);

				}
			});
		} catch (Exception e) {
			System.err.print("RegionService crushed...\n\n");
			e.printStackTrace();
		}
	}
}

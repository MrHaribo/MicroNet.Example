import micronet.activemq.AMQPeer;
import micronet.network.Request;
import micronet.network.Context;

import SomeGame.PortService.PortService;



public class ServiceImpl {

	public static void main(String[] args) {
		try {
			System.out.println("Starting PortService...");
	
			AMQPeer peer = new AMQPeer();
			Context context = new Context(peer, "mn://port");
	
			PortService service = new PortService();
	
			peer.listen("/reserve", (Request request) -> service.reservePort(context, request));
peer.listen("/release", (Request request) -> service.releasePort(context, request));

			
			System.out.println("PortService started...");
service.onStart(context);

			
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					System.out.println("PortService stopped...");
service.onStop(context);

				}
			});
		} catch (Exception e) {
			System.err.print("PortService crushed...\n\n");
			e.printStackTrace();
		}
	}
}

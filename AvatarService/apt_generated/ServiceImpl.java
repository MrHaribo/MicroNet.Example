import micronet.activemq.AMQPeer;
import micronet.network.Request;
import micronet.network.Context;

import SomeGame.AvatarService.AvatarService;



public class ServiceImpl {

	public static void main(String[] args) {
		try {
			System.out.println("Starting AvatarService...");
	
			AMQPeer peer = new AMQPeer();
			Context context = new Context(peer, "mn://avatar");
	
			AvatarService service = new AvatarService();
	
			peer.listen("/reputation/add", (Request request) -> service.addReputation(context, request));
peer.listen("/takeoff", (Request request) -> service.takeoff(context, request));
peer.listen("/get", (Request request) -> service.setAvatar(context, request));
peer.listen("/land", (Request request) -> service.land(context, request));
peer.listen("/current/set", (Request request) -> service.setCurrentAvatar(context, request));
peer.listen("/current/update", (Request request) -> service.updateAvatar(context, request));
peer.listen("/delete", (Request request) -> service.deleteAvatar(context, request));
peer.listen("/current/get", (Request request) -> service.getCurrentAvatar(context, request));
peer.listen("/credits/balance", (Request request) -> service.balanceCredits(context, request));
peer.listen("/all", (Request request) -> service.getAllAvatars(context, request));
peer.listen("/credits/add", (Request request) -> service.addCredits(context, request));
peer.listen("/persist", (Request request) -> service.persistAvatar(context, request));
peer.listen("/create", (Request request) -> service.createAvatar(context, request));
peer.listen("/credits/remove", (Request request) -> service.removeCredits(context, request));
peer.listen("/current/name/get", (Request request) -> service.getCurrentAvatarName(context, request));

			
			System.out.println("AvatarService started...");
service.onStart(context);

			
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					System.out.println("AvatarService stopped...");
service.onStop(context);

				}
			});
		} catch (Exception e) {
			System.err.print("AvatarService crushed...\n\n");
			e.printStackTrace();
		}
	}
}

import micronet.activemq.AMQPeer;
import micronet.network.Request;
import micronet.network.Context;

import SomeGame.VehicleService.VehicleService;



public class ServiceImpl {

	public static void main(String[] args) {
		try {
			System.out.println("Starting VehicleService...");
	
			AMQPeer peer = new AMQPeer();
			Context context = new Context(peer, "mn://vehicle");
	
			VehicleService service = new VehicleService();
	
			peer.listen("/weapon/equip", (Request request) -> service.listenerName(context, request));
peer.listen("/change", (Request request) -> service.changeVehicle(context, request));
peer.listen("/add", (Request request) -> service.addVehicle(context, request));
peer.listen("/sell", (Request request) -> service.sellVehicle(context, request));
peer.listen("/weapon/unequip", (Request request) -> service.unequipWeapon(context, request));
peer.listen("/collection/remove", (Request request) -> service.deleteVehicleCollection(context, request));
peer.listen("/configuration/all/upload", (Request request) -> service.uploadAllConfigurations(context, request));
peer.listen("/available", (Request request) -> service.getAvailableVehicles(context, request));
peer.listen("/collection/create", (Request request) -> service.createVehicleCollection(context, request));
peer.listen("/current", (Request request) -> service.getCurrentVehicle(context, request));

			
			System.out.println("VehicleService started...");
service.onStart(context);

			
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					System.out.println("VehicleService stopped...");
service.onStop(context);

				}
			});
		} catch (Exception e) {
			System.err.print("VehicleService crushed...\n\n");
			e.printStackTrace();
		}
	}
}

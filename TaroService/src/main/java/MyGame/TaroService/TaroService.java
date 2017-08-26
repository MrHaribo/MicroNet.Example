package MyGame.TaroService;

import micronet.annotation.MessageListener;
import micronet.annotation.MessageService;
import micronet.annotation.OnStart;
import micronet.network.Context;
import micronet.network.Request;

@MessageService(uri = "mn://taroservice")
public class TaroService {
	
	@OnStart
	public void onStart(Context context) {
		System.out.println("TaroService Start Routine...");
		context.sendRequest("mn://taroservice/hello/world/handler", new Request("Hello"));
	}
	
	@MessageListener(uri="/hello/world/handler")
	public void helloHandler(Context context, Request request) {
		System.out.println(request.getData() + " World MicroNet...");
	}
}


package SimpleService;

import micronet.annotation.MessageListener;
import micronet.annotation.MessageService;
import micronet.annotation.OnStart;
import micronet.annotation.OnStop;
import micronet.network.Context;
import micronet.network.Request;
import micronet.network.Response;
import micronet.network.StatusCode;

@MessageService(uri="mn://yourService") 
public class SomeService {

	@OnStart
	public void onStart(Context context) {
	}
	
	@OnStop
	public void onStop(Context context) {
	}

	@MessageListener(uri = "/yourListener/with/response")
	public Response reservePort(Context context, Request request) {
		return new Response(StatusCode.OK, "Hello World!");
	}
	@MessageListener(uri = "/yourListener/simple")
	public void releasePort(Context context, Request request) {
	}

}

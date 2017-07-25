package SomeGame.VoteService;

import micronet.annotation.MessageListener;
import micronet.annotation.MessageService;
import micronet.network.Context;
import micronet.network.Request;


@MessageService(uri = "mn://bar")
public class ServiceBar {
	@MessageListener(uri="/hello")
	public void helloHandler(Context context, Request request) {
		int parameter = request.getParameters().getInt(ParameterCode.ID);
		System.out.println("Received Request with Parameter: " + parameter);
	}
}


//@MessageService(uri = "mn://bar")
//public class ServiceBar {
//	@MessageListener(uri="/hello")
//	public Response helloHandler(Context context, Request request) {
//		System.out.println("Received Request: " + request.getData());
//		return new Response(StatusCode.OK, "Likewise, Bar");
//	}
//}

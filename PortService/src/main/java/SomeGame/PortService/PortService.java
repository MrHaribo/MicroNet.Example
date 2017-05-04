package SomeGame.PortService;

import java.util.Stack;

import micronet.annotation.MessageListener;
import micronet.annotation.MessageService;
import micronet.annotation.OnStart;
import micronet.annotation.OnStop;
import micronet.network.Context;
import micronet.network.Request;
import micronet.network.Response;
import micronet.network.StatusCode;

@MessageService(uri="mn://port")
public class PortService {

	private static final int portRangeStart = System.getenv("port_range_start") != null ? Integer.parseInt(System.getenv("port_range_start")) : 40000;
	private static final int portRangeSize = System.getenv("port_range_size") != null ? Integer.parseInt(System.getenv("port_range_size")) : 10;
	
	Stack<Integer> freePorts = new Stack<Integer>();

	@OnStart
	public void onStart(Context context) {
		for (int i = 0; i < portRangeSize; i++) {
			freePorts.add(portRangeStart + i);
		}
	}
	
	@OnStop
	public void onStop(Context context) {

	}

	@MessageListener(uri = "/reserve")
	public Response reservePort(Context context, Request request) {
		if (freePorts.empty())
			return new Response(StatusCode.SERVICE_UNAVAILABLE, "No Free Ports!");
		
		int port = freePorts.pop();
		return new Response(StatusCode.OK, Integer.toString(port));
	}
	@MessageListener(uri = "/release")
	public void releasePort(Context context, Request request) {
		freePorts.push(Integer.parseInt(request.getData()));
	}

}

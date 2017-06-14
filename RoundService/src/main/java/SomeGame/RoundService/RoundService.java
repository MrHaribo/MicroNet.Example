package SomeGame.RoundService;

import SomeGame.DataAccess.Event;
import micronet.annotation.MessageService;
import micronet.annotation.OnStart;
import micronet.annotation.OnStop;
import micronet.network.Context;
import micronet.network.Request;

@MessageService(uri = "mn://round")
public class RoundService {

	private int roundDuration = 8000;
	
	private Thread roundThread;
	private boolean isRunning;

	@OnStart
	public void onStart(Context context) {
		isRunning = true;
		roundThread = new Thread(() -> roundUpdate(context));
		roundThread.start();
	}

	@OnStop
	public void onStop(Context context) {
		isRunning = false;
		roundThread.interrupt();
	}

	private void roundUpdate(Context context) {
		while (isRunning) {
			
			context.sendRequest("mn://player/score/broadcast", new Request());
			
			context.broadcastEvent(Event.RoundStart.toString(), Integer.toString(roundDuration));

			try {
				Thread.sleep(roundDuration);
			} catch (InterruptedException e) {
				return;
			}
		}
	}
}

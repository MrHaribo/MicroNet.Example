package SomeGame.RoundService;

import java.util.Random;

import micronet.annotation.MessageService;
import micronet.annotation.OnStart;
import micronet.annotation.OnStop;
import micronet.network.Context;
import micronet.network.Request;
import micronet.serialization.Serialization;

@MessageService(uri = "mn://round")
public class RoundService {

	private int roundDuration = 10000;
	private int intermissionDuration = 5000;

	private Thread roundThread;
	private boolean isRunning;

	@OnStart
	public void onStart(Context context) {
		isRunning = true;
		roundThread = new Thread(() -> roundUpdate(context));
		roundThread.setDaemon(true);
		roundThread.start();
	}

	@OnStop
	public void onStop(Context context) {
		isRunning = false;
		roundThread.interrupt();
	}

	private void roundUpdate(Context context) {
		while (isRunning) {
			System.out.println("New Round");

			int voteValue = new Random().nextInt(100) + 1;

			RoundInfo roundInfo = new RoundInfo();
			roundInfo.setDuration(roundDuration);
			roundInfo.setVoteValue(voteValue);

			context.getAdvisory().send(Event.RoundStart.toString(), Serialization.serialize(roundInfo));
			context.broadcastEvent(Event.RoundStart.toString(), Integer.toString(roundDuration));

			try {
				Thread.sleep(roundDuration);
			} catch (InterruptedException e) {
				return;
			}

			context.getAdvisory().send(Event.RoundEnd.toString(), Serialization.serialize(roundInfo));
			context.broadcastEvent(Event.RoundEnd.toString(), Integer.toString(intermissionDuration));
			context.sendRequest("mn://player/score/broadcast", new Request());

			try {
				Thread.sleep(intermissionDuration);
			} catch (InterruptedException e) {
				return;
			}

			context.sendRequestBlocking("mn://vote/clear", new Request());
		}
	}
}

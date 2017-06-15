package SomeGame.PlayerService;

import java.util.List;

import SomeGame.DataAccess.Event;
import SomeGame.DataAccess.ParameterCode;
import SomeGame.DataAccess.Player;
import SomeGame.DataAccess.PlayerStore;
import micronet.annotation.MessageListener;
import micronet.annotation.MessageService;
import micronet.annotation.OnStart;
import micronet.annotation.OnStop;
import micronet.network.Context;
import micronet.network.NetworkConstants;
import micronet.network.Request;
import micronet.serialization.Serialization;

@MessageService(uri="mn://player")
public class PlayerService {

	private PlayerStore players = new PlayerStore();

	@OnStart
	public void onStart(Context context) {
	}
	
	@OnStop
	public void onStop(Context context) {
		
	}
	
	@MessageListener(uri="/add")
	public void addPlayer(Context context, Request request) {
		int userID = request.getParameters().getInt(NetworkConstants.USER_ID);
		players.add(userID, request.getData());
	}
	
	@MessageListener(uri="/remove")
	public void removePlayer(Context context, Request request) {
		int userID = request.getParameters().getInt(NetworkConstants.USER_ID);
		players.remove(userID);
	}
	
	@MessageListener(uri="/score/add")
	public void addScore(Context context, Request request) {
		int userID = request.getParameters().getInt(NetworkConstants.USER_ID);
		Player player = players.get(userID);
		int newScore = player.getScore() + Integer.parseInt(request.getData());
		player.setScore(newScore);
		players.update(userID, player);
	}
	
	@MessageListener(uri="/score/broadcast")
	public void broadcastScore(Context context, Request request) {
		
		List<Player> allPlayers = players.all();
		String data = Serialization.serialize(allPlayers);
		
		context.broadcastEvent(Event.ScoreUpdate.toString(), data);
	}
}

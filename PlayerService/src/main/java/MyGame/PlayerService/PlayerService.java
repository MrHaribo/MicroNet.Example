package MyGame.PlayerService;

import java.util.List;

import micronet.annotation.MessageListener;
import micronet.annotation.MessageParameter;
import micronet.annotation.MessageService;
import micronet.annotation.OnStart;
import micronet.annotation.OnStop;
import micronet.annotation.RequestParameters;
import micronet.annotation.RequestPayload;
import micronet.annotation.ResponsePayload;
import micronet.network.Context;
import micronet.network.Request;
import micronet.network.Response;
import micronet.network.StatusCode;
import micronet.serialization.Serialization;

@MessageService(uri="mn://player", desc="Player Service that manages scores")
public class PlayerService {

	private PlayerStore players = new PlayerStore();

	@OnStart
	public void onStart(Context context) {
	}
	
	@OnStop
	public void onStop(Context context) {
	}
	
	@MessageListener(uri="/add", desc="Adds a new Player to the Session Store")
	@RequestParameters({@MessageParameter(code=ParameterCode.USER_ID, type=Integer.class, desc="UserID")})
	public void addPlayer(Context context, Request request) {
		int userID = request.getParameters().getInt(ParameterCode.USER_ID);
		players.add(userID, request.getData());
	}
	
	@MessageListener(uri="/remove", desc="Explicitly removes a player from the Session Store.")
	@RequestParameters(@MessageParameter(code=ParameterCode.USER_ID, type=Integer.class, desc="UserID"))
	public void removePlayer(Context context, Request request) {
		int userID = request.getParameters().getInt(ParameterCode.USER_ID);
		players.remove(userID);
	}
	
	@MessageListener(uri="/score/add", desc="Increments the score of the specified player")
	@RequestParameters(@MessageParameter(code=ParameterCode.USER_ID, type=Integer.class, desc="UserID"))
	@RequestPayload(value = Integer.class, desc = "Score Increment")
	public void addScore(Context context, Request request) {
		int userID = request.getParameters().getInt(ParameterCode.USER_ID);
		Player player = players.get(userID);
		int newScore = player.getScore() + Integer.parseInt(request.getData());
		player.setScore(newScore);
		players.update(userID, player);
	}
	
	@MessageListener(uri="/score/all", desc="Request the score of all players")
	@ResponsePayload(value=Player[].class, desc="All Player Scores")
	public Response getScores(Context context, Request request) {
		List<Player> allPlayers = players.all();
		String data = Serialization.serialize(allPlayers);
		return new Response(StatusCode.OK, data);
	}
	
	@MessageListener(uri="/score/broadcast", desc="Broadcast the score of all players to all players")
	public void broadcastScore(Context context, Request request) {
		List<Player> allPlayers = players.all();
		String data = Serialization.serialize(allPlayers);
		
		context.broadcastEvent(Event.ScoreUpdate.toString(), data);
	}
}

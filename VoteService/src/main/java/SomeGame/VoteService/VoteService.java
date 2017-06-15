package SomeGame.VoteService;

import java.util.Map;

import SomeGame.DataAccess.Event;
import SomeGame.DataAccess.RoundInfo;
import SomeGame.DataAccess.VoteResult;
import SomeGame.DataAccess.VoteStore;
import micronet.annotation.MessageListener;
import micronet.annotation.MessageService;
import micronet.annotation.OnStart;
import micronet.annotation.OnStop;
import micronet.network.Context;
import micronet.network.NetworkConstants;
import micronet.network.Request;
import micronet.network.Response;
import micronet.network.StatusCode;
import micronet.serialization.Serialization;

@MessageService(uri = "mn://vote")
public class VoteService {

	private RoundInfo currentRoundInfo;

	VoteStore votes = new VoteStore();
	
	public static void main(String[] args) {
		
		
//		
//		votes.add(42,  314124);
//		votes.add(43,  6);
//		votes.add(44,  87);
//		votes.add(45,  43);
//		
//		Map<Integer, Integer> all = votes.all();
//		System.out.println(all);
//		
//		votes.clear();
//		all = votes.all();
//		System.out.println(all);
//		
//		
//		votes.add(42,  314124);
//		votes.add(43,  6);
//		votes.add(44,  87);
//		votes.add(45,  43);
//		
//		all = votes.all();
//		System.out.println(all);
	}
	
	@OnStart
	public void onStart(Context context) {
		context.getAdvisory().listen(Event.RoundStart.toString(), event ->{ 
			currentRoundInfo = Serialization.deserialize(event, RoundInfo.class);
		});
		context.getAdvisory().listen(Event.RoundEnd.toString(), event ->{ 
			currentRoundInfo = null;
		});
	}

	@OnStop
	public void onStop(Context context) {
	}
	
	@MessageListener(uri="/clear")
	public Response clear(Context context, Request request) {
		votes.clear();
		return new Response(StatusCode.OK);
	}
	
	@MessageListener(uri="/put")
	public Response vote(Context context, Request request) {
		int userID = request.getParameters().getInt(NetworkConstants.USER_ID);

		if (votes.contains(userID))
			return new Response(StatusCode.FORBIDDEN, "You already voted");
		
		Integer vote = Integer.parseInt(request.getData());
		votes.add(userID, vote);
		
		int score = 101 - Math.abs(currentRoundInfo.getVoteValue() - vote);
		score = score * score;

		Request addScoreRequest = new Request(Integer.toString(score));
		addScoreRequest.getParameters().set(NetworkConstants.USER_ID, userID);
		context.sendRequest("mn://player/score/add", addScoreRequest);
		
		VoteResult result = new VoteResult();
		result.setMessage("Thank you for playing!");
		result.setRealValue(currentRoundInfo.getVoteValue());
		result.setScore(score);
		
		return new Response(StatusCode.OK, Serialization.serialize(result));
	}

}

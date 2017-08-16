package MyGame.VoteService;

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

@MessageService(uri = "mn://vote", desc="Service that processes player votes")
public class VoteService {

	private RoundInfo currentRoundInfo;

	VoteStore votes = new VoteStore();
	
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
	
	@MessageListener(uri="/clear", desc="Clear the current vote")
	public Response clear(Context context, Request request) {
		votes.clear();
		return new Response(StatusCode.OK);
	}
	
	@MessageListener(uri="/put", desc="Place a new vote by a player")
	@RequestParameters(@MessageParameter(code=ParameterCode.USER_ID, type=Integer.class, desc="UserID"))
	@RequestPayload(value=Integer.class, desc="The value of the Vote")
	@ResponsePayload(VoteResult.class)
	public Response vote(Context context, Request request) {
		int userID = request.getParameters().getInt(ParameterCode.USER_ID);

		if (votes.contains(userID))
			return new Response(StatusCode.FORBIDDEN, "You already voted");
		
		Integer vote = Integer.parseInt(request.getData());
		votes.add(userID, vote);
		
		int score = 101 - Math.abs(currentRoundInfo.getVoteValue() - vote);
		score = score * score;

		Request addScoreRequest = new Request(Integer.toString(score));
		addScoreRequest.getParameters().set(ParameterCode.USER_ID, userID);
		context.sendRequest("mn://player/score/add", addScoreRequest);
		
		VoteResult result = new VoteResult();
		result.setMessage("Thank you for playing!");
		result.setRealValue(currentRoundInfo.getVoteValue());
		result.setScore(score);
		
		return new Response(StatusCode.OK, Serialization.serialize(result));
	}

}

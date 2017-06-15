package SomeGame.DataAccess;

import java.util.HashMap;
import java.util.Map;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.datastructures.MutationOptionBuilder;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.error.DocumentDoesNotExistException;
import com.couchbase.client.java.error.subdoc.PathNotFoundException;

import micronet.serialization.Serialization;

public class VoteStore {

	private Cluster cluster;
	private Bucket bucket;

	public VoteStore() {
		String connectionString = System.getenv("couchbase_address") != null ? System.getenv("couchbase_address")
				: "localhost";
		System.out.println("Connecting to Couchbase: " + connectionString);
		cluster = CouchbaseCluster.create(connectionString);
		bucket = cluster.openBucket("entities");
		bucket.bucketManager().createN1qlPrimaryIndex(true, false);
	}

	public void add(int userID, int voteValue) {
		System.out.println("Add Vote: " + getVoteID(userID));
		bucket.mapAdd("votes", getVoteID(userID), voteValue, MutationOptionBuilder.builder().createDocument(true));
	}

	public void clear() {
		try {
			bucket.remove("votes");
		} catch (DocumentDoesNotExistException e) {
		}
	}

	public Map<Integer, Integer> all() {
		JsonDocument jsonDocument = bucket.get("votes");
		if (jsonDocument == null)
			return new HashMap<>();

		Map<String, Object> map = jsonDocument.content().toMap();

		Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		for (Map.Entry<String, Object> voteEntry : map.entrySet()) {
			result.put(getUserID(voteEntry.getKey()), (Integer) voteEntry.getValue());
		}

		return result;
	}

	public boolean contains(int userID) {
		try {
			Integer vote = bucket.mapGet("votes", getVoteID(userID), Integer.class);
			return vote != null;
		} catch (DocumentDoesNotExistException | PathNotFoundException e) {
			return false;
		} 
	}

	private int getUserID(String vodeID) {
		return Integer.parseInt(vodeID.replace("vote_", ""));
	}

	private String getVoteID(int userID) {
		return "vote_" + userID;
	}
}

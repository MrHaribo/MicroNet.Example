package SomeGame.DataAccess;

import java.util.ArrayList;
import java.util.List;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;

import micronet.model.Entity;
import micronet.serialization.Serialization;

public class PlayerStore {
	
	private int playerTimeout = 3000;
	
	private Cluster cluster;
	private Bucket bucket;

	public PlayerStore() {
		String connectionString = System.getenv("couchbase_address") != null ? System.getenv("couchbase_address") : "localhost";
		System.out.println("Connecting to Couchbase: " + connectionString);
		cluster = CouchbaseCluster.create(connectionString);
		bucket = cluster.openBucket("entities");
        bucket.bucketManager().createN1qlPrimaryIndex(true, false);
	}
	
	public Player get(int userID) {
		JsonDocument playerDoc = bucket.getAndTouch(getPlayerID(userID), playerTimeout);
		if (playerDoc == null)
			return null;
		JsonObject playerObject = playerDoc.content().getObject(Entity.VALUE_KEY);
		return Serialization.deserialize(playerObject.toString(), Player.class);
	}
	
	public Player add(int userID, String name) {
		System.out.println("Add Player: " + getPlayerID(userID));

		Player player = new Player();
		player.setScore(0);
		player.setName(name);
		
        JsonObject playerObject = JsonObject.create();
        playerObject.put(Entity.TYPE_KEY, Player.class.getSimpleName());
        playerObject.put(Entity.VALUE_KEY, JsonObject.fromJson(Serialization.serialize(player)));
        
        bucket.upsert(JsonDocument.create(getPlayerID(userID), playerTimeout, playerObject));
        return player;
	}
	
	public List<Player> all() {

        N1qlQueryResult result = bucket.query(
                N1qlQuery.parameterized("SELECT `value` FROM entities WHERE type=$1", 
                JsonArray.from(Player.class.getSimpleName()))
            );

        List<Player> connections = new ArrayList<>();
        for (N1qlQueryRow row : result) {
        	connections.add(Serialization.deserialize(row.value().get("value").toString(), Player.class));
        }
        return connections;
	}

	public void remove(int userID) {
		System.out.println("Remove Player Connection: " + getPlayerID(userID));
		bucket.remove(getPlayerID(userID));
	}
	
	private String getPlayerID(int userID) {
		return "player_" + userID;
	}
}

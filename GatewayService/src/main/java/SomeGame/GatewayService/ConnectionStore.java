package SomeGame.GatewayService;

import java.util.ArrayList;
import java.util.List;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.bucket.BucketType;
import com.couchbase.client.java.cluster.BucketSettings;
import com.couchbase.client.java.cluster.ClusterManager;
import com.couchbase.client.java.cluster.DefaultBucketSettings;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;

import micronet.serialization.Serialization;

public class ConnectionStore {
	
	private Cluster cluster;
	private Bucket bucket;

	public ConnectionStore() {
		String connectionString = System.getenv("couchbase_address") != null ? System.getenv("couchbase_address") : "localhost";
		System.out.println("Connecting to Couchbase: " + connectionString);
		cluster = CouchbaseCluster.create(connectionString);
		bucket = cluster.openBucket("user_connections");
        bucket.bucketManager().createN1qlPrimaryIndex(true, false);
	}
	
	public Connection get(String connectionID) {
		JsonDocument connectionDoc = bucket.getAndTouch(connectionID, 3000);
		if (connectionDoc == null)
			return null;
		return Serialization.deserialize(connectionDoc.content().toString(), Connection.class);
	}
	
	public Connection get(int userID) {

        N1qlQueryResult result = bucket.query(
            N1qlQuery.parameterized("SELECT connectionID, userID FROM user_connections WHERE userID=$1",
            JsonArray.from(userID))
        );

        for (N1qlQueryRow row : result) {
        	System.out.println(row);
        	return Serialization.deserialize(row.value().toString(), Connection.class);
        }
        return null;
	}
	
	public List<Connection> all() {

        N1qlQueryResult result = bucket.query(
            N1qlQuery.simple("SELECT connectionID, userID FROM user_connections")
        );

        List<Connection> connections = new ArrayList<>();
        for (N1qlQueryRow row : result) {
        	System.out.println(row);
        	connections.add(Serialization.deserialize(row.value().toString(), Connection.class));
        }
        return connections;
	}
	
	public Connection add(String connectionID, int userID) {

		Connection connection = get(userID);
		if (connection != null) {
			System.out.println("Removing old connection: " + connection.getConnectionID());
			remove(connection.getConnectionID());
		}
		
        System.out.println("Add Player Connection: " + connectionID);
		connection = new Connection(connectionID, userID);
        JsonObject connectionObj = JsonObject.fromJson(Serialization.serialize(connection));
        bucket.insert(JsonDocument.create(connectionID, 3000, connectionObj));
        
        return connection;
	}

	public void remove(String connectionID) {
		System.out.println("Remove Player Connection: " + connectionID);
		bucket.remove(connectionID);
	}
}

package SomeGame.WorldService;

import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.s;
import static com.couchbase.client.java.query.dsl.Expression.x;

import java.util.List;

import com.couchbase.client.core.message.kv.subdoc.multi.Lookup;
import com.couchbase.client.core.message.kv.subdoc.multi.Mutation;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.error.subdoc.MultiMutationException;
import com.couchbase.client.java.error.subdoc.PathExistsException;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.Statement;
import com.couchbase.client.java.subdoc.DocumentFragment;

import micronet.model.ID;

public class InstanceStore {
	
	private Cluster cluster;
	private Bucket bucket;

	public InstanceStore() {
		String connectionString = System.getenv("couchbase_address") != null ? System.getenv("couchbase_address") : "localhost";
		System.out.println("Connecting to Couchbase: " + connectionString);
		cluster = CouchbaseCluster.create(connectionString);
		bucket = cluster.openBucket("instance_connections");
        bucket.bucketManager().createN1qlPrimaryIndex(true, false);
	}
	
	public void addInstance(ID regionID) {
        JsonObject instanceObj = JsonObject.create()
            .put("status", "opening")
            .put("queuedUsers", JsonArray.empty());
        bucket.insert(JsonDocument.create(regionID.toString(), instanceObj));
	}

	public void openInstance(ID regionID, String instanceID, String host, int port) {
		DocumentFragment<Mutation> mutation = bucket
		    .mutateIn(regionID.toString())
		    .replace("status", "open")
		    .insert("instanceID", instanceID)
		    .insert("host", host)
		    .insert("port", port)
		    .execute();
		System.out.println(mutation.toString());
	}

	public boolean existsInstance(ID regionID) {
		return bucket.get(regionID.toString()) != null;
	}
	
	public void removeInstance(ID regionID) {
		bucket.remove(regionID.toString());
	}

	public boolean isRegionOpen(ID regionID) {
		JsonDocument regionDoc = bucket.get(regionID.toString());
		if (regionDoc == null)
			return false;
		return regionDoc.content().getString("status").equals("open");
	}

	public boolean isRegionOpening(ID regionID) {
		JsonDocument regionDoc = bucket.get(regionID.toString());
		if (regionDoc == null)
			return false;
		return regionDoc.content().getString("status").equals("opening");
	}

	public void queueUser(ID regionID, int userID) {
		try {
			DocumentFragment<Mutation> mutation = bucket
			    .mutateIn(regionID.toString())
			    .arrayAddUnique("queuedUsers", userID)
			    .execute();
			System.out.println("Mutated: " + mutation.id());
		} catch (MultiMutationException e) {
			if (e.getCause().getClass() == PathExistsException.class)
				System.err.println("Player is already queued: " + userID);
			else
				e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void unqueueUser(int userID) {
		Statement query = select("meta(instance_connections).id, queuedUsers")
				.from(i(bucket.name()))
				.where(x(userID).in(x("queuedUsers")));

        N1qlQueryResult result = bucket.query(N1qlQuery.simple(query));
		
        String regionID = null;
        JsonArray queuedUsers = null;
        for (N1qlQueryRow row : result) {
        	regionID = row.value().getString("id");
        	queuedUsers = row.value().getArray("queuedUsers");
        	break;
        }
        if (regionID == null)
        	return;
        
        List<Integer> list = (List<Integer>)(Object)queuedUsers.toList();
        list.remove((Integer)userID);

        
        if (list.size() == 0) {
        	removeInstance(new ID(regionID));
        } else {
        	//TODO: Use Cas
            queuedUsers = JsonArray.from(list);
    		bucket
			    .mutateIn(regionID.toString())
			    .replace("queuedUsers", queuedUsers)
			    .execute(); 
        }
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getQueuedUsers(ID regionID) {
		DocumentFragment<Lookup> result = bucket
		    .lookupIn(regionID.toString())
		    .get("queuedUsers")
		    .execute();
		JsonArray users = result.content("queuedUsers", JsonArray.class);
		return (List<Integer>)(Object)users.toList();
	}

	public String getRegionInstance(ID regionID) {
		JsonDocument regionDoc = bucket.get(regionID.toString());
		if (regionDoc == null)
			return null;
		return regionDoc.content().getString("instanceID");
	}
	
	public ID getRegionFromInstance(String instanceID) {
		Statement query = select("meta(instance_connections).id").from(i(bucket.name())).where(x("instanceID").eq(s(instanceID)));
        N1qlQueryResult result = bucket.query(N1qlQuery.simple(query));
		
        for (N1qlQueryRow row : result) {
        	return new ID(row.value().getString("id"));
        }

		return null;
	}
	
	public int getInstancePort(ID regionID) {
		JsonDocument instanceDoc = bucket.get(regionID.toString());
		return instanceDoc.content().getInt("port");
	}

	public Object getInstanceHost(ID regionID) {
		JsonDocument instanceDoc = bucket.get(regionID.toString());
		return instanceDoc.content().getString("host");
	}
}

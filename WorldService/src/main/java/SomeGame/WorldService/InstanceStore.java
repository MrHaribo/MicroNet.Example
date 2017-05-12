package SomeGame.WorldService;

import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.s;
import static com.couchbase.client.java.query.dsl.Expression.x;

import java.util.Arrays;
import java.util.List;

import com.couchbase.client.core.message.kv.subdoc.multi.Lookup;
import com.couchbase.client.core.message.kv.subdoc.multi.Mutation;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.error.DocumentAlreadyExistsException;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.Statement;
import com.couchbase.client.java.query.dsl.path.AsPath;
import com.couchbase.client.java.search.SearchQuery;
import com.couchbase.client.java.search.queries.ConjunctionQuery;
import com.couchbase.client.java.subdoc.DocumentFragment;

import micronet.model.ID;

public class InstanceStore {
	
	private Cluster cluster;
	private Bucket bucket;

	public InstanceStore() {
		cluster = CouchbaseCluster.create("localhost");
		bucket = cluster.openBucket("instance_connections");
        bucket.bucketManager().createN1qlPrimaryIndex(true, false);
	}

	public void addInstance(String instanceID) {
		try {
			JsonDocument doc = JsonDocument.create(instanceID, JsonObject.create().put("status", "free"));
			System.out.println(bucket.insert(doc));
		} catch (DocumentAlreadyExistsException e) {
			System.err.println("Instance already exists: " + instanceID);
			e.printStackTrace();
		}
	}
	
	public String reserveInstance(ID regionID) {

		Statement query = select("meta(instance_connections).id").from(i(bucket.name())).where(x("status").eq(s("free")));
		N1qlQueryResult result = bucket.query(N1qlQuery.simple(query));

		String freeInstanceID = null;
		for (N1qlQueryRow row : result) {
			System.out.println(row);
			freeInstanceID = row.value().getString("id");
			break;
		}
		if (freeInstanceID == null)
			return null;
		
		DocumentFragment<Mutation> mutation = bucket
		    .mutateIn(freeInstanceID)
		    .replace("status", "opening")
		    .insert("regionID", regionID.toString())
		    .execute();
		System.out.println(mutation.toString());
		
		JsonObject regionObj = JsonObject.create()
            .put("regionID", regionID.toString())
            .put("instanceID", freeInstanceID)
            .put("queuedUsers", JsonArray.empty());
		
		JsonDocument doc = JsonDocument.create(regionID.toString(), regionObj);
		System.out.println(bucket.insert(doc));
		
		return freeInstanceID;
	}

	public void readyInstance(String instanceID, String host, int port) {
		DocumentFragment<Mutation> mutation = bucket
		    .mutateIn(instanceID)
		    .replace("status", "open")
		    .insert("host", host)
		    .insert("port", port)
		    .execute();
		System.out.println(mutation.toString());
	}

	public void resetInstance(String instanceID) {
		DocumentFragment<Mutation> mutation = bucket
		    .mutateIn(instanceID)
		    .replace("status", "free")
		    .remove("regionID")
		    .remove("host")
		    .remove("port")
		    .execute();
		System.out.println(mutation.toString());
	}

	public boolean isRegionOpen(ID regionID) {
		JsonDocument regionDoc = bucket.get(regionID.toString());
		if (regionDoc == null)
			return false;
		String instanceID = regionDoc.content().getString("instanceID");
		if (instanceID == null)
			return false;
		JsonDocument instanceDoc = bucket.get(instanceID);
		return instanceDoc.content().getString("status").equals("open");
	}

	public boolean isRegionOpening(ID regionID) {
		JsonDocument regionDoc = bucket.get(regionID.toString());
		if (regionDoc == null)
			return false;
		String instanceID = regionDoc.content().getString("instanceID");
		if (instanceID == null)
			return false;
		JsonDocument instanceDoc = bucket.get(instanceID);
		return instanceDoc.content().getString("status").equals("opening");
	}

	public void queueUser(ID regionID, int userID) {
		DocumentFragment<Mutation> mutation = bucket
		    .mutateIn(regionID.toString())
		    .arrayAddUnique("queuedUsers", userID)
		    .execute();
		System.out.println(mutation.toString());
	}
	
	public List<Integer> getQueuedUsers(ID regionID) {

		DocumentFragment<Lookup> result = bucket
		    .lookupIn(regionID.toString())
		    .get("queuedUsers")
		    .execute();
		
		Integer[] users = result.content("queuedUsers", Integer[].class);
		return Arrays.asList(users);
	}

	public String getRegionInstance(ID regionID) {
		JsonDocument regionDoc = bucket.get(regionID.toString());
		if (regionDoc == null)
			return null;
		return regionDoc.content().getString("instanceID");
	}
	
	public int getInstancePort(String instanceID) {
		JsonDocument instanceDoc = bucket.get(instanceID);
		return instanceDoc.content().getInt("port");
	}

	public Object getInstanceHost(String instanceID) {
		JsonDocument instanceDoc = bucket.get(instanceID);
		return instanceDoc.content().getString("host");
	}

//	public Instance getInstance(String instanceID) {
//		JsonDocument doc = bucket.get(instanceID);
//		if (doc == null)
//			return null;
//		return Serialization.deserialize(doc.content().toString(), Instance.class);
//	}
//	
//	public Instance getFreeInstance() {
//        N1qlQueryResult result = bucket.query(
//            N1qlQuery.simple("SELECT id, host, port, status FROM instance_connections WHERE status=0")
//        );
//
//        for (N1qlQueryRow row : result) {
//        	System.out.println(row);
//        	return Serialization.deserialize(row.value().toString(), Instance.class);
//        }
//		return null;
//	}
//	
//	public RegionInstance getRegion(ID regionID) {
//		JsonDocument doc = bucket.get(regionID.toString());
//		if (doc == null)
//			return null;
//		return Serialization.deserialize(doc.content().toString(), RegionInstance.class);
//	}
//	
//	public void add(Instance instance) {
//		System.out.println("Add Instance: " + instance.getID());
//        bucket.insert(JsonDocument.create(instance.getID(), JsonObject.fromJson(Serialization.serialize(instance))));
//
//	}
//	
//	public void add(RegionInstance instance) {
//		System.out.println("Add Instance: " + instance.getRegionID());
//        bucket.insert(JsonDocument.create(instance.getRegionID(), JsonObject.fromJson(Serialization.serialize(instance))));
//	}
//
//	public Instance remove(String id) {
//		System.out.println("Remove Region Instance: " + id);
//		JsonDocument doc = bucket.remove(id);
//		if (doc == null)
//			return null;
//		return Serialization.deserialize(doc.content().toString(), Instance.class);
//	}	
//	
//	public void UpdateRegion() {
//		
//	}
}

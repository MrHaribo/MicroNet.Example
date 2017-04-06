package SomeGame.RegionService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.postgresql.util.PGobject;

import micronet.database.Database;
import micronet.model.ID;
import micronet.model.RegionValues;
import micronet.serialization.Serialization;

public class RegionDatabase extends Database {

	public static void main(String[] args) {
		new RegionDatabase();
	}

	public RegionDatabase() {
		super("region_db", "region_service", "region1234");
	}

	public boolean addMasterRegion(RegionValues region) {
		try {
			String data = Serialization.serialize(region);

			PGobject dataObject = new PGobject();
			dataObject.setType("json");
			dataObject.setValue(data);

			String sql = "INSERT INTO master_regions (region_id,data) VALUES (?,?);";
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setShort(1, region.getID().getValue());
			stmt.setObject(2, dataObject);

			stmt.executeUpdate();
			stmt.close();
			return true;
		} catch (SQLException sqle) {
			System.err.println("Add: " + sqle.getMessage());
		}
		return false;
	}

	public RegionValues getMasterRegion(ID regionID) {
		try {
			String queryString = "SELECT data FROM master_regions WHERE region_id = ?;";
			PreparedStatement stmt = getConnection().prepareStatement(queryString);
			stmt.setShort(1, regionID.getValue());

			ResultSet result = stmt.executeQuery();
			if (result.next()) {
				String data = result.getString(1);
				RegionValues region = Serialization.deserialize(data, RegionValues.class);
				region.setID(regionID);
				return region;
			}
			stmt.close();
		} catch (SQLException sqle) {
			System.err.println("Get: " + sqle.getMessage());
		}
		return null;
	}

	public List<RegionValues> getAllMasterRegions() {
		try {
			String queryString = "SELECT data FROM master_regions;";
			PreparedStatement stmt = getConnection().prepareStatement(queryString);
			ResultSet result = stmt.executeQuery();

			List<RegionValues> arrayList = new ArrayList<>();
			while (result.next()) {
				RegionValues region = Serialization.deserialize(result.getString(1), RegionValues.class);
				arrayList.add(region);
			}
			stmt.close();
			return arrayList;
		} catch (SQLException sqle) {
			System.err.println("Get All Regions: " + sqle.getMessage());
		}
		return null;
	}

	public List<String> getAllMasterRegionsRaw() {
		try {
			String queryString = "SELECT data FROM master_regions;";
			PreparedStatement stmt = getConnection().prepareStatement(queryString);
			ResultSet result = stmt.executeQuery();

			List<String> arrayList = new ArrayList<>();
			while (result.next()) {
				arrayList.add(result.getString(1));
			}
			stmt.close();
			return arrayList;
		} catch (SQLException sqle) {
			System.err.println("Get All Regions Raw: " + sqle.getMessage());
		}
		return null;
	}

	public void clearMasterRegions() {
		try {
			String queryString = "DELETE FROM master_regions;";
			PreparedStatement stmt = getConnection().prepareStatement(queryString);
			stmt.execute();
			stmt.close();
		} catch (SQLException sqle) {
			System.err.println("Clear regions: " + sqle.getMessage());
		}
	}
}

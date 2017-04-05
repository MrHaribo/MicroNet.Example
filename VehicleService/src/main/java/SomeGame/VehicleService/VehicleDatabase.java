package SomeGame.VehicleService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.postgresql.util.PGobject;

import micronet.database.Database;
import micronet.model.VehicleValues;
import micronet.serialization.Serialization;

public class VehicleDatabase extends Database {
	public VehicleDatabase() {
		super("vehicle_db", "vehicle_service", "vehicle1234");
	}

	public void setCurrentVehicle(int userID, String name, int index) {
		try {
			String sql = "UPDATE vehicles SET current_vehicle_index = ? WHERE user_id = ? AND avatar_name = ?;";
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setInt(1, index);
			stmt.setInt(2, userID);
			stmt.setString(3, name);
			stmt.execute();
			stmt.close();
		} catch (SQLException sqle) {
			System.err.println("Error setting current vehicle: " + sqle.getMessage());
		}
	}

	public int getCurrentVehicleIndex(int userID, String name) {
		try {
			String sql = "SELECT current_vehicle_index FROM vehicles WHERE user_id = ? AND avatar_name = ?;";
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setInt(1, userID);
			stmt.setString(2, name);
			ResultSet result = stmt.executeQuery();
			if (result.next()) {
				return result.getInt(1);
			}
			stmt.close();
		} catch (SQLException sqle) {
			System.err.println("Error getting current vehicle: " + sqle.getMessage());
		}
		return -1;
	}

	public VehicleValues getCurrentVehicle(int userID, String name) {
		try {
			String sql = "SELECT current_vehicle_index FROM vehicles WHERE user_id = ? AND avatar_name = ?;";
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setInt(1, userID);
			stmt.setString(2, name);
			ResultSet result = stmt.executeQuery();
			if (result.next()) {
				return getVehicle(userID, name, result.getInt(1));
			}
			stmt.close();
		} catch (SQLException sqle) {
			System.err.println("Error getting current vehicle: " + sqle.getMessage());
		}
		return null;
	}

	public void createVehicleCollection(int userID, String name) {
		try {
			String sql = "INSERT INTO vehicles (user_id, avatar_name) VALUES (?,?)";
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setInt(1, userID);
			stmt.setString(2, name);
			stmt.execute();
			stmt.close();
		} catch (SQLException sqle) {
			System.err.println("Error getting current vehicle: " + sqle.getMessage());
		}
	}

	public void deleteVehicleCollection(int userID, String name) {
		try {
			String sql = "DELETE FROM vehicles WHERE user_id = ? AND avatar_name = ?;";
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setInt(1, userID);
			stmt.setString(2, name);
			stmt.execute();
			stmt.close();
		} catch (SQLException sqle) {
			System.err.println("Error getting current vehicle: " + sqle.getMessage());
		}
	}

	public void addVehicle(int userID, String name, VehicleValues vehicle) {
		try {
			PGobject dataObject = new PGobject();
			dataObject.setType("json");
			dataObject.setValue(Serialization.serialize(vehicle));

			String sql = "UPDATE vehicles SET data = array_append(data, ?) WHERE user_id = ? AND avatar_name = ?;";
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setObject(1, dataObject);
			stmt.setInt(2, userID);
			stmt.setString(3, name);
			stmt.execute();
			stmt.close();
		} catch (SQLException sqle) {
			System.err.println("Error adding vehicle: " + sqle.getMessage());
		}
	}

	public void deleteVehicle(int userID, String name, int vehicleIndex) {
		try {
			if (vehicleIndex == 0) {
				String sql = "UPDATE vehicles SET data = data[2:2147483647] WHERE user_id = ? AND avatar_name = ?;";
				PreparedStatement stmt = getConnection().prepareStatement(sql);
				stmt.setInt(1, userID);
				stmt.setString(2, name);
				stmt.execute();
				stmt.close();
			} else {
				String sql = "UPDATE vehicles SET data = data[1:?] || data[?:2147483647] WHERE user_id = ? AND avatar_name = ?;";
				PreparedStatement stmt = getConnection().prepareStatement(sql);
				stmt.setInt(1, vehicleIndex);
				stmt.setInt(2, vehicleIndex + 2);
				stmt.setInt(3, userID);
				stmt.setString(4, name);
				stmt.execute();
				stmt.close();
			}
		} catch (SQLException sqle) {
			System.err.println("Error selling vehicle: " + sqle.getMessage());
		}
	}

	public void updateVehicle(int userID, String name, int index, VehicleValues vehicle) {
		try {
			PGobject dataObject = new PGobject();
			dataObject.setType("json");
			dataObject.setValue(Serialization.serialize(vehicle));

			String sql = "UPDATE vehicles SET data[?] = ? WHERE user_id = ? AND avatar_name = ?;";
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setInt(1, index + 1);
			stmt.setObject(2, dataObject);
			stmt.setInt(3, userID);
			stmt.setString(4, name);
			stmt.execute();
			stmt.close();
		} catch (SQLException sqle) {
			System.err.println("Error adding vehicle: " + sqle.getMessage());
		}
	}

	public VehicleValues getVehicle(int userID, String name, int index) {
		try {
			String sql = "SELECT data[?] FROM vehicles WHERE user_id = ? AND avatar_name = ?;";
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setInt(1, index + 1);
			stmt.setInt(2, userID);
			stmt.setString(3, name);
			ResultSet result = stmt.executeQuery();
			if (result.next()) {
				return Serialization.deserialize(result.getString(1), VehicleValues.class);
			}
			stmt.close();
		} catch (SQLException sqle) {
			System.err.println("Error getting vehicle: " + sqle.getMessage());
		}
		return null;
	}

	public VehicleValues[] getVehicles(int userID, String name) {
		try {
			String sql = "SELECT array_to_json(data) FROM vehicles WHERE user_id = ? AND avatar_name = ?;";
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setInt(1, userID);
			stmt.setString(2, name);
			ResultSet result = stmt.executeQuery();
			if (result.next()) {
				String data = result.getString(1);
				VehicleValues[] vehicles = Serialization.deserialize(data, VehicleValues[].class);
				return vehicles;
			}
			stmt.close();
		} catch (SQLException sqle) {
			System.err.println("Error getting vehicles: " + sqle.getMessage());
		}
		return null;
	}

	public void saveVehicleConfigurations(VehicleValues[] vehicles) {
		try {
			String sql = "DELETE FROM vehicle_configurations;";
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.execute();
			stmt.close();

			for (VehicleValues vehicle : vehicles) {
				sql = "INSERT INTO vehicle_configurations (name, data) VALUES (?, ?)";
				stmt = getConnection().prepareStatement(sql);

				PGobject dataObject = new PGobject();
				dataObject.setType("json");
				dataObject.setValue(Serialization.serialize(vehicle));

				stmt.setString(1, vehicle.getName());
				stmt.setObject(2, dataObject);
				stmt.execute();
			}
			stmt.close();
		} catch (SQLException sqle) {
			System.err.println("Error getting vehicles: " + sqle.getMessage());
		}
	}

	public VehicleValues loadVehicleConfiguration(String name) {
		try {
			String sql = "SELECT data FROM vehicle_configurations WHERE name = ?;";
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setString(1, name);
			ResultSet result = stmt.executeQuery();
			if (result.next()) {
				String data = result.getString(1);
				VehicleValues vehicle = Serialization.deserialize(data, VehicleValues.class);
				return vehicle;
			}
			stmt.close();
		} catch (SQLException sqle) {
			System.err.println("Error getting vehicle config: " + sqle.getMessage());
		}
		return null;
	}
}

package SomeGame.ItemService;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.postgresql.util.PGobject;

import micronet.database.Database;
import micronet.model.ItemValues;
import micronet.serialization.Serialization;

public class ItemDatabase extends Database {

	public ItemDatabase() {
		super("item_db", "item_service", "item1234");
	}

	public ItemValues[] getInventory(int userId, String location) {

		try {
			String sql = "SELECT size, inventory FROM items WHERE user_id = ? AND location = ?;";
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setInt(1, userId);
			stmt.setString(2, location);

			ResultSet result = stmt.executeQuery();
			if (result.next()) {
				int size = result.getInt(1);
				Array array = result.getArray(2);

				ItemValues[] items = new ItemValues[size];
				if (array == null)
					return items;

				ResultSet arrayResult = array.getResultSet();
				while (arrayResult.next()) {
					int index = arrayResult.getInt(1);
					String json = arrayResult.getString(2);
					items[index - 1] = Serialization.deserialize(json, ItemValues.class);
				}
				return items;
			}
			stmt.close();
		} catch (SQLException sqle) {
			System.err.println("Error getting inv: " + sqle.getMessage());
		}
		return null;
	}

	public void createInventory(int userId, String location, int size) {
		try {
			String sql = "INSERT INTO items VALUES (?, ?, ?);";
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setInt(1, userId);
			stmt.setString(2, location);
			stmt.setInt(3, size);
			stmt.execute();
			stmt.close();
		} catch (SQLException sqle) {
			System.err.println("Error creating inv: " + sqle.getMessage());
		}
	}

	public boolean hasItem(int userId, String location, int index) {
		boolean itemFound = false;
		try {
			String sql = "SELECT inventory[?] IS NOT NULL FROM items WHERE user_id = ?";
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setInt(1, index);
			stmt.setInt(2, userId);

			ResultSet result = stmt.executeQuery();
			if (result.next()) {
				itemFound = result.getBoolean(1);
			}
			stmt.close();
		} catch (SQLException sqle) {
			System.err.println("Error checking item: " + sqle.getMessage());
		}
		return itemFound;
	}

	public boolean addItem(int userId, String location, ItemValues item) {
		ItemValues[] inventory = getInventory(userId, location);
		for (int i = 0; i < inventory.length; i++) {
			if (inventory[i] == null) {
				setItem(userId, location, i, item);
				return true;
			}
		}
		return false;
	}

	public void setItem(int userId, String location, int index, ItemValues item) {

		// TODO: Check if itemIndex < size

		try {
			String data = Serialization.serialize(item);
			PGobject dataObject = new PGobject();
			dataObject.setType("json");
			dataObject.setValue(data);

			String sql = "UPDATE items SET inventory[?] = ?::json WHERE user_id = ? AND location = ?";
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setInt(1, index);
			stmt.setObject(2, dataObject);
			stmt.setInt(3, userId);
			stmt.setString(4, location);

			stmt.execute();
			stmt.close();
		} catch (SQLException sqle) {
			System.err.println("Error setting item: " + sqle.getMessage());
		}
	}

	public ItemValues getItem(int userId, String location, int index) {

		// TODO: Check if itemIndex < size

		try {
			String sql = "SELECT inventory[?] FROM items WHERE user_id = ? AND location = ?";
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setInt(1, index);
			stmt.setInt(2, userId);
			stmt.setString(3, location);

			ResultSet result = stmt.executeQuery();
			if (result.next()) {
				String data = result.getString(1);
				return Serialization.deserialize(data, ItemValues.class);
			}
			stmt.close();
		} catch (SQLException sqle) {
			System.err.println("Error get item: " + sqle.getMessage());
		}
		return null;
	}

	public void deleteItem(int userId, String location, int index) {
		try {
			String sql = "UPDATE items SET inventory[?] = NULL WHERE user_id = ? AND location = ?";
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setInt(1, index);
			stmt.setInt(2, userId);
			stmt.setString(3, location);
			stmt.execute();
			stmt.close();
		} catch (SQLException sqle) {
			System.err.println("Error delete Item: " + sqle.getMessage());
		}
	}
}

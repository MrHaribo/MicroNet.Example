package SomeGame.ShopService;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import micronet.database.Database;
import micronet.model.ItemValues;
import micronet.serialization.Serialization;

public class ShopDatabase extends Database {

	public ShopDatabase() {
		super("shop_db", "shop_service", "shop1234");
	}

	public void addShop(String faction, ItemValues[] items, Integer[] rankRestrictions) {

		try {
			String sql = "DELETE FROM shops WHERE faction=?;";
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setString(1, faction);
			stmt.execute();
			stmt.close();

			sql = "INSERT INTO shops VALUES (?, ?, ?);";
			stmt = getConnection().prepareStatement(sql);
			stmt.setString(1, faction);

			String[] itemsJson = new String[items.length];
			for (int i = 0; i < items.length; i++) {
				itemsJson[i] = Serialization.serialize(items[i]);
			}

			stmt.setArray(2, getConnection().createArrayOf("json", itemsJson));
			stmt.setArray(3, getConnection().createArrayOf("integer", rankRestrictions));
			stmt.execute();
			stmt.close();
		} catch (SQLException sqle) {
			System.err.println("Error running the select: " + sqle.getMessage());
		}
	}

	public ItemValues[] getShop(String faction) {

		try {
			String sql = "SELECT array_length(items, 1), items FROM shops WHERE faction = ?;";
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setString(1, faction);

			ResultSet result = stmt.executeQuery();
			if (result.next()) {
				int size = result.getInt(1);
				Array array = result.getArray(2);

				if (size == 0)
					return new ItemValues[0];
				ItemValues[] items = new ItemValues[size];

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
			System.err.println("Error running the select: " + sqle.getMessage());
		}
		return null;
	}
	
	public int[] getRankRestrictions(String faction) {

		try {
			String sql = "SELECT array_length(rank_restrictions, 1), rank_restrictions FROM shops WHERE faction = ?;";
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setString(1, faction);

			ResultSet result = stmt.executeQuery();
			if (result.next()) {
				int size = result.getInt(1);
				Array array = result.getArray(2);

				if (size == 0)
					return new int[0];
				int[] restrictions = new int[size];

				ResultSet arrayResult = array.getResultSet();
				while (arrayResult.next()) {
					int index = arrayResult.getInt(1);
					restrictions[index - 1] = arrayResult.getInt(2);
				}

				return restrictions;
			}
			stmt.close();
		} catch (SQLException sqle) {
			System.err.println("Error running the select: " + sqle.getMessage());
		}
		return null;
	}


}

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

	public void addShop(String faction, ItemValues[] items) {

		try {
			String sql = "DELETE FROM shops WHERE faction=?;";
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setString(1, faction);
			stmt.execute();
			stmt.close();

			sql = "INSERT INTO shops VALUES (?, ?);";
			stmt = getConnection().prepareStatement(sql);
			stmt.setString(1, faction);

			String[] itemsJson = new String[items.length];
			for (int i = 0; i < items.length; i++) {
				itemsJson[i] = Serialization.serialize(items[i]);
			}
			Array itemArray = getConnection().createArrayOf("json", itemsJson);
			stmt.setArray(2, itemArray);
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

}

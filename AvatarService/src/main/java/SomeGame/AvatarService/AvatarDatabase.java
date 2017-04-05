package SomeGame.AvatarService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.postgresql.util.PGobject;

import micronet.database.Database;
import micronet.model.AvatarValues;
import micronet.serialization.Serialization;

public class AvatarDatabase extends Database {
	public AvatarDatabase() {
		super("avatar_db", "avatar_service", "avatar1234");
	}

	public void addAvatar(int userID, AvatarValues avatar) {
		try {
			PGobject dataObject = new PGobject();
			dataObject.setType("json");
			dataObject.setValue(Serialization.serialize(avatar));

			String sql = "INSERT INTO avatars VALUES (?, ?, ?);";
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setInt(1, userID);
			stmt.setString(2, avatar.getName());
			stmt.setObject(3, dataObject);
			stmt.execute();
			stmt.close();
		} catch (SQLException sqle) {
			System.err.println("Error adding Avatar: " + sqle.getMessage());
		}
	}

	public void updateAvatar(int userID, String name, Function<AvatarValues, AvatarValues> updateFunction) {

		PreparedStatement readAvatar = null;
		PreparedStatement writeAvatar = null;

		String readSql = "SELECT data FROM avatars WHERE user_id = ? AND name = ?;";
		String writeSql = "UPDATE avatars SET data = ? WHERE user_id = ? AND name = ?;";

		try {
			getConnection().setAutoCommit(false);

			readAvatar = getConnection().prepareStatement(readSql);
			readAvatar.setInt(1, userID);
			readAvatar.setString(2, name);
			ResultSet result = readAvatar.executeQuery();
			if (result.next()) {
				AvatarValues avatar = Serialization.deserialize(result.getString(1), AvatarValues.class);
				if (avatar == null)
					throw new SQLException("Avatar is null: " + userID + " : " + name);

				AvatarValues updatedAvatar = updateFunction.apply(avatar);
				PGobject dataObject = new PGobject();
				dataObject.setType("json");
				dataObject.setValue(Serialization.serialize(updatedAvatar));

				writeAvatar = getConnection().prepareStatement(writeSql);
				writeAvatar.setObject(1, dataObject);
				writeAvatar.setInt(2, userID);
				writeAvatar.setString(3, name);
				writeAvatar.execute();

				getConnection().commit();
			} else {
				throw new SQLException("Avatar does not exist: " + userID + " : " + name);
			}
		} catch (SQLException e) {
			System.err.println("Error updating avatar: " + e.getMessage());
			if (getConnection() != null) {
				try {
					System.err.print("Transaction is being rolled back");
					getConnection().rollback();
				} catch (SQLException excep) {
					System.err.println("Error rollback avatar update: " + excep.getMessage());
				}
			}
		} finally {
			try {
				if (readAvatar != null) {
					readAvatar.close();
				}
				if (writeAvatar != null) {
					writeAvatar.close();
				}
				getConnection().setAutoCommit(true);
			} catch (SQLException excep) {
				System.err.println("Error finalizing avatar update: " + excep.getMessage());
			}
		}
	}

	public AvatarValues getAvatar(int userID, String name) {
		try {
			String sql = "SELECT data FROM avatars WHERE user_id = ? AND name = ?;";
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setInt(1, userID);
			stmt.setString(2, name);
			ResultSet result = stmt.executeQuery();
			if (result.next()) {
				return Serialization.deserialize(result.getString(1), AvatarValues.class);
			}
			stmt.close();
		} catch (SQLException sqle) {
			System.err.println("Error getting vehicle: " + sqle.getMessage());
		}
		return null;
	}

	public void deleteAvatar(int userID, String name) {
		try {
			String sql = "DELETE FROM avatars WHERE user_id = ? AND name = ?;";
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setInt(1, userID);
			stmt.setString(2, name);
			stmt.execute();
			stmt.close();
		} catch (SQLException sqle) {
			System.err.println("Error adding Avatar: " + sqle.getMessage());
		}
	}

	public AvatarValues[] getAvatars(int userID) {
		return getAvatarList(userID).toArray(new AvatarValues[0]);
	}

	public List<AvatarValues> getAvatarList(int userID) {
		List<AvatarValues> avatars = new ArrayList<>();
		try {
			String sql = "SELECT data FROM avatars WHERE user_id = ?;";
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setInt(1, userID);
			ResultSet result = stmt.executeQuery();
			while (result.next()) {
				String data = result.getString(1);
				AvatarValues avatar = Serialization.deserialize(data, AvatarValues.class);
				avatars.add(avatar);
			}
			stmt.close();
		} catch (SQLException sqle) {
			System.err.println("Error getting vehicles: " + sqle.getMessage());
		}
		return avatars;
	}
}

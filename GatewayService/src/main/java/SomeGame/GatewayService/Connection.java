package SomeGame.GatewayService;

import java.net.URI;

class Connection {
	private String connectionID;
	private int userID;

	public Connection(String connectionId, int userID) {
		super();
		this.connectionID = connectionId;
		this.userID = userID;
	}

	public String getConnectionID() {
		return connectionID;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public URI getQueueURI() {
		return URI.create("mn://" + getConnectionID());
	}
}
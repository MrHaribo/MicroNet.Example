package SomeGame.GatewayService;

import java.net.URI;

import micronet.model.CredentialValues;

class ClientConnection {
	private String connectionId;
	private boolean authenticated;
	private int userId;
	private CredentialValues credentials;

	public ClientConnection(String connectionId) {
		this.connectionId = connectionId;
		this.authenticated = false;
	}

	public boolean isAuthenticated() {
		return authenticated;
	}

	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}

	public String getConnectionId() {
		return connectionId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public CredentialValues getCredentials() {
		return credentials;
	}

	public void setCredentials(CredentialValues credentials) {
		this.credentials = credentials;
	}

	public URI getQueueURI() {
		// TODO: only use one queue on client also
		return URI.create("mn://" + getConnectionId());
	}
}
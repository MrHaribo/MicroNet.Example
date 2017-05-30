package SomeGame.TestClient;

import java.net.URI;

import micronet.model.AvatarValues;
import micronet.model.CredentialValues;
import micronet.network.Context;
import micronet.network.Request;
import micronet.network.Response;
import micronet.network.factory.PeerFactory;
import micronet.serialization.Serialization;

public class TestClient {

	public static void main(String[] args) {
		Context context = new Context(PeerFactory.createPeer());

		CredentialValues creds = new CredentialValues();
		creds.setUsername("");
		creds.setPassword("");
		
		Request loginRequest = new Request(Serialization.serialize(creds));
		loginRequest.getParameters().set("USER_REQUEST", "mn://account/login");
		context.getPeer().sendRequest(URI.create("mn://request3"), loginRequest, response -> onLogin(context, response));
	}
	
	static void onLogin(Context context, Response response) {
		System.out.println("Login: " + response);

		AvatarValues avatar = new AvatarValues("Hans");
		
		Request createAvatarRequest = new Request(Serialization.serialize(avatar));
		createAvatarRequest.getParameters().set("FACTION", "Rebel");
		createAvatarRequest.getParameters().set("USER_REQUEST", "mn://avatar/create");
		context.getPeer().sendRequest(URI.create("mn://request"), createAvatarRequest, avatarCreatedResponse -> onAvatarCreated(context, avatarCreatedResponse));
	}
	
	static void onAvatarCreated(Context context, Response response) {
		System.out.println("Create Avatar: " + response);
		
		Request avatarSelectRequest = new Request("Hans");
		avatarSelectRequest.getParameters().set("USER_REQUEST", "mn://avatar/current/set");
		context.getPeer().sendRequest(URI.create("mn://request"), avatarSelectRequest, avatarSelectedResponse -> onAvatarSelect(context, avatarSelectedResponse));
	}
	
	static void onAvatarSelect(Context context, Response response) {
		Request joinRequest = new Request();
		joinRequest.getParameters().set("USER_REQUEST", "mn://world/travel/home");
		context.getPeer().sendRequest(URI.create("mn://request"), joinRequest, travelResponse -> {
			System.out.println("Avatar Select: " + travelResponse);
		});
	}
}

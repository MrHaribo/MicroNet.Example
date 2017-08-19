package MyGame.TestClient;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.DefaultWindowManager;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalResizeListener;

import MyGame.TestClient.UI.ConsoleWindow;
import MyGame.TestClient.UI.GameInformationWindow;
import MyGame.TestClient.UI.HeaderWindow;
import MyGame.TestClient.UI.LoginWindow;
import MyGame.TestClient.UI.ResultWindow;
import MyGame.TestClient.UI.VoteWindow;
import micronet.network.IPeer;
import micronet.network.Request;
import micronet.network.Response;
import micronet.network.StatusCode;
import micronet.network.factory.PeerFactory;
import micronet.serialization.Serialization;

public class TestClient {

	private static LoginWindow loginWindow;
	private static ConsoleWindow console;
	private static GameInformationWindow gameInfoWindow;
	private static VoteWindow voteWindow;

	private static IPeer peer;
	private static MultiWindowTextGUI gui;
	private static ResultWindow resultWindow;

	public static void main(String[] args) throws IOException {

		Terminal terminal = new DefaultTerminalFactory().createTerminal();

		Screen screen = new TerminalScreen(terminal);
		screen.startScreen();

		gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));

		HeaderWindow headerWindow = new HeaderWindow(terminal.getTerminalSize());
		headerWindow.refreshLayout(terminal.getTerminalSize());

		console = new ConsoleWindow();
		console.refreshLayout(terminal.getTerminalSize());

		gameInfoWindow = new GameInformationWindow(terminal.getTerminalSize());
		gameInfoWindow.refreshLayout(terminal.getTerminalSize());
		gameInfoWindow.setVisible(false);

		Runnable onVote = () -> sendVote();
		voteWindow = new VoteWindow(onVote);
		voteWindow.setVisible(false);
		
		resultWindow = new ResultWindow();
		resultWindow.setVisible(false);

		Runnable onLogin = () -> sendLogin();
		Runnable onRegister = () -> sendRegister();
		loginWindow = new LoginWindow(onLogin, onRegister);

		terminal.addResizeListener(new TerminalResizeListener() {
			@Override
			public void onResized(Terminal terminal, TerminalSize newSize) {
				console.refreshLayout(newSize);
				gameInfoWindow.refreshLayout(newSize);
				headerWindow.refreshLayout(newSize);
			}
		});

		createPeer();

		gui.addWindow(headerWindow);
		gui.addWindow(console);
		gui.addWindow(gameInfoWindow);
		gui.addWindow(voteWindow);
		gui.addWindow(resultWindow);
		gui.addWindowAndWait(loginWindow);

		gameInfoWindow.stopRoundUpdate();
		peer.shutdown();
	}

	private static void createPeer() {
		Thread networkThread = new Thread(() -> {
			peer = PeerFactory.createClientPeer();
			peer.listen(Event.ScoreUpdate.toString(), event -> {
				Player[] allPlayers = Serialization.deserialize(event.getData(), Player[].class);
				gameInfoWindow.refreshPlayerScores(allPlayers);
			});
			peer.listen(Event.RoundStart.toString(), event -> {
				gameInfoWindow.setTimeDisplay("Remaining: ", Integer.parseInt(event.getData()));
				voteWindow.setVisible(true);
				resultWindow.setVisible(false);
				gui.setActiveWindow(voteWindow);
			});
			peer.listen(Event.RoundEnd.toString(), event -> {
				gameInfoWindow.setTimeDisplay("Next Round in: ", Integer.parseInt(event.getData()));
				voteWindow.setVisible(false);
				gui.setActiveWindow(gameInfoWindow);
			});
		});
		networkThread.setDaemon(true);
		networkThread.start();
	}

	private static void sendVote() {

		Integer vote = voteWindow.getVote();
		if (vote == null) {
			console.print("Incorrect Vote Format");
			return;
		}
		console.print("Sending Vote: " + voteWindow.getVote());
		voteWindow.setVisible(false);
		gui.setActiveWindow(gameInfoWindow);

		Request loginRequest = new Request(Integer.toString(voteWindow.getVote()));
		peer.sendRequest(URI.create("mn://vote/put"), loginRequest, response -> onVote(response, vote));
	}

	private static void onVote(Response response, int guessedValue) {
		console.print("Vote Response: " + response.getStatus());
		resultWindow.setResult(Serialization.deserialize(response.getData(), VoteResult.class), guessedValue);
		resultWindow.setVisible(true);
		gui.setActiveWindow(resultWindow);
	}

	private static void sendLogin() {
		console.print("Sending Login: " + loginWindow.getUsername() + " -> " + hidePasswordString());
		CredentialValues creds = new CredentialValues();
		creds.setUsername(loginWindow.getUsername());
		creds.setPassword(loginWindow.getPassword());

		Request loginRequest = new Request(Serialization.serialize(creds));
		peer.sendRequest(URI.create("mn://account/login"), loginRequest, response -> onLogin(response));
	}

	static void onLogin(Response response) {
		console.print("Login: " + response);

		if (response.getStatus() == StatusCode.OK) {
			loginWindow.setVisible(false);
			gameInfoWindow.setVisible(true);
			
			peer.sendRequest(URI.create("mn://player/score/all"), new Request(), scoreResponse -> {
				Player[] allPlayers = Serialization.deserialize(scoreResponse.getData(), Player[].class);
				gameInfoWindow.refreshPlayerScores(allPlayers);
			});
		}
	}

	private static void sendRegister() {
		console.print("Sending Register: " + loginWindow.getUsername() + " -> " + hidePasswordString());
		CredentialValues creds = new CredentialValues();
		creds.setUsername(loginWindow.getUsername());
		creds.setPassword(loginWindow.getPassword());

		Request request = new Request(Serialization.serialize(creds));
		peer.sendRequest(URI.create("mn://account/register"), request, response -> onRegister(response));
	}

	private static void onRegister(Response response) {
		console.print("Register: " + response);
	}

	private static String hidePasswordString() {
		char[] charArray = new char[loginWindow.getPassword().length()];
		Arrays.fill(charArray, '*');
		return new String(charArray);
	}
}

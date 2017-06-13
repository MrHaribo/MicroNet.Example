package SomeGame.TestClient;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.function.Consumer;

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

import SomeGame.TestClient.UI.ConsoleWindow;
import SomeGame.TestClient.UI.GameWindow;
import SomeGame.TestClient.UI.HeaderWindow;
import SomeGame.TestClient.UI.LoginWindow;
import micronet.network.Context;
import micronet.network.Request;
import micronet.network.Response;
import micronet.network.StatusCode;
import micronet.network.factory.PeerFactory;
import micronet.serialization.Serialization;

public class TestClient {
	
	private static LoginWindow loginWindow;
	private static ConsoleWindow console;
	private static GameWindow gameWindow;
	
	private static Context context;

	public static void main(String[] args) throws IOException {

        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        
        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();
        
        HeaderWindow headerWindow = new HeaderWindow(terminal.getTerminalSize());
        headerWindow.refreshLayout(terminal.getTerminalSize());

        console = new ConsoleWindow();
        console.refreshLayout(terminal.getTerminalSize());

        gameWindow = new GameWindow();
        
        Runnable onLogin = () -> {
        	console.print("Sending Login: " + loginWindow.getUsername() + " -> " + hidePasswordString());
        	sendLogin();
        };
        Runnable onRegister = () -> {
        	console.print("Sending Register: " + loginWindow.getUsername() + " -> " + hidePasswordString());
        	sendRegister();
        };
        
        loginWindow = new LoginWindow(onLogin, onRegister);

        terminal.addResizeListener(new TerminalResizeListener() {
			@Override
			public void onResized(Terminal terminal, TerminalSize newSize) {
				console.refreshLayout(newSize);
				headerWindow.refreshLayout(newSize);
			}
		});
        
        new Thread(() ->{
        	context = new Context(PeerFactory.createPeer());
        }).start();
        
        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));
       	gui.addWindow(headerWindow);
        gui.addWindow(console);
        gui.addWindow(gameWindow);
        gui.addWindowAndWait(loginWindow);
        
        context.shutdown();
    }

	private static void sendLogin() {
		CredentialValues creds = new CredentialValues();
		creds.setUsername(loginWindow.getUsername());
		creds.setPassword(loginWindow.getPassword());
		
		Request loginRequest = new Request(Serialization.serialize(creds));
		sendRequest("mn://account/login", loginRequest, response -> onLogin(response));
	}
	
	static void onLogin(Response response) {
		console.print("Login: " + response);
		
		if (response.getStatus() == StatusCode.OK) {
			loginWindow.setVisible(false);
			gameWindow.setVisible(true);
		}
	}
	
	private static void sendRegister() {
		CredentialValues creds = new CredentialValues();
		creds.setUsername(loginWindow.getUsername());
		creds.setPassword(loginWindow.getPassword());
		
		Request request = new Request(Serialization.serialize(creds));
		sendRequest("mn://account/register", request, response -> onRegister(response));
	}

	
	private static void onRegister(Response response) {
		console.print("Register: " + response);
	}

	private static void sendRequest(String destination, Request request, Consumer<Response> messageHandler) {
		request.getParameters().set(ParameterCode.USER_REQUEST, destination);
		context.getPeer().sendRequest(URI.create("mn://request"), request, messageHandler);
	}
	
	private static String hidePasswordString() {
		char[] charArray = new char[loginWindow.getPassword().length()];
		Arrays.fill(charArray, '*');
		return new String(charArray);
	}
}

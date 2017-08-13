package MyGame.TestClient.UI;

import java.util.Arrays;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.Window;

public class LoginWindow extends BasicWindow {

	private TextBox usernameTextBox;
	private TextBox passwordTextBox;
	private TextBox passwordConfirmTextBox;

	private Runnable onLogin;
	private Runnable onRegister;

	public LoginWindow(Runnable onLogin, Runnable onRegister) {
		this.onLogin = onLogin;
		this.onRegister = onRegister;
		
		setHints(Arrays.asList(Window.Hint.CENTERED, Window.Hint.NO_DECORATIONS));
		
		createLoginPanel();
	}

	private void createLoginPanel() {
		Panel panel = new Panel();
		panel.setLayoutManager(new GridLayout(2));

		usernameTextBox = new TextBox();
		passwordTextBox = new TextBox().setMask('*');

		panel.addComponent(new Label("Username"));
		panel.addComponent(usernameTextBox);

		panel.addComponent(new Label("Password"));
		panel.addComponent(passwordTextBox);

		panel.addComponent(new Button("Login", onLogin));
		
		panel.addComponent(new Button("Register", () -> {
			createRegisterPanel();
		}));
		
		setComponent(panel.withBorder(Borders.singleLine("Login")));
	}

	private void createRegisterPanel() {
		Panel panel = new Panel();
		panel.setLayoutManager(new GridLayout(2));

		usernameTextBox = new TextBox();
		passwordTextBox = new TextBox().setMask('*');
		passwordConfirmTextBox = new TextBox().setMask('*');

		panel.addComponent(new Label("Username"));
		panel.addComponent(usernameTextBox);

		panel.addComponent(new Label("Password"));
		panel.addComponent(passwordTextBox);
		
		panel.addComponent(new Label("Confirm"));
		panel.addComponent(passwordConfirmTextBox);
		
		panel.addComponent(new Button("Register", onRegister));

		panel.addComponent(new Button("Login", () -> {
			createLoginPanel();
		}));
		
		setComponent(panel.withBorder(Borders.singleLine("Register")));
	}

	public String getUsername() {
		return usernameTextBox.getText();
	}

	public String getPassword() {
		return passwordTextBox.getText();
	}
	
	public String getConfirmPassword() {
		return passwordConfirmTextBox.getText();
	}
}

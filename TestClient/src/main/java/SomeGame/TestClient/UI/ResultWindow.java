package SomeGame.TestClient.UI;

import java.util.Arrays;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.Window;

import SomeGame.TestClient.VoteResult;

public class ResultWindow extends BasicWindow {

	int topMargin = 5;
	int leftMargin = 1;

	int width = 40;
	int height = 12;

	private Label messageLabel;
	private Label guessedValueLabel;
	private Label realValueLabel;
	private Label scoreLabel;
	
	public ResultWindow() {
		
		setHints(Arrays.asList(Window.Hint.FIXED_POSITION));
		setPosition(new TerminalPosition(leftMargin, topMargin));
		setSize(new TerminalSize(width, height));

		Panel panel = new Panel();
		panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

		messageLabel = new Label("");
		realValueLabel = new Label("");
		guessedValueLabel = new Label("");
		scoreLabel = new Label("");
		
		
		panel.addComponent(messageLabel);
		panel.addComponent(realValueLabel);
		panel.addComponent(guessedValueLabel);
		panel.addComponent(scoreLabel);

		setComponent(panel);
	}

	public void setResult(VoteResult result, int guessedNumber) {
		messageLabel.setText(result.getMessage());
		realValueLabel.setText(String.format("The Number: %d", result.getRealValue()));
		guessedValueLabel.setText(String.format("Your guess: %d", guessedNumber));
		scoreLabel.setText(String.format("Score: %d",result.getScore()));
	}
}

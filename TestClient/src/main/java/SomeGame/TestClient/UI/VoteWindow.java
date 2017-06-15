package SomeGame.TestClient.UI;

import java.util.Arrays;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.Window;

public class VoteWindow extends BasicWindow {

	int topMargin = 5;
	int leftMargin = 1;

	int width = 40;
	int height = 12;

	//TODO: The Values have to come from the static Game Model (Database or Local)
	private int minVote = 1;
	private int maxVote = 100;
	
	private TextBox voteInput;

	public VoteWindow(Runnable onVote) {
		
		setHints(Arrays.asList(Window.Hint.FIXED_POSITION));
		setPosition(new TerminalPosition(leftMargin, topMargin));
		setSize(new TerminalSize(width, height));

		Panel panel = new Panel();
		panel.setLayoutManager(new GridLayout(2));

		String msg = String.format("A Number between %d and %d.\nWhich one could it be?", minVote,	maxVote);
		Label label = new Label(msg);
		panel.addComponent(label);
		panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));

		voteInput = new TextBox();
		panel.addComponent(voteInput);

		Button voteButton = new Button("Guess", () -> {
			onVote.run();
			setFocusedInteractable(voteInput);
			voteInput.setText("");
		});
		panel.addComponent(voteButton);

		setComponent(panel);
	}

	public Integer getVote() {
		try {
			int vote = Integer.parseInt(voteInput.getText());
			if (vote < minVote || vote > maxVote)
				return null;
			return vote;
		} catch (NumberFormatException e) {
			return null;
		}
	}
}

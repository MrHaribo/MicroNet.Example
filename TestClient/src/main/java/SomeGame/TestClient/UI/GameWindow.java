package SomeGame.TestClient.UI;

import java.util.Arrays;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Window;

public class GameWindow extends BasicWindow {

	public GameWindow() {
		setHints(Arrays.asList(Window.Hint.FIXED_SIZE, Window.Hint.CENTERED));
		setSize(new TerminalSize(40, 10));

		Panel panel = new Panel();
		panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
		Label label = new Label("The Game Play Window");
		label.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

		panel.addComponent(label);

		setComponent(panel);
	}
	

}

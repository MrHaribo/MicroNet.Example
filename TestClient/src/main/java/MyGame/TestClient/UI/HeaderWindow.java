package MyGame.TestClient.UI;

import java.util.Arrays;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Window;

public class HeaderWindow extends BasicWindow {

	int topMargin = 1;
	int leftMargin = 1;
	int rightMargin = 3;
	int headerHeight = 0;

	public HeaderWindow(TerminalSize terminalSize) {

		setHints(Arrays.asList(Window.Hint.FIXED_POSITION, Window.Hint.FIXED_SIZE));
		setPosition(new TerminalPosition(leftMargin, topMargin));
		refreshLayout(terminalSize);
		
		Panel panel = new Panel();
		panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
		Label label = new Label("The Game");
		label.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
		
		panel.addComponent(label);
		
		setComponent(panel);
	}

	public void refreshLayout(TerminalSize terminalSize) {
		int width = terminalSize.getColumns() < leftMargin + rightMargin ? 0 : terminalSize.getColumns() - leftMargin - rightMargin;
		int height = headerHeight + topMargin;
		setSize(new TerminalSize(width, height));
	}
}

package SomeGame.TestClient.UI;

import java.util.Arrays;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.ActionListBox;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.Window;

public class ConsoleWindow extends BasicWindow {
	
	private ActionListBox actionListBox;

	private int terminalWidth = 40;
	private int terminalHeight = 10; 
	
	int horizontalMargin = 1;
	int verticalMargin = 1;
	
	public ConsoleWindow() {
		
		actionListBox = new ActionListBox();
		setComponent(actionListBox.withBorder(Borders.singleLine("Information")));
		
		setHints(Arrays.asList(Window.Hint.FIXED_POSITION, Window.Hint.FIXED_SIZE, Window.Hint.NO_DECORATIONS));
		setSize(new TerminalSize(terminalWidth, terminalHeight));
	}
	
	public void print(String msg) {
		actionListBox.addItem(msg, ()->{});
		
		actionListBox.setSelectedIndex(actionListBox.getItemCount() - verticalMargin);
	}

	public void refreshLayout(TerminalSize terminalSize) {
		setPosition(new TerminalPosition(horizontalMargin, terminalSize.getRows() - terminalHeight - verticalMargin));
	}
}

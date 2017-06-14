package SomeGame.TestClient.UI;

import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.gui2.table.TableModel;

import SomeGame.TestClient.Player;

public class GameInformationWindow extends BasicWindow {

	private int topMargin = 5;
	private int rightMargin = 1;
	private int botMargin = 1;
	private int width = 32;

	private Label roundTimeLabel;
	private Table<String> scoreTable;
	
	private long roundEndTime;
	private Timer roundUpdateTimer;

	public GameInformationWindow(TerminalSize terminalSize) {
		setHints(Arrays.asList(Window.Hint.FIXED_SIZE, Window.Hint.FIXED_POSITION, Window.Hint.NO_DECORATIONS));
		refreshLayout(terminalSize);

		Panel panel = new Panel();
		panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

		roundTimeLabel = new Label("Remaining: 0s");
		roundTimeLabel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));

		scoreTable = new Table<String>("Player", "Score");

		TableModel<String> tableModel = new TableModel<>("Player", "Score");

		scoreTable.setTableModel(tableModel);

		scoreTable.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));

		scoreTable.getTableModel().addRow("James bond", "45686");
		scoreTable.getTableModel().addRow("James bond", "45686");

		panel.addComponent(roundTimeLabel.withBorder(Borders.singleLine("Round Time")));
		panel.addComponent(scoreTable.withBorder(Borders.singleLine("Score")));

		setComponent(panel.withBorder(Borders.singleLine("Game Information")));

		startRoundTimeUpdate();
	}

	private void startRoundTimeUpdate() {
		roundUpdateTimer = new Timer();
		roundUpdateTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				long currentTimeMS = System.currentTimeMillis();
				long remainingTimeMS = roundEndTime < currentTimeMS ? 0 : roundEndTime - currentTimeMS;
				long timeInSecond = remainingTimeMS / 1000;
				roundTimeLabel.setText(String.format("Remaining: %ds", timeInSecond));
			}
		}, 0, 1);
	}

	public void setRoundTime(int roundTimeMS) {
		roundEndTime = System.currentTimeMillis() + roundTimeMS;
	}
	
	public void stopRoundUpdate() {
		roundUpdateTimer.cancel();
	}
	
	public void refreshPlayerScores(Player[] players) {
		
		TableModel<String> tableModel = new TableModel<>("Player", "Score");

		for (Player player : players) {
			tableModel.addRow(player.getName(), Integer.toString(player.getScore()));
		}

		scoreTable.setTableModel(tableModel);
	}

	public void refreshLayout(TerminalSize terminalSize) {
		int height = terminalSize.getRows() < topMargin + botMargin ? 0
				: terminalSize.getRows() - topMargin - botMargin;
		setSize(new TerminalSize(width, height));

		int left = terminalSize.getColumns() < width + rightMargin ? 0
				: terminalSize.getColumns() - width - rightMargin;
		setPosition(new TerminalPosition(left, topMargin));
	}

}

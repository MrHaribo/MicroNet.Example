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

	private Label timeDisplayLabel;
	private Table<String> scoreTable;
	
	private long timeDisplayEndTime;
	private Timer timeDisplayTimer;
	private String timeDisplayPrefixText = "Please Wait:";

	public GameInformationWindow(TerminalSize terminalSize) {
		setHints(Arrays.asList(Window.Hint.FIXED_SIZE, Window.Hint.FIXED_POSITION, Window.Hint.NO_DECORATIONS));
		refreshLayout(terminalSize);

		Panel panel = new Panel();
		panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

		timeDisplayLabel = new Label("Please wait for next round...");
		timeDisplayLabel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));

		scoreTable = new Table<String>("Player", "Score");

		TableModel<String> tableModel = new TableModel<>("Player", "Score");

		scoreTable.setTableModel(tableModel);

		scoreTable.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));

		panel.addComponent(timeDisplayLabel.withBorder(Borders.singleLine("Round Time")));
		panel.addComponent(scoreTable.withBorder(Borders.singleLine("Score")));

		setComponent(panel.withBorder(Borders.singleLine("Game Information")));

		startRoundTimeUpdate();
	}

	private void startRoundTimeUpdate() {
		timeDisplayTimer = new Timer();
		timeDisplayTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				long currentTimeMS = System.currentTimeMillis();
				long remainingTimeMS = timeDisplayEndTime < currentTimeMS ? 0 : timeDisplayEndTime - currentTimeMS;
				long timeInSecond = remainingTimeMS / 1000;
				timeDisplayLabel.setText(String.format("%s%ds", timeDisplayPrefixText, timeInSecond));
			}
		}, 0, 1);
	}

	public void setTimeDisplay(String prefixText, int durationMS) {
		timeDisplayPrefixText = prefixText;
		timeDisplayEndTime = System.currentTimeMillis() + durationMS;
	}
	
	public void stopRoundUpdate() {
		timeDisplayTimer.cancel();
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

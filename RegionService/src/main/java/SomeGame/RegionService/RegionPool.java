package SomeGame.RegionService;

import java.io.IOException;
import java.util.concurrent.Semaphore;

import micronet.network.IAdvisory;
import micronet.network.IAdvisory.QueueState;

public class RegionPool {
	private static final int FREE_INSTANCE_AMOUNT = 2;
	private static final int RESIZE_AMOUNT = 3;

	private int freeInstanceCount = 0;
	private int resizingCount = 0;
	private boolean isResizing = false;
	private Semaphore resizeLock = new Semaphore(1);

	public RegionPool(IAdvisory advisory) {
		advisory.registerQueueStateListener("mn://freeinstance", (QueueState state) -> {
			freeInstanceStatusChanged(state);
		});
		Refresh();
	}

	private void freeInstanceStatusChanged(QueueState state) {
		try {
			switch (state) {
			case OPEN:
				resizeLock.acquire();
				freeInstanceCount++;
				resizingCount--;
				if (resizingCount <= 0)
					isResizing = false;
				resizeLock.release();
				Refresh();
				break;
			case CLOSE:
				resizeLock.acquire();
				freeInstanceCount--;
				resizeLock.release();
				Refresh();
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void Refresh() {
		try {
			resizeLock.acquire();

			if (isResizing) {
				resizeLock.release();
				return;
			}

			if (freeInstanceCount < FREE_INSTANCE_AMOUNT) {
				isResizing = true;
				resizingCount = RESIZE_AMOUNT;
				for (int i = 0; i < RESIZE_AMOUNT; i++)
					startRegionProcess();
			}

			resizeLock.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void startRegionProcess() {
		try {
			Runtime.getRuntime().exec("./../Spacegame/Bin/Game.exe -server");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

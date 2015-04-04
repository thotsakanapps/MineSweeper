package com.thotsakan.minesweeper.gameview;

import android.graphics.Bitmap;

import com.thotsakan.minesweeper.R;

final class CellType {

	public static final int EXPLODED_MINE = -4;

	public static final int FLAGGED = -2;

	public static final int HIDDEN = -1;

	public static final int MINE = -3;

	private static Bitmap[] RESOURCE_ARRAY;

	private static final int[] RESOURCE_ID_ARRAY;

	private static final int RESOURCE_ID_ARRAY_SIZE;

	public static final int REVEALED = 0;

	static {
		RESOURCE_ID_ARRAY = new int[] { R.drawable.exploded_mine, R.drawable.mine, R.drawable.flag, R.drawable.hidden, R.drawable.revealed, R.drawable.one, R.drawable.two, R.drawable.three, R.drawable.four, R.drawable.five, R.drawable.six, R.drawable.seven, R.drawable.eight };
		RESOURCE_ID_ARRAY_SIZE = RESOURCE_ID_ARRAY.length;
		RESOURCE_ARRAY = new Bitmap[RESOURCE_ID_ARRAY_SIZE];
	}

	public static Bitmap getResource(int type) {
		int index = type - EXPLODED_MINE;
		if (-1 < index && index < RESOURCE_ID_ARRAY_SIZE) {
			return RESOURCE_ARRAY[index];
		} else {
			return null;
		}
	}

	public static int getResourceID(int type) {
		int index = type - EXPLODED_MINE;
		if (-1 < index && index < RESOURCE_ID_ARRAY_SIZE) {
			return RESOURCE_ID_ARRAY[index];
		} else {
			return 0;
		}
	}

	public static void setResource(int type, Bitmap bitmap) {
		int index = type - EXPLODED_MINE;
		if (-1 < index && index < RESOURCE_ID_ARRAY_SIZE) {
			RESOURCE_ARRAY[index] = bitmap;
		}
	}
}

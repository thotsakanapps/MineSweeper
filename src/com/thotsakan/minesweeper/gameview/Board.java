package com.thotsakan.minesweeper.gameview;

import java.util.Arrays;
import java.util.Random;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

final class Board {

	private int boardCols;

	private int boardRows;

	private int[][] cells;

	private boolean[][] mine;

	public int numFlags;

	public int numHidden;

	private int numMines;

	public Board(int boardRows, int boardCols, int numMines) {
		this.boardRows = boardRows;
		this.boardCols = boardCols;
		this.numMines = numMines;
		mine = new boolean[boardRows][boardCols];
		cells = new int[boardRows][boardCols];
		resetBoard();
	}

	private int countNeighbouringMines(int row, int col) {
		int numMines = 0;
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				int thisRow = row + i;
				int thisCol = col + j;
				if (-1 < thisRow && thisRow < boardRows && -1 < thisCol && thisCol < boardCols && mine[thisRow][thisCol]) {
					numMines++;
				}
			}
		}
		return numMines;
	}

	public void draw(Canvas canvas, Resources resources, int width, int height) {
		Paint paint = new Paint();
		for (int row = 0; row < boardRows; row++) {
			for (int col = 0; col < boardCols; col++) {
				Bitmap bitmap = CellType.getResource(cells[row][col]);
				if (bitmap == null) {
					bitmap = BitmapFactory.decodeResource(resources, CellType.getResourceID(cells[row][col]));
					CellType.setResource(cells[row][col], bitmap);
				}
				canvas.drawBitmap(bitmap, null, new Rect(col * width, row * height, (col * width) + width, (row * height) + height), paint);
			}
		}
	}

	public boolean hasWon() {
		return numHidden == numMines;
	}

	private void initCells() {
		for (int i = 0; i < boardRows; i++) {
			Arrays.fill(cells[i], CellType.HIDDEN);
		}
	}

	private void initMines() {
		for (int i = 0; i < boardRows; i++) {
			Arrays.fill(mine[i], false);
		}
	}

	public boolean isCellMine(int row, int col) {
		return mine[row][col];
	}

	private void putMines() {
		Random random = new Random(System.currentTimeMillis());
		for (int i = 0; i < numMines; i++) {
			boolean minePlaced = false;

			while (!minePlaced) {
				int row = random.nextInt(boardRows);
				int col = random.nextInt(boardCols);

				if (!mine[row][col]) {
					minePlaced = true;
					mine[row][col] = true;
				}
			}
		}
	}

	public void reArrangeMines(int row, int col) {
		initCells();
		for (int i = 0; i < boardRows; i++) {
			for (int j = 0; j < boardCols; j++) {
				if (!mine[i][j]) {
					mine[i][j] = true;
					mine[row][col] = false;
					return;
				}
			}
		}
	}

	public void resetBoard() {
		numFlags = 0;
		numHidden = boardRows * boardCols;
		initCells();
		initMines();
		putMines();
	}

	public boolean revealCell(int row, int col) {
		if (mine[row][col]) {
			showMines(row, col);
			return false;
		} else if (cells[row][col] == CellType.HIDDEN) {
			revealOthers(row, col);
			return true;
		}
		return true;
	}

	public void revealOthers(int row, int col) {
		if (cells[row][col] != CellType.HIDDEN || mine[row][col]) {
			return;
		}

		--numHidden;
		cells[row][col] = countNeighbouringMines(row, col);
		if (cells[row][col] == CellType.REVEALED) {
			for (int i = -1; i < 2; i++) {
				for (int j = -1; j < 2; j++) {
					int thisRow = row + i;
					int thisCol = col + j;
					if (-1 < thisRow && thisRow < boardRows && -1 < thisCol && thisCol < boardCols && cells[thisRow][thisCol] == CellType.HIDDEN) {
						revealOthers(thisRow, thisCol);
					}
				}
			}
		}
	}

	private void showMines(int row, int col) {
		for (int i = 0; i < boardRows; i++) {
			for (int j = 0; j < boardCols; j++) {
				if (mine[i][j]) {
					cells[i][j] = CellType.MINE;
				}
			}
		}
		cells[row][col] = CellType.EXPLODED_MINE;
	}

	public boolean toggleFlag(int row, int col) {
		if (cells[row][col] == CellType.HIDDEN) {
			cells[row][col] = CellType.FLAGGED;
			++numFlags;
			return true;
		} else if (cells[row][col] == CellType.FLAGGED) {
			cells[row][col] = CellType.HIDDEN;
			--numFlags;
			return true;
		}
		return false;
	}
}

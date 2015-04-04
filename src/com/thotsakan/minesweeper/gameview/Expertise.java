package com.thotsakan.minesweeper.gameview;

public enum Expertise {

	BEGINNER(8, 8, 10),

	EXPERT(30, 16, 99),

	INTERMEDIATE(16, 16, 40);

	public final int cols;

	public final int mines;

	public final int rows;

	private Expertise(int rows, int cols, int mines) {
		this.rows = rows;
		this.cols = cols;
		this.mines = mines;
	}
}

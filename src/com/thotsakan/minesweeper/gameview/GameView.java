package com.thotsakan.minesweeper.gameview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.preference.PreferenceManager;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.thotsakan.minesweeper.MainActivity;
import com.thotsakan.minesweeper.R;
import com.thotsakan.minesweeper.Timer;

public final class GameView extends View implements OnGestureListener, OnDoubleTapListener {

	private static final int CELL_SIZE = 80;

	private Board board;

	private Expertise expertise;

	private boolean gameOver;

	private GestureDetector gestureDetector;

	private boolean isFirstClick;

	private Paint paint;

	private Timer timer;

	public GameView(Context context, Expertise expertise, Timer timer) {
		super(context);
		this.expertise = expertise;
		this.timer = timer;
		board = new Board(expertise.rows, expertise.cols, expertise.mines);
		gestureDetector = new GestureDetector(getContext(), this);
		paint = new Paint();
		paint.setARGB(255, 0, 0, 0);
		paint.setAntiAlias(true);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(2);
		newGame();
	}

	private void flagCell(float x, float y) {
		int boardWidth = getWidth() / expertise.cols;
		int boardHeight = getHeight() / expertise.rows;
		int col = (int) (x / boardHeight);
		int row = (int) (y / boardWidth);
		if (-1 < row && row < expertise.rows && -1 < col && col < expertise.cols) {
			if (board.toggleFlag(row, col)) {
				updateUnflaggedMineCount();
				if (board.hasWon()) {
					gameOver = true;
					setNewGameButton(R.drawable.ic_cool);
					timer.stopTimer();
					saveScores();
				}
				invalidate();
			}
		}
	}

	public void newGame() {
		gameOver = false;
		isFirstClick = true;
		board.resetBoard();
		updateUnflaggedMineCount();
		setNewGameButton(R.drawable.ic_smile);
		timer.resetTimer();
		invalidate();
	}

	@Override
	public boolean onDoubleTap(MotionEvent event) {
		if (isFirstClick) {
			timer.startTimer();
		}
		flagCell(event.getX(), event.getY());
		isFirstClick = false;
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent event) {
		return false;
	}

	@Override
	public boolean onDown(MotionEvent event) {
		return false;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		board.draw(canvas, getResources(), CELL_SIZE, CELL_SIZE);
		for (int i = 0; i <= expertise.rows; i++) {
			int yCoord = i * CELL_SIZE;
			canvas.drawLine(0, yCoord, CELL_SIZE * expertise.cols, yCoord, paint);
		}
		for (int i = 0; i <= expertise.cols; i++) {
			int xCoord = i * CELL_SIZE;
			canvas.drawLine(xCoord, 0, xCoord, CELL_SIZE * expertise.rows, paint);
		}
		super.onDraw(canvas);
	}

	@Override
	public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent event) {
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int measuredWidth = MeasureSpec.makeMeasureSpec(CELL_SIZE * expertise.cols, MeasureSpec.AT_MOST);
		int measuredheight = MeasureSpec.makeMeasureSpec(CELL_SIZE * expertise.rows, MeasureSpec.AT_MOST);
		setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), measuredWidth), getDefaultSize(getSuggestedMinimumHeight(), measuredheight));
	}

	@Override
	public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent event) {
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent event) {
		if (isFirstClick) {
			timer.startTimer();
		}
		revealCell(event.getX(), event.getY());
		isFirstClick = false;
		return true;
	}

	@Override
	public boolean onSingleTapUp(MotionEvent event) {
		return false;
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gameOver) {
			return false;
		} else {
			gestureDetector.onTouchEvent(event);
			return true;
		}
	}

	private void revealCell(float x, float y) {
		int boardWidth = getWidth() / expertise.cols;
		int boardHeight = getHeight() / expertise.rows;
		int col = (int) (x / boardHeight);
		int row = (int) (y / boardWidth);
		if (0 <= row && row < expertise.rows && 0 <= col && col < expertise.cols) {
			if (!board.revealCell(row, col)) {
				if (isFirstClick) {
					isFirstClick = false;
					board.reArrangeMines(row, col);
					board.revealCell(row, col);
				} else {
					gameOver = true;
				}
			}
			if (gameOver) { // game lost
				setNewGameButton(R.drawable.ic_angry);
				timer.stopTimer();
			} else if (board.hasWon()) { // game won
				gameOver = true;
				setNewGameButton(R.drawable.ic_cool);
				timer.stopTimer();
				saveScores();
			}
			invalidate();
		}
	}

	private void saveScores() {
		long timeTaken = timer.elapsedTimeInSeconds();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		Resources resources = getResources();

		String key = "";
		switch (expertise) {
		case BEGINNER:
			key = resources.getString(R.string.best_scores_beginner);
			break;
		case INTERMEDIATE:
			key = resources.getString(R.string.best_scores_intermediate);
			break;
		case EXPERT:
			key = resources.getString(R.string.best_scores_expert);
			break;
		}

		long bestTime = Long.parseLong(prefs.getString(key, resources.getString(R.string.best_scores_default)));
		if (bestTime == -1 || bestTime > timeTaken) {
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString(key, Long.toString(timeTaken));
			editor.apply();
			Toast.makeText(getContext(), "Congratulations! This is the new best score.", Toast.LENGTH_LONG).show();
		}
	}

	private void setNewGameButton(int resourceId) {
		ImageButton newGameButton = (ImageButton) ((MainActivity) getContext()).findViewById(R.id.new_game_button);
		newGameButton.setImageResource(resourceId);
	}

	private void updateUnflaggedMineCount() {
		TextView unflaggedMineCount = (TextView) ((MainActivity) getContext()).findViewById(R.id.flagged_cells);
		unflaggedMineCount.setText("" + (expertise.mines - board.numFlags));
	}
}

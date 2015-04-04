package com.thotsakan.minesweeper;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.Chronometer;

public final class Timer extends Chronometer {

	public Timer(Context context) {
		super(context);
	}

	public Timer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public Timer(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public long elapsedTimeInSeconds() {
		return (SystemClock.elapsedRealtime() - getBase()) / 1000;
	}

	public void resetTimer() {
		stop();
		setBase(SystemClock.elapsedRealtime());
	}

	public void startTimer() {
		setBase(SystemClock.elapsedRealtime());
		start();
	}

	public void stopTimer() {
		stop();
	}
}

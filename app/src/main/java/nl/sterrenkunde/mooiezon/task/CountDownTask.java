package nl.sterrenkunde.mooiezon.task;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import nl.sterrenkunde.mooiezon.utils.task.RepeatableTask;

/**
 * Created by mint on 28-11-15.
 */
public class CountDownTask extends RepeatableTask {
    private static final String TAG = CountDownTask.class.getName();

    private CountDownTaskCallback _countDownTaskCallback;
    private String _dateFormat;
    private String _currentTime;
    private Handler _mainThreadHandler;

    public interface CountDownTaskCallback {
        void onCountDown(String time);
    }

    public CountDownTask(Context context, String currentTime, String dateFormat, CountDownTaskCallback countDownTaskCallback) {
        super(1000);
        _dateFormat = dateFormat;
        _currentTime = currentTime;
        _countDownTaskCallback = countDownTaskCallback;
        _mainThreadHandler = new Handler(context.getMainLooper());
    }

    protected void run() {
        String newTimeString = _currentTime;

        if (_currentTime != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(_dateFormat);
            try {
                Date date = simpleDateFormat.parse(_currentTime);
                long time = date.getTime() - 1000L;
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(time);
                newTimeString = simpleDateFormat.format(calendar.getTime());
            } catch (ParseException e) {
                Log.e(TAG, "Unable to parse time", e);
            }
        }

        if (newTimeString != null) {
            _currentTime = newTimeString;
            _mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    _countDownTaskCallback.onCountDown(_currentTime);
                }
            });
        } else {
            Log.e(TAG, "Unable to update time: no time value");
        }
    }
}

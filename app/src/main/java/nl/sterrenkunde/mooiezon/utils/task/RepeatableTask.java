package nl.sterrenkunde.mooiezon.utils.task;

import android.util.Log;

/**
 * Created by mint on 28-11-15.
 */
public abstract class RepeatableTask {

    private static final String TAG = RepeatableTask.class.getName();

    public enum Status {
        CONTINUE,
        ABORT,
        FINISHED
    }

    private static int _instanceCount = 0;

    private int _milliseconds;
    private Status _status;
    private int _cycle;

    public RepeatableTask(int milliseconds) {
        _milliseconds = milliseconds;
        new Thread(new Runnable() {
            @Override
            public void run() {
                _instanceCount ++;
                Log.d(TAG, "Running repeatable task cycle " + _cycle);
                _cycle = 0;
                _status = Status.CONTINUE;
                while (_status == Status.CONTINUE) {
                    try {
                        Thread.sleep(_milliseconds);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "Error in sleeping thread", e);
                    }

                    RepeatableTask.this.run();

                    _cycle++;
                }

                _status = Status.FINISHED;
                _instanceCount--;
            }
        }).start();
    }

    public void stop() {
        if (_status != Status.FINISHED) {
            _status = Status.ABORT;
        }
    }

    public int getCycle() {
        return _cycle;
    }

    public static int getInstanceCount() {
        return _instanceCount;
    }

    protected abstract void run();
}

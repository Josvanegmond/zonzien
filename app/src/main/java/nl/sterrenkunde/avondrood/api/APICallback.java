package nl.sterrenkunde.avondrood.api;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by mint on 28-11-15.
 */
public abstract class APICallback implements APIErrorCallback {
    private static final String TAG = APICallback.class.getName();

    public abstract void onFinished(JSONObject jsonAnswer);
    public void onError(String message) {
        Log.e(TAG, message);
    }
}

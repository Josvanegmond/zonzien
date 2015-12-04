package nl.sterrenkunde.zonno.api;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by mint on 28-11-15.
 */
public enum MZAPIClient {
    INSTANCE;

    private static String _serverUrlString = "http://zonno.sterrenkunde.nl/";

    private static final String TAG = MZAPIClient.class.getName();

    public enum Request {
        DUMMY,
        NEXT_EVENT
    }

    public void request(final Request request, String lon, String lat, final APICallback apiCallback) {
        new AsyncTask<String, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(String... params) {
                try {
                    String serverAnswer;
                    String lon = params[0];
                    String lat = params[1];
                    switch (request) {
                        case NEXT_EVENT:
                            serverAnswer = _postRequest(apiCallback, "request=nextEvent", "lon="+lon, "lat="+lat);
                            break;
                        case DUMMY:
                        default:
                            serverAnswer = _postRequest(apiCallback, "request=dummy");
                    }
                    return new JSONObject(serverAnswer);
                } catch (Exception exception) {
                    try {
                        return new JSONObject().put("error", exception.getMessage());
                    } catch (JSONException jsonException) {
                        Log.e(TAG, "Failed to convert json answer in-execute", jsonException);
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject answer) {
                if (answer != null) {
                    try {
                        if (answer.has("error")) {
                            apiCallback.onError(answer.get("error").toString());
                        } else {
                            apiCallback.onFinished(answer);
                        }
                    } catch (JSONException jsonException) {
                        Log.e(TAG, "Failed to convert server json answer post-execute", jsonException);
                    }
                }
            }
        }.execute(lon, lat);
    }


    private String _postRequest(APIErrorCallback apiCallback, String... urlAppends) {

        String urlString = _serverUrlString;
        if (urlAppends.length > 0) {
            urlString += "?";
        }
        for (int index = 0; index < urlAppends.length; index++) {
            urlString += urlAppends[index];
            if (index < urlAppends.length - 1) {
                urlString += "&";
            }
        }

        Log.d(TAG, "Making request" + urlString);

        HttpURLConnection urlConnection = null;
        String serverAnswer = "";
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            int urlConnectionResponseCode = urlConnection.getResponseCode();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line = in.readLine();
                while (line != null) {
                    serverAnswer += line;
                    line = in.readLine();
                }
            } else {
                String errorMessage = "Server responds with NOT OK (Response code " + urlConnectionResponseCode + ")";
                apiCallback.onError(errorMessage);
            }
        } catch (IOException e) {
            apiCallback.onError(e.getMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return serverAnswer;
    }
}

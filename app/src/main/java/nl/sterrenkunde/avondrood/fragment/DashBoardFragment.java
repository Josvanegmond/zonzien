package nl.sterrenkunde.avondrood.fragment;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import org.json.JSONArray;
import org.json.JSONObject;

import nl.sterrenkunde.avondrood.R;
import nl.sterrenkunde.avondrood.api.APICallback;
import nl.sterrenkunde.avondrood.api.MZAPIClient;
import nl.sterrenkunde.avondrood.task.CountDownTask;
import nl.sterrenkunde.avondrood.widget.ImageTableLayout;
import nl.sterrenkunde.avondrood.widget.TextView;

/**
 * Created by mint on 1-12-15.
 */
public class DashBoardFragment extends Fragment {

    private static final String TAG = DashBoardFragment.class.getName();

    private CountDownTask _countdownTask;

    private TextView _untilNextTimeText;
    private TextView _blueHourTimeText;
    private TextView _goldenHourTimeText;
    private ImageButton _fabRefreshButton;
    private ImageTableLayout _colorTableLayout;

    private String _etaTimeString;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dashboard_fragment, container, false);

        _untilNextTimeText = (TextView)view.findViewById(R.id.untilNextTimeText);
        _blueHourTimeText = (TextView) view.findViewById(R.id.blueHourTimeText);
        _goldenHourTimeText = (TextView) view.findViewById(R.id.goldenHourTimeText);
        _fabRefreshButton = (ImageButton) view.findViewById(R.id.fabRefreshButton);
        _colorTableLayout = (ImageTableLayout) view.findViewById(R.id.colorTable);

        _fabRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _syncData();
            }
        });

        _createCountDownTask();
        _syncData();

        return view;
    }

    private void _createCountDownTask() {
        if (_countdownTask != null) {
            _countdownTask.stop();
        }
        _countdownTask = new CountDownTask(getContext(), _etaTimeString, "dd:mm:ss", new CountDownTask.CountDownTaskCallback() {
            @Override
            public void onCountDown(String time) {
                _etaTimeString = time;
                if (_untilNextTimeText != null) {
                    _untilNextTimeText.setText(time);
                }
            }
        });
    }

    private Drawable getCloud(String type, Integer color) {
        Log.d(TAG, "clr: " + color);
        Drawable cloudDrawable = null;
        if ("broken".equals(type)) {
            cloudDrawable = ContextCompat.getDrawable(getContext(), R.drawable.broken);
        } else if ("fluffy".equals(type)) {
            cloudDrawable = ContextCompat.getDrawable(getContext(), R.drawable.fluffy);
        } else if ("deck".equals(type)) {
            cloudDrawable = ContextCompat.getDrawable(getContext(), R.drawable.deck);
        }

        if(cloudDrawable != null && color != null) {
            DrawableCompat.setTintMode(cloudDrawable, PorterDuff.Mode.SRC_IN);
            DrawableCompat.setTint(cloudDrawable, color);
        }

        return cloudDrawable;
    }

    private void _syncData() {
        _untilNextTimeText.setText(getString(R.string.loadingData));
        /* Create Animation */
        Animation rotation = AnimationUtils.loadAnimation(getContext(), R.anim.button_rotate);
        rotation.setRepeatCount(Animation.INFINITE);

        /* start Animation */
        _fabRefreshButton.startAnimation(rotation);

        MZAPIClient.INSTANCE.request(MZAPIClient.Request.NEXT_EVENT, new APICallback() {
            @Override
            public void onFinished(JSONObject jsonAnswer) {
                try {
                    JSONObject dataObject = jsonAnswer.getJSONObject("data");
                    String eta = dataObject.getString("eta");
                    String blueHourStart = dataObject.getString("blue_hour_start");
                    String blueHourEnd = dataObject.getString("blue_hour_end");
                    String goldenHourStart = dataObject.getString("golden_hour_start");
                    String goldenHourEnd = dataObject.getString("golden_hour_end");

                    JSONArray frames = dataObject.getJSONArray("frames");
                    for (int frameCount = 0; frameCount < frames.length(); frameCount++) {
                        JSONObject frame = frames.getJSONObject(frameCount);

                        JSONObject high = frame.getJSONObject("high");
                        Log.d(TAG, "clr: " + high.getString("cloud_color"));
                        _colorTableLayout.setColor(frameCount, 0, Color.parseColor(high.getString("color")));
                        _colorTableLayout.setImage(frameCount, 0, getCloud(high.getString("cloud_type"), Color.parseColor(high.getString("cloud_color").toUpperCase())));

                        JSONObject low = frame.getJSONObject("low");
                        Log.d(TAG, "clr: " + low.getString("cloud_color"));
                        _colorTableLayout.setColor(frameCount, 1, Color.parseColor(low.getString("color")));
                        _colorTableLayout.setImage(frameCount,1, getCloud(low.getString("cloud_type"), Color.parseColor(low.getString("cloud_color").toUpperCase())));

                        JSONObject horizon = frame.getJSONObject("horizon");
                        Log.d(TAG, "clr: " + horizon.getString("cloud_color"));
                        _colorTableLayout.setColor(frameCount, 2, Color.parseColor(horizon.getString("color")));
                        _colorTableLayout.setImage(frameCount, 2, getCloud(horizon.getString("cloud_type"), Color.parseColor(horizon.getString("cloud_color").toUpperCase())));
                    }

                    _colorTableLayout.update();

                    _untilNextTimeText.setText(eta);
                    _etaTimeString = eta;
                    _createCountDownTask();

                    _blueHourTimeText.setText(getString(R.string.blueHourText, blueHourStart, blueHourEnd));
                    _goldenHourTimeText.setText(getString(R.string.goldenHourText, goldenHourStart, goldenHourEnd));

                } catch (Exception e) {
                    _untilNextTimeText.setText(getString(R.string.noDataAvailable));
                    Log.e(TAG, "Error reading json object", e);
                } finally {
                    _fabRefreshButton.clearAnimation();
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error: " + error);
                _untilNextTimeText.setText(getString(R.string.errorRetrievingData));
                _fabRefreshButton.clearAnimation();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        _countdownTask.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        _countdownTask.stop();
        _countdownTask = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        _createCountDownTask();
        _syncData();
    }

}

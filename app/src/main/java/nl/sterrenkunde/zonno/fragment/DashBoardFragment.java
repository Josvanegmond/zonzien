package nl.sterrenkunde.zonno.fragment;

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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import nl.sterrenkunde.zonno.R;
import nl.sterrenkunde.zonno.api.APICallback;
import nl.sterrenkunde.zonno.api.MZAPIClient;
import nl.sterrenkunde.zonno.task.CountDownTask;
import nl.sterrenkunde.zonno.widget.ImageTableLayout;
import nl.sterrenkunde.zonno.widget.TextView;

/**
 * Created by mint on 1-12-15.
 */
public class DashBoardFragment extends Fragment {

    private static final String TAG = DashBoardFragment.class.getName();

    private CountDownTask _countdownTask;

    private TextView _untilNextTimeText;
    private TextView _blueHourTimeText;
    private TextView _goldenHourTimeText;
    private TextView _sunsetStartText;
    private TextView _sunsetEndText;
    private TextView _sunsetStart;
    private TextView _sunsetEnd;
    private ImageButton _fabRefreshButton;
    private ImageTableLayout _colorTableLayout;
    private Button _uploadPhoto;

    private String _etaTimeString;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dashboard_fragment, container, false);

        _untilNextTimeText = (TextView)view.findViewById(R.id.untilNextTimeText);
        _blueHourTimeText = (TextView) view.findViewById(R.id.blueHourTimeText);
        _goldenHourTimeText = (TextView) view.findViewById(R.id.goldenHourTimeText);
        _sunsetStartText = (TextView) view.findViewById(R.id.sunsetStartText);
        _sunsetEndText = (TextView) view.findViewById(R.id.sunsetEndText);
        _sunsetStart = (TextView) view.findViewById(R.id.sunsetStart);
        _sunsetEnd = (TextView) view.findViewById(R.id.sunsetEnd);
        _fabRefreshButton = (ImageButton) view.findViewById(R.id.fabRefreshButton);
        _colorTableLayout = (ImageTableLayout) view.findViewById(R.id.colorTable);
        _uploadPhoto = (Button) view.findViewById(R.id.uploadPhoto);

        _uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DashBoardFragment.this.getContext(), "Bekijk de pro versie voor deze feature!", Toast.LENGTH_SHORT).show();
            }
        });

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
        Drawable cloudDrawable = null;
        if ("broken".equals(type)) {
            cloudDrawable = ContextCompat.getDrawable(getContext(), R.drawable.cloud);
        } else if ("fluffy".equals(type)) {
            cloudDrawable = ContextCompat.getDrawable(getContext(), R.drawable.cloud);
        } else if ("deck".equals(type)) {
            cloudDrawable = ContextCompat.getDrawable(getContext(), R.drawable.cloud);
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

        MZAPIClient.INSTANCE.request(MZAPIClient.Request.NEXT_EVENT, "51-58", "4-56", new APICallback() {
            @Override
            public void onFinished(JSONObject jsonAnswer) {
                try {
                    JSONObject dataObject = jsonAnswer.getJSONObject("data");
                    String eta = dataObject.getString("eta");
                    String blueHour = dataObject.getString("blue_hour");
                    String sunsetStart = dataObject.getString("sunset_start");
                    String goldenHour = dataObject.getString("golden_hour");

                    JSONArray frames = dataObject.getJSONArray("frames");
                    for (int frameCount = 0; frameCount < frames.length(); frameCount++) {
                        JSONObject frame = frames.getJSONObject(frameCount);

                        JSONObject high = frame.getJSONObject("high");
                        _colorTableLayout.setColor(frameCount, 0, Color.parseColor(high.getString("color")));
                        _colorTableLayout.setImage(frameCount, 0, getCloud(high.getString("cloud_type"), Color.parseColor(high.getString("cloud_color").toUpperCase())));

                        JSONObject low = frame.getJSONObject("low");
                        _colorTableLayout.setColor(frameCount, 1, Color.parseColor(low.getString("color")));
                        _colorTableLayout.setImage(frameCount, 1, getCloud(low.getString("cloud_type"), Color.parseColor(low.getString("cloud_color").toUpperCase())));

                        JSONObject horizon = frame.getJSONObject("horizon");
                        _colorTableLayout.setColor(frameCount, 2, Color.parseColor(horizon.getString("color")));
                        _colorTableLayout.setImage(frameCount, 2, getCloud(horizon.getString("cloud_type"), Color.parseColor(horizon.getString("cloud_color").toUpperCase())));
                    }

                    _colorTableLayout.update();

                    _untilNextTimeText.setText(eta);
                    _etaTimeString = eta;
                    _createCountDownTask();

                    _sunsetStart.setText(sunsetStart);
                    _sunsetEnd.setText(blueHour);

                    _blueHourTimeText.setText(blueHour);
                    _sunsetStartText.setText(sunsetStart);
                    _goldenHourTimeText.setText(goldenHour);
                    _sunsetEndText.setText(blueHour);

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

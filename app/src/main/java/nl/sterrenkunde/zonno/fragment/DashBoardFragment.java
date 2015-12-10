package nl.sterrenkunde.zonno.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
    private TextView _untilNextEventText;
    private TextView _blueHourTimeText;
    private TextView _goldenHourTimeText;
    private TextView _sunsetStartText;
    private TextView _sunsetMiddle;
    private TextView _sunsetStart;
    private TextView _sunsetEnd;
    private TextView _dateText;
    private TextView _disclaimerText;
    private ImageButton _fabRefreshButton;
    private ImageTableLayout _colorTableLayout;

    private String _etaTimeString;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.dashboard_fragment, container, false);

        _untilNextTimeText = (TextView) view.findViewById(R.id.untilNextTimeText);
        _untilNextEventText = (TextView) view.findViewById(R.id.untilNextEventText);
        _blueHourTimeText = (TextView) view.findViewById(R.id.blueHourTimeText);
        _goldenHourTimeText = (TextView) view.findViewById(R.id.goldenHourTimeText);
        _sunsetStartText = (TextView) view.findViewById(R.id.sunsetStartText);
        _sunsetStart = (TextView) view.findViewById(R.id.goldenHourStart);
        _sunsetMiddle = (TextView) view.findViewById(R.id.sunsetStart);
        _sunsetEnd = (TextView) view.findViewById(R.id.sunsetEnd);
        _fabRefreshButton = (ImageButton) view.findViewById(R.id.fabRefreshButton);
        _colorTableLayout = (ImageTableLayout) view.findViewById(R.id.colorTable);
        _dateText = (TextView) view.findViewById(R.id.dateText);
        _disclaimerText = (TextView) view.findViewById(R.id.disclaimerText);
        _disclaimerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogFragment() {
                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState) {
                        // Use the Builder class for convenient dialog construction
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(R.string.disclaimer)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                        // Create the AlertDialog object and return it
                        return builder.create();
                    }
                }.show(getActivity().getFragmentManager(), "disclaimer");
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
        _countdownTask = new CountDownTask(getContext(), _etaTimeString, "HH:mm:ss", new CountDownTask.CountDownTaskCallback() {
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
        int randomNumber = (int) (Math.random() * 2);
        if ("broken".equals(type)) {
            if (randomNumber == 0) {
                cloudDrawable = ContextCompat.getDrawable(getContext(), R.drawable.cloud);
            } else {
                cloudDrawable = ContextCompat.getDrawable(getContext(), R.drawable.cloud);
            }
        } else if ("fluffy".equals(type)) {
            if (randomNumber == 0) {
                cloudDrawable = ContextCompat.getDrawable(getContext(), R.drawable.cloud);
            } else {
                cloudDrawable = ContextCompat.getDrawable(getContext(), R.drawable.cloud);
            }
        } else if ("deck".equals(type)) {
            if (randomNumber == 0) {
                cloudDrawable = ContextCompat.getDrawable(getContext(), R.drawable.cloud);
            } else {
                cloudDrawable = ContextCompat.getDrawable(getContext(), R.drawable.cloud);
            }
        }

        if (cloudDrawable != null && color != null) {
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
                    String event = jsonAnswer.getString("event");
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

                    _untilNextEventText.setText(getString(R.string.untilNextEventText, _getEventName(event)));

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("cccc d MMMM", new Locale("nl", "NL"));
                    _dateText.setText(simpleDateFormat.format(new Date(System.currentTimeMillis())));

                    _sunsetStart.setText(goldenHour);
                    _sunsetMiddle.setText(sunsetStart);
                    _sunsetEnd.setText(blueHour);

                    _goldenHourTimeText.setText(goldenHour);
                    _sunsetStartText.setText(sunsetStart);
                    _blueHourTimeText.setText(blueHour);

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

    private String _getEventName(String event) {
        if ("sunrise".equals(event)) {
            return getResources().getStringArray(R.array.eventNameText)[0];
        } else if ("sunset".equals(event)) {
            return getResources().getStringArray(R.array.eventNameText)[1];
        } else {
            return "?";
        }
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

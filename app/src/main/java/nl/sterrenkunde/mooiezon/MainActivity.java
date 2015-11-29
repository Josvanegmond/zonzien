package nl.sterrenkunde.mooiezon;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import org.json.JSONException;
import org.json.JSONObject;

import nl.sterrenkunde.mooiezon.api.APICallback;
import nl.sterrenkunde.mooiezon.api.MZAPIClient;
import nl.sterrenkunde.mooiezon.task.CountDownTask;
import nl.sterrenkunde.mooiezon.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getName();

    private CountDownTask _countdownTask;

    private String _serverUrlString = "http://www.joozey.nl/mooiezon/index.php";

    private TextView _untilNextTimeText;
    private TextView _blueHourTimeText;
    private TextView _goldenHourTimeText;
    private ImageButton _fabRefreshButton;

    private MZAPIClient _mzApiClient;

    private String _etaTimeString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        _untilNextTimeText = (TextView) findViewById(R.id.untilNextTimeText);
        _blueHourTimeText = (TextView) findViewById(R.id.blueHourTimeText);
        _goldenHourTimeText = (TextView) findViewById(R.id.goldenHourTimeText);
        _fabRefreshButton = (ImageButton) findViewById(R.id.fabRefreshButton);

        _fabRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _syncData();
            }
        });

        _createCountDownTask();

        _mzApiClient = new MZAPIClient(_serverUrlString);
        _syncData();
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

    private void _createCountDownTask() {
        if(_countdownTask != null) {
            _countdownTask.stop();
        }
        _countdownTask = new CountDownTask(getApplicationContext(), _etaTimeString, "dd:mm:ss", new CountDownTask.CountDownTaskCallback() {
            @Override
            public void onCountDown(String time) {
                _etaTimeString = time;
                if (_untilNextTimeText != null) {
                    _untilNextTimeText.setText(time);
                }
            }
        });
    }

    private void _syncData() {
        _untilNextTimeText.setText(getString(R.string.loadingData));
        /* Create Animation */
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.button_rotate);
        rotation.setRepeatCount(Animation.INFINITE);

        /* start Animation */
        _fabRefreshButton.startAnimation(rotation);

        _mzApiClient.request(MZAPIClient.Request.NEXT_EVENT, new APICallback() {
            @Override
            public void onFinished(JSONObject jsonAnswer) {
                try {
                    JSONObject dataObject = jsonAnswer.getJSONObject("data");
                    String eta = dataObject.getString("eta");
                    String blueHourStart = dataObject.getString("blue_hour_start");
                    String blueHourEnd = dataObject.getString("blue_hour_end");
                    String goldenHourStart = dataObject.getString("golden_hour_start");
                    String goldenHourEnd = dataObject.getString("golden_hour_end");

                    _untilNextTimeText.setText(eta);
                    _etaTimeString = eta;
                    _createCountDownTask();

                    _blueHourTimeText.setText(getString(R.string.blueHourText, blueHourStart, blueHourEnd));
                    _goldenHourTimeText.setText(getString(R.string.goldenHourText, goldenHourStart, goldenHourEnd));

                } catch (JSONException e) {
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

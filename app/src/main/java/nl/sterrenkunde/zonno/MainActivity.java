package nl.sterrenkunde.zonno;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import nl.sterrenkunde.zonno.fragment.CreditsFragment;
import nl.sterrenkunde.zonno.fragment.DashBoardFragment;
import nl.sterrenkunde.zonno.fragment.ProFragment;
import nl.sterrenkunde.zonno.fragment.TutorialFragment;
import nl.sterrenkunde.zonno.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getName();

    private Fragment _dashboardFragment;
    private Fragment _tutorialFragment;
    private Fragment _proVersionFragment;
    private Fragment _creditsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) drawer.findViewById(R.id.nav_view);
        View navHeaderMain = navigationView.inflateHeaderView(R.layout.nav_header_main);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        _dashboardFragment = new DashBoardFragment();
        _tutorialFragment = new TutorialFragment();
        _proVersionFragment = new ProFragment();
        _creditsFragment = new CreditsFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.contentContainer, _dashboardFragment).commit();

        TextView versionText = (TextView) navHeaderMain.findViewById(R.id.versionText);
        versionText.setText("Version: " + Config.version);
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

        if (id == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.contentContainer, _dashboardFragment).commit();
        } else if (id == R.id.nav_tutorial) {
            getSupportFragmentManager().beginTransaction().replace(R.id.contentContainer, _tutorialFragment).commit();
        } else if (id == R.id.nav_pro) {
            getSupportFragmentManager().beginTransaction().replace(R.id.contentContainer, _proVersionFragment).commit();
        } else if (id == R.id.nav_credits) {
            getSupportFragmentManager().beginTransaction().replace(R.id.contentContainer, _creditsFragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

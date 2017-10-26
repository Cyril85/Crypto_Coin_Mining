package com.thankachan.cyril.crypto_coin_mining;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.builders.Actions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private InterstitialAd interstitial;


    private RewardedVideoAd mAd;
    Button button3;
    Button button2;
    FloatingActionButton floatingActionButton;
    ProgressBar pBar;
    int pStatus = 0;
    private Handler handler = new Handler();
    int snoBalance;
    SharedPreferences prefs;
    TextView snoBalTxt;
    int pBarProgress;
    TextView countDownTv;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        snoBalance = prefs.getInt("snoBalance", 0);

        snoBalTxt = (TextView) findViewById(R.id.snoCoin);

        snoBalTxt.setText(getString(R.string.avail_sno_coin) + " " + snoBalance);

        countDownTv = (TextView) findViewById(R.id.countDown);

        button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Main2Activity.this, cartActivity.class));

            }
        });
        button2 = (Button) findViewById(R.id.button2);
        pBar = (ProgressBar) findViewById(R.id.progressBar1);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Main2Activity.this, buyActivity.class));

            }
        });

        pBarProgress = prefs.getInt("pBarProgress", 0);
        if (pBarProgress > 0) {
            pStatus = pBarProgress;
            handleProgressBar();
        }

        button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                handleProgressBar();

            }

        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.sell:
                        Intent intent = new Intent(Main2Activity.this, SellActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.claim:
                        intent = new Intent(Main2Activity.this, Main2Activity.class);
                        startActivity(intent);
                        break;
                    case R.id.cart:
                        intent = new Intent(Main2Activity.this, buyActivity.class);
                        startActivity(intent);
                        break;

                }
                return false;
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        pBar = (ProgressBar) findViewById(R.id.progressBar1);
    }

    public void handleProgressBar() {

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (pStatus <= 100) {

                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            pBar.setProgress(pStatus);
                            //pBar.setSecondaryProgress(pStatus + 5);
                            countDownTv.setText(pStatus + "%");
                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        // Just to display the progress slowly
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    pStatus++;
                }
            }
        }).start();
        AdRequest adRequest = new AdRequest.Builder().build();

        // Prepare the Interstitial Ad
        interstitial = new InterstitialAd(Main2Activity.this);
// Insert the Ad Unit ID
        interstitial.setAdUnitId(getString(R.string.admob_interstitial_id));

        interstitial.loadAd(adRequest);
// Prepare an Interstitial Ad Listener
        interstitial.setAdListener(new AdListener() {
            public void onAdLoaded() {
// Call displayInterstitial() function
                displayInterstitial();
            }
        });

        // Do what you need to do..
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "MM/dd/yyyy hh:mm:ss aa");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        button2.setText("Wait for Claim");
        button2.setBackgroundColor(getResources().getColor(R.color.darkerprimary));
        // Set the button not-clickable..
        button2.setEnabled(false);

        // Then re-enable it after 5 seconds..
        final Runnable enableButton = new Runnable() {
            @Override
            public void run() {
                button2.setEnabled(true);
                snoBalance = snoBalance + 10;
                PreferenceManager.getDefaultSharedPreferences(Main2Activity.this).edit().putInt("snoBalance", snoBalance).apply();
                pStatus = 0;

                FirebaseUser user = firebaseAuth.getCurrentUser();

                mDatabaseReference.child(user.getUid()).child("balance").setValue(snoBalance);
                recreate();

            }
        };


        new Handler().postDelayed(enableButton, 200000);
    }

    @Override
    protected void onResume() {
        super.onResume();

        snoBalance = prefs.getInt("snoBalance", 0);
        snoBalTxt.setText(getString(R.string.avail_sno_coin) + " " + snoBalance);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(Main2Activity.this).edit().putInt("pBarProgress", pStatus).apply();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);

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
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent = new Intent(Main2Activity.this, profileActivity.class);
            startActivity(intent);

            // Handle the camera action
        } else if (id == R.id.nav_gallery) {


        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        return Actions.newView("Main2", "http://[ENTER-YOUR-URL-HERE]");
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        FirebaseUserActions.getInstance().start(getIndexApiAction());
    }

    @Override
    public void onStop() {

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        FirebaseUserActions.getInstance().end(getIndexApiAction());
        super.onStop();
    }


    public void displayInterstitial() {
// If Ads are loaded, show Interstitial else show nothing.
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
    }


}




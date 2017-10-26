package milanroxe.inc.snocoins;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.javiersantos.appupdater.AppUpdater;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.builders.Actions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import cn.pedant.SweetAlert.SweetAlertDialog;
import milanroxe.inc.snocoins.bitcoin.BitcoinActivity;
import milanroxe.inc.snocoins.withdraw.ExchangeAlert;


public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private InterstitialAd interstitial;

    private AlertDialog alertDialog;
    private RewardedVideoAd mAd;
    Button button3;
    Button button2;
    FloatingActionButton fabshare;

   WebView wv;


    ProgressBar pBar;
    int pStatus = 0;
    private Handler handler = new Handler();
    int snoBalance;
    SharedPreferences prefs;
    TextView snoBalTxt;
    FloatingActionButton floatingActionButton;

    int pBarProgress;
    TextView countDownTv;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth firebaseAuth;
    private static final int WITHDRAW_LIMIT = 41500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        AppUpdater appUpdater = new AppUpdater(this);
        appUpdater.start();





        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();


        //snoBalance = prefs.getInt("snoBalance",  0);
        snoBalance = prefs.getInt("snoBalance", 0);

        //Toast.makeText(getApplicationContext(), String.valueOf(snoBalance), Toast.LENGTH_SHORT).show();

        snoBalTxt = (TextView) findViewById(R.id.snoCoin);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        snoBalTxt.setText(getString(R.string.avail_sno_coin) + " " + snoBalance);


        countDownTv = (TextView) findViewById(R.id.countDown);




        DatabaseReference mref = mDatabaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("balance");
        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    snoBalTxt.setText(getString(R.string.avail_sno_coin) + " " + dataSnapshot.getValue().toString());
                    //Toast.makeText(getApplicationContext(), dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        fabshare=(FloatingActionButton)findViewById(R.id.fabShare) ;
        fabshare.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main2Activity.this, AlertUnlockFreeRidesActivity.class);
                startActivity(intent);

            }
        }));


        button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //shows alert dialogue
                showDialog(v);

            }
        });


        button2 = (Button) findViewById(R.id.button2);
        pBar = (ProgressBar) findViewById(R.id.progressBar1);


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
                    case R.id.bitcoin:
                        Intent intent = new Intent(Main2Activity.this, BitcoinActivity.class);
                        startActivity(intent);
                        return true;

                    case R.id.claim:
                        intent = new Intent(Main2Activity.this, Main2Activity.class);
                        onPause();


                        return true;
                    case R.id.cart:
                        intent = new Intent(Main2Activity.this, cartActivity.class);
                        startActivity(intent);
                        return true;

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
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //FAB fab = new FAB();
                // fab.show(getFragmentManager(), "Level 2 Timer");

                SweetAlertDialog pDialog = new SweetAlertDialog(Main2Activity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("LEVEL 2 TIMER");
                pDialog.setCustomImage(R.drawable.millis);
                pDialog.setContentText("Timer will Started When You Earn At Least 1 Bitcoin");
                pDialog.setCancelable(false);
                pDialog.show();

            }
        });


        Toolbar t = (Toolbar) findViewById(R.id.sno_bal_toolbar);
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExchangeAlert exchangeAlert = new ExchangeAlert();
                exchangeAlert.show(getFragmentManager(), "Exchange");

            }
        });
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
                        Thread.sleep(9000);
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

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
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
                snoBalance = (snoBalance + 250);
                PreferenceManager.getDefaultSharedPreferences(Main2Activity.this).edit().putInt("snoBalance", snoBalance).apply();
                pStatus = 0;

                FirebaseUser user = firebaseAuth.getCurrentUser();

                mDatabaseReference.child(user.getUid()).child("balance").setValue(snoBalance);
                recreate();

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(Main2Activity.this);
                mBuilder.setSmallIcon(R.drawable.ic_snocoins);
                mBuilder.setColor(R.color.colorPrimary);
                mBuilder.setContentTitle("CLAIM YOUR SNO COINS");
                mBuilder.setContentText("250COINS!");


                Intent i = new Intent(Main2Activity.this.getApplicationContext(), Main2Activity.class);

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());

                stackBuilder.addParentStack(Main2Activity.class);

                stackBuilder.addNextIntent(i);

                PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);


                mBuilder.setContentIntent(pendingIntent);


                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                mBuilder.setSound(alarmSound);
                mBuilder.setVibrate(new long[]{5000, 5000});


                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                nm.notify(1, mBuilder.build());

            }

        };


        new Handler().postDelayed(enableButton, 900000);
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else

            new AlertDialog.Builder(this)
                    .setTitle("Sno Coins")
                    .setIcon(R.drawable.mils)
                    .setMessage("Are you sure you want to exit Sno Coins?")
                    .setCancelable(false)

                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Main2Activity.this.finish();
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)

                    .show();
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
            // Toast.makeText(getApplicationContext(),"Clicked on history",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Main2Activity.this, tabActivity.class);
            startActivity(intent);


        } else if (id == R.id.bit) {
            // Toast.makeText(getApplicationContext(),"Clicked on history",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Main2Activity.this, BitcoinActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(Main2Activity.this, AlertUnlockFreeRidesActivity .class);
            startActivity(intent);

        }
        else if (id == R.id.aboutus) {
            Intent intent = new Intent(Main2Activity.this, WebActivity.class);
            startActivity(intent);


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
    public void showDialog(View v){

        final CharSequence items[]={"BTC"};
        alertDialog = new AlertDialog.Builder(Main2Activity.this).create();
        alertDialog.setTitle("Pick a Currency");
        alertDialog.setMessage("Pick a currecy that you'd like to with draw the payment to BTC." +
                "Minimum withdraw is 41500 SNO Coins");

        // Alert dialog button


        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,"CANCEL",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                }
        );
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "PICK",

                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        // Alert dialog action goes here
                        // onClick button code here
                        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
                        firebaseAuth = FirebaseAuth.getInstance();
                        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        int snoBalance = prefs.getInt("snoBalance", 0);
                        int mycoin = prefs.getInt("mysnobalance", 0);
                        if (snoBalance != 0) {

                            if(snoBalance == WITHDRAW_LIMIT)
                            {
                                alerts(" Can't Withdraw. Please claim more coins ");
                            }
                            if (snoBalance < WITHDRAW_LIMIT) {
                                ///if user tries to wthdraw higher sno coin
                                // Toast.makeText(getApplicationContext(), "You can't withdraw a higher amount than you have", Toast.LENGTH_SHORT).show();
                                alerts("You can't withdraw a higher amount than you have");
                                return;
                            }
                            else if(snoBalance > WITHDRAW_LIMIT && (snoBalance-WITHDRAW_LIMIT)>0) {
                                mycoin = mycoin - WITHDRAW_LIMIT;
                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putInt("mysnocoins", mycoin).apply();
                                //All criteria passed do what you want to
                                snoBalance = snoBalance - WITHDRAW_LIMIT;
                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putInt("snoBalance", snoBalance).apply();
                                snoBalance = prefs.getInt("snoBalance", 0);
                                // snoBalTxt.setText(getString(R.string.avail_sno_coin) + " " + snoBalance);

                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                mDatabaseReference.child(user.getUid()).child("balance").setValue(snoBalance);



                                String date = DateFormat.getDateTimeInstance().format(new Date());
                                HashMap<String, String> hash = new HashMap();
                                hash.put("balance", String.valueOf(snoBalance - WITHDRAW_LIMIT));
                                hash.put("Time", date);
                                hash.put("url", "payment pending");
                                mDatabaseReference.child(user.getUid()).child("transaction").push().setValue(hash);
                                // mstorageref.child("transaction").setvalue(hash);
                                alerts("You payment is done.. Waiting for confirmation");

                            }
                            else
                                alerts("You don't have any SNO COIN to withdraw. Balance = "+String.valueOf(snoBalance-WITHDRAW_LIMIT));

                        } else {
                            alerts("You don't have any SNO COIN to withdraw");
                        }

                    }
                });

        alertDialog.show();
    }

    public  void alerts(String str)
    {
        alertDialog=new AlertDialog.Builder(Main2Activity.this).create();

        alertDialog.setMessage(str);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,"Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }




}





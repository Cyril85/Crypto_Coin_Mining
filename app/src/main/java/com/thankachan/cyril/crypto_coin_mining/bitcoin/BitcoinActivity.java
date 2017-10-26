package milanroxe.inc.snocoins.bitcoin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.floatingactionmenu.FloatingActionMenu;
import com.flask.floatingactionmenu.FloatingActionToggleButton;
import com.flask.floatingactionmenu.OnFloatingActionMenuSelectedListener;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.hitomi.cmlibrary.CircleMenu;

import java.util.ArrayList;
import java.util.List;

import milanroxe.inc.snocoins.BANKANDPANActivity;
import milanroxe.inc.snocoins.R;
import milanroxe.inc.snocoins.cartActivity;

public class BitcoinActivity  extends DemoBase implements SeekBar.OnSeekBarChangeListener,
        OnChartGestureListener, OnChartValueSelectedListener {
    private LineChart mChart;
    CircleMenu circleMenu;
    private static final String TAG = "Bitcoin_act";
    private TextView bitcoinbalance, buy_rate, sell_rate;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences sharedPreferences;
    private String bal, buy_rate_, sell_rate_;
    private LinearLayout linearLayout;

    FirebaseStorage storage;
   // ImageView ivFirebaseImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitcoin);

        /*FirebaseDatabase.getInstance().getReference().child("buy_rate").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {
                    bit_buy.setText(dataSnapshot.getValue().toString());
                    Log.e(TAG, "buy_rate:- " + dataSnapshot.getValue().toString());
                    sharedPreferences.edit().putString("buy_rate", dataSnapshot.getValue().toString()).commit();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        mChart = (LineChart) findViewById(R.id.chart1);
        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(true);

        mChart.animateX(3000);

        // no description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);


        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
        mv.setChartView(mChart); // For bounds control
        mChart.setMarker(mv); // Set the marker to the chart

        // x-axis limit line
        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.setTextColor(android.R.color.black);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setTextColor(android.R.color.black);
        mChart.getAxisRight().setEnabled(false);


        // add data
        setData(45, 100);


        mChart.animateX(2500);
        mChart.setPinchZoom(true);
        //mChart.invalidate();


        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.SQUARE);

        if (isConnected(getApplicationContext())) {
            bitcoinbalance = (TextView) findViewById(R.id.bitcoinbalance);
            buy_rate = (TextView) findViewById(R.id.bitcoin_buy_rate_textview);
            sell_rate = (TextView) findViewById(R.id.bitcoin_sell_rate_textview);


            firebaseAuth = FirebaseAuth.getInstance();
            mDatabaseReference = FirebaseDatabase.getInstance().getReference();

            storage = FirebaseStorage.getInstance();
       //     ivFirebaseImage = (ImageView) findViewById(R.id.ivFirebaseImage);

            //Ref : https://code.tutsplus.com/tutorials/firebase-for-android-file-storage--cms-27376
//            StorageReference storageRef = storage.getReferenceFromUrl("gs://snocoins-ccfa2.appspot.com chevron_right/Pancards/").child("OsSXAUJXRhPTquU3xwurnVzjlpx1.jpg");
//            try {
//                final File localFile = File.createTempFile("images", "png");
//                storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
//                   //     ivFirebaseImage.setImageBitmap(bitmap);
//
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                    }
//                });
//            } catch (IOException e) {
//            }

            Log.e(TAG, firebaseAuth.getCurrentUser().getUid());

            //gets from shared preference
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            Log.e(TAG, "shared Preference name:- " + getApplicationContext().getPackageName() + "_preferences");
            bal = sharedPreferences.getString("bitcoinbalance", "");
            buy_rate_ = sharedPreferences.getString("buy_rate", "");
            sell_rate_ = sharedPreferences.getString("sell_rate", "");
            Log.e(TAG, "Rsbalance:- " + sharedPreferences.getString("rsbalance", ""));
            if (buy_rate_ != "")
                buy_rate.setText(buy_rate_);

            if (sell_rate_ != "")
                sell_rate.setText(sell_rate_);

            linearLayout = (LinearLayout) findViewById(R.id.buy_sell_layout);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), buyandsellActivity.class));
                }
            });

            //gets from Firebase
            mDatabaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("bitcoinbalance").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null) {
                        Log.e(TAG, "---->In bitcoin act value added listner-- bitcoinbalance");
                        sharedPreferences.edit().putString("bitcoinbalance", dataSnapshot.getValue().toString()).commit();
                        //sharedPreferences.edit().putString("bitcoinbalance",dataSnapshot.getValue().toString()).commit();
                        Log.e(TAG, dataSnapshot.getValue().toString());
                        //  showToast(" in datasnapshot bitcoinbalance:- " + dataSnapshot.getValue().toString());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            //gets from Firebase
            mDatabaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("rs_balance").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        Log.e(TAG, "---->In bitcoin act value added listner");
                        sharedPreferences.edit().putString("rsbalance", dataSnapshot.getValue().toString()).commit();
                        //sharedPreferences.edit().putString("bitcoinbalance",dataSnapshot.getValue().toString()).commit();
                        Log.e(TAG, dataSnapshot.getValue().toString());
                        //   showToast(" in datasnapshot rs balance:- " + dataSnapshot.getValue().toString());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //gets from Firebase
            mDatabaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("bitcoinbalance").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null) {
                        bitcoinbalance.setText(dataSnapshot.getValue().toString());
                        sharedPreferences.edit().putString("bitcoinbalance", dataSnapshot.getValue().toString()).commit();
                        Log.e(TAG, dataSnapshot.getValue().toString());
                        // showToast(" in datasnapshot Bit coin balance:- " + dataSnapshot.getValue().toString());

                    } else {
                        mDatabaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("bitcoinbalance").setValue(0);
                        sharedPreferences.edit().putString("bitcoinbalance", "0").commit();
                    }
                    // showToast(" Bit coin balance:- " + dataSnapshot.getValue().toString());

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            if (bal != "")
                bitcoinbalance.setText(bal);
            else
                bitcoinbalance.setText("0");


            Log.e(TAG, mDatabaseReference.toString());

            DatabaseReference buy_ref = mDatabaseReference.child("buy_rate");
            buy_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    buy_rate.setText("Ƀ ≈ ₹ " + dataSnapshot.getValue().toString());
                    Log.e(TAG, "buy_rate:- " + dataSnapshot.getValue().toString());
                    sharedPreferences.edit().putString("buy_rate", dataSnapshot.getValue().toString()).commit();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            Log.e(TAG, buy_ref.toString());
            DatabaseReference sell_ref = mDatabaseReference.child("sell_rate");
            Log.e(TAG, sell_ref.toString());
            sell_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.e(TAG, "sell_rate:- " + dataSnapshot.getValue().toString());
                    sell_rate.setText("Ƀ ≈ ₹" + dataSnapshot.getValue().toString());
                    sharedPreferences.edit().putString("sell_rate", dataSnapshot.getValue().toString()).commit();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            setOnClickEvent(R.id.fab_toggle, "Main Menu");

            final FloatingActionMenu floatingActionMenu = (FloatingActionMenu) findViewById(R.id.fam);
            floatingActionMenu.setLabelText(0, "Verification");

            floatingActionMenu.setLabelText(1, "Buy And Sell");
            floatingActionMenu.setLabelText(2, "Send And Receive");
            floatingActionMenu.setLabelText(3, "Wallet");
            floatingActionMenu.setLabelText(4, "History");
            floatingActionMenu.setLabelText(5, "Main Menu");
            floatingActionMenu.setIcon(2, R.drawable.sendandrecive);
            floatingActionMenu.setBackgroundColor(2, getResources().getColor(R.color.accent_material_light), false);


            floatingActionMenu.setOnFloatingActionMenuSelectedListener(new OnFloatingActionMenuSelectedListener() {
                @Override
                public void onFloatingActionMenuSelected(com.flask.floatingactionmenu.FloatingActionButton fab) {
                    if (fab instanceof FloatingActionToggleButton) {
                        FloatingActionToggleButton fatb = (FloatingActionToggleButton) fab;
                        if (fatb.isToggleOn()) {
                            toast(fab.getLabelText());
                        }
                    } else {
                        toast(fab.getLabelText());
                    }
                }
            });

            floatingActionMenu.setOnFloatingActionMenuSelectedListener(new OnFloatingActionMenuSelectedListener() {
                @Override
                public void onFloatingActionMenuSelected(com.flask.floatingactionmenu.FloatingActionButton fab) {
                    switch (fab.getId()) {
                        case R.id.fam:
                            startActivity(new Intent(getApplicationContext(), cartActivity.class));
                            break;
                        case R.id.fabb:
                            startActivity(new Intent(getApplicationContext(), buyandsellActivity.class));
                            break;
                        case R.id.fabc:
                            startActivity(new Intent(getApplicationContext(), BitCoinHistory.class));
                            break;
                        case R.id.fabbb:
                            startActivity(new Intent(getApplicationContext(), BitcoinSendAndReceiveActivity.class));
                            break;
                        case R.id.fabbbb:
                            startActivity(new Intent(getApplicationContext(), walletActivity.class));
                            break;
                        case R.id.faba:
                            startActivity(new Intent(getApplicationContext(), BANKANDPANActivity.class));
                            break;
                    }

                }
            });


        } else {
            //  Toast.makeText(getApplicationContext(), " NOT Connected to Internet",Toast.LENGTH_SHORT).show();
            alert(getApplicationContext());
        }
    }


    private void setOnClickEvent(int id, final String msg) {
        findViewById(id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast(msg);
            }
        });
    }

    private void toast(String msg) {
        Toast.makeText(BitcoinActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting()))
                return true;
            else return false;
        } else
            return false;
    }

    public void alert(Context c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("No Internet connection.Do you want to close this application ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });

        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("No Internet");
        alert.show();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.line, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

       /* tvX.setText("" + (mSeekBarX.getProgress() + 1));
        tvY.setText("" + (mSeekBarY.getProgress()));

        setData(mSeekBarX.getProgress() + 1, mSeekBarY.getProgress());

        // redraw
        mChart.invalidate();*/
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    private void setData(int count, float range) {

        ArrayList<Entry> values = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {

            float val = (float) (Math.random() * range) + 3;
            values.add(new Entry(i, val, getResources().getDrawable(R.mipmap.ic_launcher)));
        }

        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "DataSet 1");

            set1.setDrawIcons(false);

            // set the line to be drawn like this "- - - - - -"
//            set1.enableDashedLine(10f, 5f, 0f);
//            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
           // set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
                set1.setFillDrawable(drawable);
            }
            else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            mChart.setData(data);
        }

        List<ILineDataSet> sets = mChart.getData()
                .getDataSets();

        for (ILineDataSet iSet : sets) {

            LineDataSet set = (LineDataSet) iSet;
            set.setMode(set.getMode() == LineDataSet.Mode.CUBIC_BEZIER
                    ? LineDataSet.Mode.LINEAR
                    :  LineDataSet.Mode.CUBIC_BEZIER);

            //Hide Number values on Graph
            set.setDrawValues(!set.isDrawValuesEnabled());

            //Doted on values on Graph
            if (set.isDrawCirclesEnabled())
                set.setDrawCircles(false);
            else
                set.setDrawCircles(true);
        }

        mChart.invalidate();
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "START, x: " + me.getX() + ", y: " + me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

        // un-highlight values after the gesture is finished and no single-tap
        if(lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            mChart.highlightValues(null); // or highlightTouch(null) for callback to onNothingSelected(...)
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart longpressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.i("Fling", "Chart flinged. VeloX: " + velocityX + ", VeloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
        Log.i("LOWHIGH", "low: " + mChart.getLowestVisibleX() + ", high: " + mChart.getHighestVisibleX());
        Log.i("MIN MAX", "xmin: " + mChart.getXChartMin() + ", xmax: " + mChart.getXChartMax() + ", ymin: " + mChart.getYChartMin() + ", ymax: " + mChart.getYChartMax());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }
}

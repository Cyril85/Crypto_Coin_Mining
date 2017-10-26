package com.thankachan.cyril.crypto_coin_mining;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SellActivity extends AppCompatActivity {
    int snoBalance;
    SharedPreferences prefs;
    TextView snoBalTxt, firebaseRate;
    EditText amount;
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        snoBalance = prefs.getInt("snoBalance", 0);
        snoBalTxt = (TextView) findViewById(R.id.snoBalance);

        snoBalTxt.setText(getString(R.string.avail_sno_coin) + " " + snoBalance);

        amount = (EditText) findViewById(R.id.amount);
        firebaseRate = (TextView) findViewById(R.id.firebaseRate);

        mRef.child("rates").child("value").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println(snapshot.getValue());
                firebaseRate.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void toSell(View view) {
        String amtStr = amount.getText().toString();
        if (amtStr.isEmpty()) {
            //if nothing is entered
            Toast.makeText(this, "Please enter amount to sell", Toast.LENGTH_SHORT).show();
            return;
        }
        if (snoBalance != 0) {
            int toSell = Integer.parseInt(amtStr);
            if (toSell > snoBalance) {
                ///if user tries to wthdraw higher sno coin
                Toast.makeText(this, "You can't withdraw a higher amount than you have", Toast.LENGTH_SHORT).show();
                return;
            }
            //All criteria passed do what you want to
            snoBalance = snoBalance - toSell;
            PreferenceManager.getDefaultSharedPreferences(SellActivity.this).edit().putInt("snoBalance", snoBalance).apply();
            snoBalance = prefs.getInt("snoBalance", 0);
            snoBalTxt.setText(getString(R.string.avail_sno_coin) + " " + snoBalance);

            FirebaseUser user = firebaseAuth.getCurrentUser();
            mDatabaseReference.child(user.getUid()).child("balance").setValue(snoBalance);
        } else {
            Toast.makeText(this, "You don't have any SNO COIN to sell", Toast.LENGTH_SHORT).show();
        }
    }
}

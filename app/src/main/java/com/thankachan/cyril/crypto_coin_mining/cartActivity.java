package com.thankachan.cyril.crypto_coin_mining;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class cartActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {
    BillingProcessor bp;
    public static String SNO_COIN = "sno_coins";
    public static String SNO_GOLD_COINS = "sno_gold_coins";
    public static String SNO_SILVER_COINS = "sno_silver_coin";
    int snoBalance;
    SharedPreferences prefs;
    TextView snoBalTxt;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        bp = new BillingProcessor(this, getString(R.string.licence_key), this);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        snoBalance = prefs.getInt("snoBalance", 0);
        snoBalTxt = (TextView) findViewById(R.id.snoBalance);

        snoBalTxt.setText(getString(R.string.avail_sno_coin) + " " + snoBalance);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new CustomPagerAdapter(this));

    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {

        if (productId.equals(SNO_COIN)) {
            //Do stuff when SNO COIN is purchased
            snoBalance = snoBalance + 20;

            PreferenceManager.getDefaultSharedPreferences(cartActivity.this).edit().putInt("snoBalance", snoBalance).apply();
            snoBalance = prefs.getInt("snoBalance", 0);
            snoBalTxt.setText(getString(R.string.avail_sno_coin) + " " + snoBalance);

        } else if (productId.equals(SNO_GOLD_COINS)) {
            //Do stuff when SNO GOLD COIN is purchased
            snoBalance = snoBalance + 200;

            PreferenceManager.getDefaultSharedPreferences(cartActivity.this).edit().putInt("snoBalance", snoBalance).apply();
            snoBalance = prefs.getInt("snoBalance", 0);
            snoBalTxt.setText(getString(R.string.avail_sno_coin) + " " + snoBalance);
        } else if (productId.equals(SNO_SILVER_COINS)) {
            //Do stuff when SNO SILVER COIN is purchased
            snoBalance = snoBalance + 2000;

            PreferenceManager.getDefaultSharedPreferences(cartActivity.this).edit().putInt("snoBalance", snoBalance).apply();
            snoBalance = prefs.getInt("snoBalance", 0);
            snoBalTxt.setText(getString(R.string.avail_sno_coin) + " " + snoBalance);
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();

        mDatabaseReference.child(user.getUid()).child("balance").setValue(snoBalance);
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        // Toast.makeText(this, "Purchase error: " + errorCode, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBillingInitialized() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    public void snoSilver(View view) {
        bp.purchase(cartActivity.this, SNO_COIN);
    }

    public void snoGold(View view) {
        bp.purchase(cartActivity.this, SNO_GOLD_COINS);
    }

    public void snoCoins(View view) {
        bp.purchase(cartActivity.this, SNO_SILVER_COINS);
    }

    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();

        super.onDestroy();
    }
}

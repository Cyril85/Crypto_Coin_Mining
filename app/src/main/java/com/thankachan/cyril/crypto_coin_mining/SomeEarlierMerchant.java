package com.thankachan.cyril.crypto_coin_mining;

import android.app.Activity;
import android.os.Bundle;

import com.razorpay.Checkout;

public class SomeEarlierMerchant extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_some_earlier_merchant);
        Checkout.preload(getApplicationContext());
    }
}

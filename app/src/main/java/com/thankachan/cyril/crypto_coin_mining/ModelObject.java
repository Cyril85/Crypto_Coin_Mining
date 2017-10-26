package com.thankachan.cyril.crypto_coin_mining;

/**
 * Created by Holla Inc on 6/8/2017.
 */
public enum ModelObject {

    SNOCOIN(R.string.sno_coin, R.layout.activity_cart_item1),
    SNOSILVER(R.string.sno_silver_coins, R.layout.activity_cart_item2),
    SNOGOLD(R.string.sno_gold_coins, R.layout.activity_cart_item3);

    private int mTitleResId;
    private int mLayoutResId;

    ModelObject(int titleResId, int layoutResId) {
        mTitleResId = titleResId;
        mLayoutResId = layoutResId;
    }

    public int getTitleResId() {
        return mTitleResId;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }

}
package milanroxe.inc.snocoins;

/**
 * Created by Holla Inc on 6/8/2017.
 */
public enum ModelObject {

    SNOMONTH(R.string.sno_month, R.layout.activity_cart_item1),
    SNO6MONTH(R.string.sno_6month, R.layout.activity_cart_item2),
    SNO1YEAR(R.string.sno_1year, R.layout.activity_cart_item3);

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
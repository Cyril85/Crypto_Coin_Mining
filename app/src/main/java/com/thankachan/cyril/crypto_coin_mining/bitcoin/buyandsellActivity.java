package milanroxe.inc.snocoins.bitcoin;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import milanroxe.inc.snocoins.R;

public class buyandsellActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "btcoin_buy_sell";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private BuyAndSellSectionAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyandsell);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new BuyAndSellSectionAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs2);
        tabLayout.setupWithViewPager(mViewPager);


        tabLayout.setOnClickListener(this);


    }


    private void setupViewPager(ViewPager viewPager) {
        BuyAndSellSectionAdapter adapter = new BuyAndSellSectionAdapter(getSupportFragmentManager());
        adapter.addFragment(new BitCoinBuyFragment(),"BUY");
        adapter.addFragment(new BitcoinSellFragment(),"SELL");

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {

    }
}

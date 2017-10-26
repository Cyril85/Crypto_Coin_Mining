package milanroxe.inc.snocoins;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import milanroxe.inc.snocoins.withdraw.Tab1Fragment;
import milanroxe.inc.snocoins.withdraw.TipsAlert;


public class tabActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        Log.d(TAG, "onCreate: Starting.");
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);


        ImageView i=(ImageView)findViewById(R.id.help_image_button);
        i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*AlertDialog alBuilder=new AlertDialog.Builder(tabActivity.this).create();

                alBuilder.setTitle("Tips");
                alBuilder.se
                alBuilder.setIcon(R.drawable.ic_payment_completed);
                alBuilder.setMessage("Payment completed");
                alBuilder.setIcon(R.drawable.ic_payment_pending);
                alBuilder.setMessage("Payment pending");
                alBuilder.setIcon(R.drawable.ic_payment_cancel);
                alBuilder.setMessage("Payment cancelled");

                alBuilder.show();*/
                TipsAlert t=new TipsAlert();
                t.show(getFragmentManager(),"Tips");
            }
        });

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setOnClickListener(this);
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new Tab1Fragment(),"TRANSACTION");
        adapter.addFragment(new Tab2Fragment(),"PURCHASES");
        adapter.addFragment(new Tab3Fragment(),"REWARD");
        viewPager.setAdapter(adapter);
    }


    @Override
    public void onClick(View v) {
        }
    }

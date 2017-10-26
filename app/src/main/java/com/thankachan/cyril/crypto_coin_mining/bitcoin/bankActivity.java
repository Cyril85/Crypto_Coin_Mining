package milanroxe.inc.snocoins.bitcoin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import milanroxe.inc.snocoins.R;

public class bankActivity extends AppCompatActivity {

    private static final String TAG = "Bank_act";
    private TextView amt,servicetax,total;
    String total_amount;
    private Button update_ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank);

        Log.e(TAG,"In Bank activity");


        long am=getIntent().getLongExtra("depost_amt",-1);
        total_amount=String.valueOf(am);

        amt = (TextView) findViewById(R.id.textView40);
        amt.setText(total_amount);
        servicetax =(TextView)findViewById(R.id.service_tax);
        servicetax.setVisibility(0);
        total =(TextView)findViewById(R.id.deposituamount);
        total_amount=amt.getText().toString();
        update_ref = (Button) findViewById(R.id.updateref);

        final long __amt=getIntent().getLongExtra("depost_amt",0);
        if (__amt!=0)
        {
            amt.setText(String.valueOf(__amt));

            total.setText(String.valueOf(__amt));

            update_ref.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.e(TAG,"Clicked on update button");
                    Log.e(TAG,total_amount);

                    BankReferenceAlerts t=new BankReferenceAlerts();
                    t.bit_amt=total_amount;
                    t.show(getFragmentManager(),"Bank");



                    //final View l=getLayoutInflater().inflate(R.layout.alert_bank_reference,null);


                }
            });







        }


    }
}

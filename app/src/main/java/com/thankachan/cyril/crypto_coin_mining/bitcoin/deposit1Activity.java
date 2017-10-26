package milanroxe.inc.snocoins.bitcoin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import cn.pedant.SweetAlert.SweetAlertDialog;
import milanroxe.inc.snocoins.R;

public class deposit1Activity extends AppCompatActivity {

    private EditText entered_amount;
    private Button bank_button;
    private DatabaseReference mDatabaseReference;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit1);

        entered_amount = (EditText) findViewById(R.id.entered_amount);
        bank_button = (Button) findViewById(R.id.bank_button);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        firebaseAuth = FirebaseAuth.getInstance();

        mDatabaseReference =  mDatabaseReference.child(firebaseAuth.getCurrentUser().getUid());

        bank_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = entered_amount.getText().toString();
                Log.e("Sell_activity", amount);
                if (amount.length() > 0) {
                    Long amt = Long.parseLong(amount);

                    if (!TextUtils.isEmpty(amount)) {
                        if (amt > 1999) {
                            if (amt < 500001) {
                                Intent i = new Intent(getApplicationContext(), bankActivity.class);
                                i.putExtra("depost_amt", amt);
                                startActivity(i);


                            } else {
                                SweetAlertDialog pDialog = new SweetAlertDialog(deposit1Activity.this, SweetAlertDialog.ERROR_TYPE);
                                pDialog.getProgressHelper().setBarColor(R.color.colorPrimary);
                                pDialog.setTitleText("Oops...");
                                pDialog.setContentText("Max. Amount to deposit is 500000");
                                pDialog.setCancelable(false);
                                pDialog.show();

                            }
                        } else {
                            SweetAlertDialog pDialog = new SweetAlertDialog(deposit1Activity.this, SweetAlertDialog.ERROR_TYPE);
                            pDialog.getProgressHelper().setBarColor(R.color.colorPrimary);
                            pDialog.setTitleText("Oops...");
                            pDialog.setContentText("Min. Amount to deposit is 2000");
                            pDialog.setCancelable(false);
                            pDialog.show();


                        }
                    } else {
                        SweetAlertDialog pDialog = new SweetAlertDialog(deposit1Activity.this, SweetAlertDialog.ERROR_TYPE);
                        pDialog.getProgressHelper().setBarColor(R.color.colorPrimary);
                        pDialog.setTitleText("Oops...");
                        pDialog.setContentText("Please Enter Amount For Deposit");
                        pDialog.setCancelable(false);
                        pDialog.show();


                    }

                } else{
                    SweetAlertDialog pDialog = new SweetAlertDialog(deposit1Activity.this, SweetAlertDialog.ERROR_TYPE);
                    pDialog.getProgressHelper().setBarColor(R.color.colorPrimary);
                    pDialog.setTitleText("Oops...");
                    pDialog.setContentText("Please Enter Amount For Deposit");
                    pDialog.setCancelable(false);
                    pDialog.show();


            }

        }});



    }
}

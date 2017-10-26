package milanroxe.inc.snocoins.bitcoin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import milanroxe.inc.snocoins.R;

public class walletActivity extends AppCompatActivity {

    private Button deposit,withdraw;
    private TextView amount;
    private DatabaseReference mDatabaseReference;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth firebaseAuth;
    private String verfied;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        deposit=(Button)findViewById(R.id.amount_deposit_button);
        withdraw = (Button)findViewById(R.id.amount_withdraw_button);
        amount = (TextView) findViewById(R.id.rs_balance);



        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        firebaseAuth = FirebaseAuth.getInstance();


        mDatabaseReference =  mDatabaseReference.child(firebaseAuth.getCurrentUser().getUid());


        mDatabaseReference.child("rsbalance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {
                    amount.setText(dataSnapshot.getValue().toString());
                    Log.e("Wallet Activity", "Bala:- " + dataSnapshot.getValue().toString());
                    sharedPreferences.edit().putString("rsbalance", dataSnapshot.getValue().toString()).commit();
                }
                else
                {
                    mDatabaseReference.child("rsbalance").setValue(0.0);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        String am;
        if(!(am=sharedPreferences.getString("rsbalance","")).equals("")) {
            amount.setText(am);

        }

        //verification
        mDatabaseReference.child("verification").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                verfied = dataSnapshot.getValue().toString();
                Log.e("Wallet Activitty","verified:- "+verfied);
                if(verfied.equals("yes")) {
                    sharedPreferences.edit().putBoolean("verified", true).commit();
                    enableButtons();
                }
                else {
                    sharedPreferences.edit().putBoolean("verified", false).commit();
                    disableButtons();
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        if(sharedPreferences.getBoolean("verified",false))
        {
            enableButtons();
        }
        else
        {

            AlertDialog alertDialog=new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Documents verfication");
            alertDialog.setMessage("Your documents are not verfied");
            alertDialog.show();
        }


        deposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),deposit1Activity.class));
            }
        });

        withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),withdraw2Activity.class));
            }
        });

    }

    private void disableButtons() {
        deposit.setClickable(false);
        withdraw.setClickable(false);

    }

    private void enableButtons() {
        deposit.setClickable(true);
        withdraw.setClickable(true);
    }

}

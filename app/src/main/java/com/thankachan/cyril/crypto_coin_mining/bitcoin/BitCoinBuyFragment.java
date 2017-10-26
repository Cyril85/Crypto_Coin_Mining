package milanroxe.inc.snocoins.bitcoin;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import milanroxe.inc.snocoins.R;

/**
 * Created by mohan on 24/6/17.
 */

public class BitCoinBuyFragment extends Fragment {
    private static final String TAG = "Buy_Fragment";
    private int i = -1;
    private TextView rs_bal,bit_buy,bit_buy_ans;
    private Button buy;
    private EditText buy_amount;
    private SharedPreferences sharedPreferences;
    String rs_amt,bit_bal,bit_buy_bal;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.bitcoin_buy_fragment, container, false);

        FireBaseDBRef fireBaseDBRef = new FireBaseDBRef();

        databaseReference = fireBaseDBRef.getDatabaseReference();

        rs_bal=(TextView) view.findViewById(R.id.textView16);
        bit_buy = (TextView) view.findViewById(R.id.bal);
        buy = (Button) view.findViewById(R.id.but_button);
        buy_amount =(EditText) view.findViewById(R.id.buy_amount);
        bit_buy_ans =(TextView) view.findViewById(R.id.buy_bits);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        rs_amt=sharedPreferences.getString("rsbalance","");
        if(!rs_amt.equals(""))
            rs_bal.setText(rs_amt);
        bit_bal=sharedPreferences.getString("bitcoinbalance","");
        bit_buy_bal = sharedPreferences.getString("buy_rate","");

        if(!bit_buy_bal.equals(""))
            bit_buy.setText(bit_buy_bal);


        FirebaseDatabase.getInstance().getReference().child("buy_rate").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {
                    bit_buy.setText(dataSnapshot.getValue().toString());
                    Log.e(TAG, "buy_rate:- " + dataSnapshot.getValue().toString());
                    sharedPreferences.edit().putString("buy_rate", dataSnapshot.getValue().toString()).commit();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference.child("rsbalance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {
                    rs_bal.setText(dataSnapshot.getValue().toString());
                    Log.e(TAG, "rsbalance:- " + dataSnapshot.getValue().toString());
                    sharedPreferences.edit().putString("rsbalance", dataSnapshot.getValue().toString()).commit();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        buy_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().length() > 0) {
                    Long d = new Long(bit_buy_bal);
                    Log.e(TAG,"enter amount "+s.toString());
                    Log.e(TAG,"Buy rate"+bit_buy_bal);
                    Log.e(TAG, "d * 0.000001:- "+String.valueOf(d * 0.000001));
                    Log.e(TAG,"--> "+Long.parseLong(s.toString()));
                    Log.e(TAG,String.valueOf(Long.parseLong(s.toString())/(d * 0.000001)));
                    long b = Math.round(Long.parseLong(s.toString())/(d * 0.000001));


                    bit_buy_ans.setText(String.valueOf(b)+" bits");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(buy_amount.getText().toString().length()>0) {
                    Long buy_amt = Long.parseLong(buy_amount.getText().toString());
                    long rs_bal_amt = Long.parseLong(rs_amt);

                    try {
                        if (buy_amt < rs_bal_amt) {
                            if (buy_amt > 1001) {
                                if (buy_amt < 20001) {
                                    Long bal = rs_bal_amt - buy_amt;
                                    HashMap<String, String> h = new HashMap<String, String>();
                                    h.put("date", DateFormat.getDateTimeInstance().format(new Date()));
                                    h.put("amount", String.valueOf(buy_amt));
                                    h.put("ref_no", "afafasa");
                                    h.put("image_link", "sucess");
                                    h.put("added_to_bal", "yes");
                                    h.put("type_of_deposit", "Buy");
                                    rs_bal.setText(String.valueOf(bal));


                                    Double d = new Double(bit_buy_bal);
                                    long b = Math.round(buy_amt / (d * 0.000001));

                                    Double bits = b + Double.parseDouble(bit_bal);

                                    Log.e(TAG, "4--> " + String.valueOf(bits));

                                    databaseReference.child("rsbalance").setValue(bal);
                                    sharedPreferences.edit().putString("rsbalance", String.valueOf(bal)).commit();
                                    sharedPreferences.edit().putString("bitcoinbalanc", String.valueOf(bits)).commit();

                                    databaseReference.child("bitcoinbalance").setValue(bits);
                                    databaseReference.child("bitcoin_transaction").push().setValue(h);

                                    clearForm(container);

                                    //showToast(String.valueOf(bits) + "Bits Buyed Sucessfully");
                                    final SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE)
                                            .setTitleText("Processing");
                                    pDialog.show();
                                    pDialog.setCancelable(false);
                                    new CountDownTimer(800 * 7, 800) {
                                        public void onTick(long millisUntilFinished) {
                                            // you can change the progress bar color by ProgressHelper every 800 millis
                                            i++;
                                            switch (i){
                                                case 0:
                                                    pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.blue_btn_bg_color));
                                                    break;
                                                case 1:
                                                    pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.material_deep_teal_50));
                                                    break;
                                                case 2:
                                                    pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.success_stroke_color));
                                                    break;
                                                case 3:
                                                    pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.material_deep_teal_20));
                                                    break;
                                                case 4:
                                                    pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.material_blue_grey_80));
                                                    break;
                                                case 5:
                                                    pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.warning_stroke_color));
                                                    break;
                                                case 6:
                                                    pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.success_stroke_color));
                                                    break;
                                            }
                                        }

                                        public void onFinish() {
                                            i = -1;
                                            pDialog.setTitleText("Payment Success!")
                                                    .setConfirmText("OK")
                                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                        }
                                    }.start();

                                } else{
                                    SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE);
                                    pDialog.getProgressHelper().setBarColor(R.color.colorPrimary);
                                    pDialog.setTitleText("Oops...");
                                    pDialog.setContentText("Max. Amount For Buying Bits is 500000");
                                    pDialog.setCancelable(false);
                                    pDialog.show();

                                }

                            } else{
                                SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE);
                                pDialog.getProgressHelper().setBarColor(R.color.colorPrimary);
                                pDialog.setTitleText("Oops...");
                                pDialog.setContentText("Min  Amount For Buying Bits is 1000");
                                pDialog.setCancelable(false);
                                pDialog.show();
                            }


                        } else{
                            SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
                            pDialog.getProgressHelper().setBarColor(R.color.colorPrimary);

                            pDialog.setTitleText("Not enough Funds. Please Deposit");
                            pDialog.setCancelable(false);
                            pDialog.show();
                        }

                    }catch(Exception e)
                    {
                        e.printStackTrace();
                        showToast("Exception Raised");
                    }

                }else
                {
                    SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE);
                    pDialog.getProgressHelper().setBarColor(R.color.colorPrimary);
                    pDialog.setTitleText("Oops...");
                    pDialog.setContentText("Enter Amount for Buying Bits");
                    pDialog.setCancelable(false);
                    pDialog.show();
                }


            }
        });



        return view;
    }

    private void showToast(String s) {
        Toast.makeText(getContext(),s,Toast.LENGTH_SHORT).show();
    }

    private void clearForm(ViewGroup group)
    {
        for (int i = 0, count = group.getChildCount(); i < count; ++i) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                ((EditText)view).setText("");
            }

            if(view instanceof ViewGroup && (((ViewGroup)view).getChildCount() > 0))
                clearForm((ViewGroup)view);
        }
    }

}

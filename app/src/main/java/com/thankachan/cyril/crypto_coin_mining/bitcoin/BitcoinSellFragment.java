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

public class BitcoinSellFragment extends Fragment{
    private int i = -1;
    private static final String TAG = "Sell_fragment";
    private TextView bits_text,bit_sell_value,sell_ans;
    private EditText sell_bits;
    private Button sell_button;
    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;
    private String bit_bal,rs_bal,sell_value;
    @Override
    public View onCreateView(LayoutInflater layoutInflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstance)
    {
        final View view=layoutInflater.inflate(R.layout.bitcoin_sell_fragment, container, false);

        bits_text = (TextView) view.findViewById(R.id.textView16);
        bit_sell_value = (TextView) view.findViewById(R.id.bal);
        sell_bits = (EditText) view.findViewById(R.id.sell_amount);
        sell_ans =(TextView) view.findViewById(R.id.sell_ans);
        sell_button = (Button) view.findViewById(R.id.sell_button);
        databaseReference = new FireBaseDBRef().getDatabaseReference();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        bit_bal=sharedPreferences.getString("bitcoinbalance","");
        rs_bal = sharedPreferences.getString("rsbalance","");
        sell_value = sharedPreferences.getString("sell_rate","");
        if(!bit_bal.equals(""))
            bits_text.setText(bit_bal);

        if(!sell_value.equals(""))
            bit_sell_value.setText(sell_value);
        Log.e(TAG,"Rs balance in sell:- "+rs_bal);




        FirebaseDatabase.getInstance().getReference().child("sell_rate").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {
                    bit_sell_value.setText(dataSnapshot.getValue().toString());
                    sell_value = dataSnapshot.getValue().toString();
                    Log.e(TAG, "sell_rate:- " + dataSnapshot.getValue().toString());
                    sharedPreferences.edit().putString("sell_rate", dataSnapshot.getValue().toString()).commit();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference.child("bitcoinbalance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {
                    bits_text.setText(dataSnapshot.getValue().toString());
                    bit_bal = dataSnapshot.getValue().toString();
                    Log.e(TAG, "bit coin balance:- " + dataSnapshot.getValue().toString());
                    sharedPreferences.edit().putString("bitcoinbalance", dataSnapshot.getValue().toString()).commit();
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
                    rs_bal = dataSnapshot.getValue().toString();
                    Log.e(TAG, "updatesd from firebase rsbalance:- " + dataSnapshot.getValue().toString());
                    sharedPreferences.edit().putString("rsbalance", dataSnapshot.getValue().toString()).commit();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        sell_bits.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0)
                {
                    Log.e(TAG,"1--> "+Long.parseLong(sell_value)*0.000001);
                    Log.e(TAG,"2 --> "+Long.parseLong(s.toString()));
                    Log.e(TAG,"3--> "+(Long.parseLong(sell_value)*0.000001)*Long.parseLong(s.toString()));
                    Log.e(TAG,"4 --> "+Math.round((Long.parseLong(sell_value)*0.000001)*Long.parseLong(s.toString())));
                    sell_ans.setText(String.valueOf(Math.round((Long.parseLong(sell_value)*0.000001)*Long.parseLong(s.toString()))));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        sell_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sell_bits.getText().toString().length() > 0) {

                    Double sell_coins_val = getDoubleValue(sell_ans.getText().toString());
                    Double bits_bal = getDoubleValue(bit_bal);
                    Double rs_bal_val = getDoubleValue(rs_bal);
                    if (bits_bal > sell_coins_val) {
                        if (sell_coins_val > 999) {
                            if (sell_coins_val < 1999999) {
                                Double bal = rs_bal_val + getDoubleValue(sell_ans.getText().toString());
                                HashMap<String, String> h = new HashMap<String, String>();
                                h.put("date", DateFormat.getDateTimeInstance().format(new Date()));
                                h.put("amount", String.valueOf(sell_coins_val));
                                h.put("ref_no", "afafasa");
                                h.put("image_link", "sucess");
                                h.put("added_to_bal", "yes");
                                h.put("type_of_deposit", "Sell");


                                Double bits = Double.parseDouble(bit_bal) - Double.parseDouble(sell_bits.getText().toString());

                                Log.e(TAG, "4--> " + String.valueOf(bits));
                                Log.e(TAG,"5---> rs balnce"+String.valueOf(bal));

                                databaseReference.child("rsbalance").setValue(bal);
                                sharedPreferences.edit().putString("rsbalance", String.valueOf(bal)).commit();
                                sharedPreferences.edit().putString("bitcoinbalanc", String.valueOf(bits)).commit();

                                databaseReference.child("bitcoinbalance").setValue(bits);
                                databaseReference.child("bitcoin_transaction").push().setValue(h);

                                clearForm(container);

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
                                pDialog.setContentText("Max Selling  Amount is 1000000 Bits");
                                pDialog.setCancelable(false);
                                pDialog.show();
                            }


                        } else
                        {
                            SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE);
                            pDialog.getProgressHelper().setBarColor(R.color.colorPrimary);
                            pDialog.setTitleText("Oops...");
                            pDialog.setContentText("Min Selling  Amount is 1000 Bits");
                            pDialog.setCancelable(false);
                            pDialog.show();
                        }


                    } else
                    {
                        SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
                        pDialog.getProgressHelper().setBarColor(R.color.colorPrimary);
                        pDialog.setTitleText("Oops...");
                        pDialog.setContentText("Not enough Funds. Please Deposits.");
                        pDialog.setCancelable(false);
                        pDialog.show();
                    }

                }else
                {
                    SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE);
                    pDialog.getProgressHelper().setBarColor(R.color.colorPrimary);
                    pDialog.setTitleText("Oops...");
                    pDialog.setContentText("Enter Bits For Sell");
                    pDialog.setCancelable(false);
                    pDialog.show();
                }


            }
        });





        return view;
    }

    private Double getDoubleValue(String s) { return Double.parseDouble(s); }
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

package milanroxe.inc.snocoins.bitcoin;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;
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

import static android.app.Activity.RESULT_OK;

/**
 * Created by mohan on 25/6/17.
 */

public class BitcoinSendFragment extends Fragment /*implements ZXingScannerView.ResultHandler*/ {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String TAG = "bitCoin_send_frgmt";

    private static final int REQUEST_CODE = 100;
    private static final int PERMISSION_REQUEST_CODE = 1;


   /* private ZXingScannerView mScannerView;*/

    private TextView bit_coin_bal;
    private EditText editText, bitcoin_address;
    private Button qr_scanner, send_button;
    private SharedPreferences sharedPreferences;
    private DatabaseReference databaseReference, recievedatabaseReference;
    private String bitcoin_bal, receive_address, receive_bitcoin_bal;
    private View view;
    private boolean used_scanner, gotreceivebal = false;
    ;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

       /* mScannerView = new ZXingScannerView(getContext());*/


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        databaseReference = new FireBaseDBRef().getDatabaseReference();

        view = inflater.inflate(R.layout.bitcoin_send_fragment, container, false);

        qr_scanner = (Button) view.findViewById(R.id.scanqrcode_button);
        bitcoin_address = (EditText) view.findViewById(R.id.bitcoin_address);
        send_button = (Button) view.findViewById(R.id.sendButton);
        editText = (EditText) view.findViewById(R.id.bitcoin_address);
        bit_coin_bal = (TextView) view.findViewById(R.id.bitcoin_balance);
        editText = (EditText) view.findViewById(R.id.send_amount);


        qr_scanner.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
                } else {
                    Intent i = new Intent(getContext(), QRScanActivity.class);
                    startActivityForResult(i, REQUEST_CODE);
                }

            }


        });

        //view = inflater.inflate(R.layout.bitcoin_send_fragment,container,false);


        bitcoin_bal = sharedPreferences.getString("bitcoinbalance", "");
        if (!bitcoin_bal.equals(""))
            bit_coin_bal.setText(bitcoin_bal);

        databaseReference.child("bitcoinbalance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    bit_coin_bal.setText(dataSnapshot.getValue().toString());
                    bitcoin_bal = dataSnapshot.getValue().toString();
                    Log.e(TAG, "bitcoinbalance:- " + dataSnapshot.getValue().toString());
                    sharedPreferences.edit().putString("bitcoinbalance", dataSnapshot.getValue().toString()).commit();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bitcoin_address.getText().length() > 0) {
                    String enterd_amount = editText.getText().toString();

                    if (enterd_amount.length() > 0) {
                        Double enterd_amt = Double.parseDouble(enterd_amount);
                        Double rs_bal_amt = Double.parseDouble(bitcoin_bal);
                        if (enterd_amt < rs_bal_amt) {
                            if (enterd_amt >= 20000) {
                                if (enterd_amt < 3000000) {
                                    try {
                                        Double bal = rs_bal_amt - enterd_amt;
                                        HashMap<String, String> h = new HashMap<String, String>();
                                        h.put("date", DateFormat.getDateTimeInstance().format(new Date()));
                                        h.put("amount", String.valueOf(enterd_amt));
                                        h.put("ref_no", bitcoin_address.getText().toString());
                                        h.put("image_link", "pending");
                                        h.put("added_to_bal", "yes");
                                        h.put("type_of_deposit", "Send");
                                        bit_coin_bal.setText(String.valueOf(bal));

                                        sharedPreferences.edit().putString("bitcoinbalance", String.valueOf(bal)).commit();
                                        bit_coin_bal.setText(String.valueOf(bal));
                                        bitcoin_bal = String.valueOf(bal);
                                        databaseReference.child("bitcoinbalance").setValue(bal);
                                        databaseReference.child("bitcoin_transaction").push().setValue(h);


                                        clearForm(container);

                                        if (used_scanner) {

                                            if (gotreceivebal) {
                                                h = new HashMap<String, String>();
                                                h.put("date", DateFormat.getDateTimeInstance().format(new Date()));
                                                h.put("amount", String.valueOf(enterd_amt));
                                                h.put("ref_no", bitcoin_address.getText().toString());
                                                h.put("image_link", "sucess");
                                                h.put("added_to_bal", "yes");
                                                h.put("type_of_deposit", "Receive");
                                                //bit_coin_bal.setText(String.valueOf(bal));


                                                recievedatabaseReference.child("bitcoin_transaction").push().setValue(h);


                                                long rec = Long.parseLong(receive_bitcoin_bal);
                                                recievedatabaseReference.child("bitcoinbalance").setValue(rec + enterd_amt);
                                                used_scanner = false;
                                                gotreceivebal = false;

                                            } else {
                                                sharedPreferences.edit().putString("bitcoinbalance", String.valueOf(rs_bal_amt)).commit();

                                                databaseReference.child("bitcoinbalance").setValue(rs_bal_amt);
                                            }
                                        } else {
                                            SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE);
                                            pDialog.getProgressHelper().setBarColor(R.color.colorPrimary);
                                            pDialog.setTitleText("Payment Sucess");
                                            pDialog.setCancelable(false);
                                            pDialog.show();

                                        }


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Log.e(TAG, "Exception");
                                        sharedPreferences.edit().putString("bitcoinbalance", String.valueOf(rs_bal_amt)).commit();

                                        databaseReference.child("bitcoinbalance").setValue(rs_bal_amt);


                                    }

                                } else {
                                    SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE);
                                    pDialog.getProgressHelper().setBarColor(R.color.colorPrimary);
                                    pDialog.setTitleText("Maximum Bits sending Limit is 3000000");
                                    pDialog.setCancelable(false);
                                    pDialog.show();
                                }
                            } else {
                                SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE);
                                pDialog.getProgressHelper().setBarColor(R.color.colorPrimary);
                                pDialog.setTitleText("Oops...");
                                pDialog.setContentText("Mini Bits Sending Limit is 20000!");
                                pDialog.setCancelable(false);
                                pDialog.show();
                            }
                        } else {
                            SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
                            pDialog.getProgressHelper().setBarColor(R.color.colorPrimary);
                            pDialog.setTitleText("Oops...");
                            pDialog.setContentText("Not enough Funds. Please click on Deposits!");
                            pDialog.setCancelable(false);
                            pDialog.show();

                        }

                    } else {
                        SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE);
                        pDialog.getProgressHelper().setBarColor(R.color.colorPrimary);
                        pDialog.setTitleText("Oops...");
                        pDialog.setContentText("Please Enter  Vailed Amount!");
                        pDialog.setCancelable(false);
                        pDialog.show();
                    }

                } else {
                    SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE);
                    pDialog.getProgressHelper().setBarColor(R.color.colorPrimary);
                    pDialog.setTitleText("Oops...");
                    pDialog.setContentText("Please Enter  BitCoin Address!");
                    pDialog.setCancelable(false);
                    pDialog.show();
                }


            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(getActivity(), permissions[0]) == PackageManager.PERMISSION_GRANTED) {

            switch (requestCode) {
                case 1:
                    Intent i = new Intent(getContext(), QRScanActivity.class);
                    startActivityForResult(i, REQUEST_CODE);
                    break;
            }
        }
    }


    private void showToast(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
    }

    private void clearForm(ViewGroup group) {
        for (int i = 0, count = group.getChildCount(); i < count; ++i) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                ((EditText) view).setText("");
            }

            if (view instanceof ViewGroup && (((ViewGroup) view).getChildCount() > 0))
                clearForm((ViewGroup) view);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "in result method");
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {

                final Barcode barcode = data.getParcelableExtra("barcode");
                String qr_code[] = barcode.displayValue.split(" ");
                receive_address = qr_code[1];
                receive_bitcoin_bal = qr_code[2];
                used_scanner = data.getBooleanExtra("scanned", false);

                Log.e(TAG, barcode.displayValue);
                Log.e(TAG, "Receie:- " + receive_address);
                Log.e(TAG, "scanned:- " + used_scanner);
                gotreceivebal = true;
                Log.e(TAG, "recive_balance:- " + receive_bitcoin_bal);
                //showToast(barcode.displayValue);
                bitcoin_address.setText(qr_code[0]);

                recievedatabaseReference = FirebaseDatabase.getInstance().getReference().child(receive_address);

                /*recievedatabaseReference.child("bitcoinbalance").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            Log.e(TAG, "recive_bal:- " + dataSnapshot.getValue().toString());
                            receive_bitcoin_bal = dataSnapshot.getValue().toString();
                            gotreceivebal = true;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });*/


            }
        }
    }
}

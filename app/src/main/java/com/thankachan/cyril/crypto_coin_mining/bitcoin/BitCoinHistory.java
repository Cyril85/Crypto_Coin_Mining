package milanroxe.inc.snocoins.bitcoin;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import cn.pedant.SweetAlert.SweetAlertDialog;
import milanroxe.inc.snocoins.R;

public class BitCoinHistory extends AppCompatActivity {

    private static final String TAG = "BitCoin_history";
    private ListView listView;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth firebaseAuth;
    private BitCoinHistoryAdapter bitCoinHistoryAdapter;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bit_coin_history);

        listView = (ListView) findViewById(R.id.bitcoin_list);

        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(firebaseAuth.getCurrentUser().getUid()).child("bitcoin_transaction");
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot != null) {

                    Map<String, BitCoinTransactionModel> data = (Map<String, BitCoinTransactionModel>) dataSnapshot.getValue();

                    if (data != null) {
                        Vector<BitCoinTransactionModel> v = new Vector<BitCoinTransactionModel>();
                        for (Map.Entry m : data.entrySet()) {

                            Log.e(TAG,m.getValue().toString());

                            String s[] = m.getValue().toString().split(",");
                            Log.e(TAG,"1->  "+Arrays.toString(s));

                            //[{date=Jun 24,  2017 2:33:16 AM,  amount=2000,  image_link=pending,  added_to_bal=no,  ref_no=fgjj,  type_of_deposit=Deposit}]
                            BitCoinTransactionModel b = new BitCoinTransactionModel();

                            b.setKey(m.getKey().toString());
                            String bal=sharedPreferences.getString("rsbalance","");
                            if(!bal.equals(""))
                                b.setRsbalance(bal);

                            String st[] = s[0].split("=");
                            b.setDate(st[1].toString() + s[1].toString());

                            st = s[2].split("=");
                            Log.e(TAG, "2->  "+Arrays.toString(st));
                            b.setAmount(st[1].toString());


                            String st1[] = s[3].split("=");
                            Log.e(TAG, "3->  "+Arrays.toString(st1));
                            b.setImage_link(st1[1]);

                            String st2[] = s[4].split("=");
                            Log.e(TAG, "4->  "+Arrays.toString(st2));
                            b.setAdded_to_bal(st2[1]);

                            st2 = s[6].split("=");
                            Log.e(TAG, "5->  "+Arrays.toString(st2));
                            b.setType_of_deposit(st2[1]);
                            Log.e("Tag1 frag", b.toString());





                            v.add(b);

                        }
                        bitCoinHistoryAdapter = new BitCoinHistoryAdapter(getApplicationContext(), v);
                        listView.setAdapter(bitCoinHistoryAdapter);
                    } else {
                        Log.e(TAG, "---in else-- no data");
/*                        AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).create();
                        alertDialog.setTitle("No History");
                        alertDialog.setMessage("Please Withdraw the coins to get history");
                        alertDialog.show();*/

                      //  Toast.makeText(getApplicationContext(),"Please Withdraw the coins to get history",Toast.LENGTH_SHORT).show();
                        SweetAlertDialog pDialog = new SweetAlertDialog(BitCoinHistory.this,SweetAlertDialog.ERROR_TYPE);
                        pDialog.getProgressHelper().setBarColor(R.color.colorPrimary);
                        pDialog.setConfirmText("Please Withdraw the coins to get history");
                        pDialog.setTitleText("No History");
                        pDialog.setCancelable(false);
                        pDialog.show();

                    }
                } else {
                    Log.e(TAG, "---in else-- no child");

                  //  Toast.makeText(getApplicationContext(),"Please Withdraw the coins to get history",Toast.LENGTH_SHORT).show();
                    SweetAlertDialog pDialog = new SweetAlertDialog(getApplicationContext(),SweetAlertDialog.ERROR_TYPE);
                    pDialog.getProgressHelper().setBarColor(R.color.colorPrimary);
                    pDialog.setConfirmText("Please Withdraw the coins to get history");
                    pDialog.setTitleText("No History");
                    pDialog.setCancelable(false);
                    pDialog.show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static class BitCoinHistoryEventListner implements ChildEventListener {

        private final BitCoinHistoryCallbacks callbacks;

        BitCoinHistoryEventListner(BitCoinHistoryCallbacks callbacks) {
            this.callbacks = callbacks;
        }

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String str) {
            Log.e(TAG, "----------On added");
            HashMap<String, String> h = (HashMap<String, String>) dataSnapshot.getValue();
            Log.e(TAG, h.toString());
            for (Map.Entry m : h.entrySet()) {

                Log.e(TAG,m.getValue().toString());



                String s[] = m.getValue().toString().split(",");
                Log.e(TAG,"1->  "+Arrays.toString(s));

                BitCoinTransactionModel b = new BitCoinTransactionModel();

                String st[] = s[0].split("=");
                b.setDate(st[1].toString() + s[1].toString());

                st = s[2].split("=");
                Log.e(TAG, "2->  "+Arrays.toString(st));
                b.setAmount(st[1].toString());



                String st1[] = s[3].split("=");
                Log.e(TAG, "3->  "+Arrays.toString(st1));
                b.setImage_link(st1[1]);

                String st2[] = s[4].split("=");
                Log.e(TAG, "3->  "+Arrays.toString(st1));
                b.setType_of_deposit(st2[1]);

                Log.e("Tag1 frag", b.toString());
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String str) {
            Log.e(TAG,"---On chng");
            HashMap<String, String> h = (HashMap<String, String>) dataSnapshot.getValue();
            Log.e(TAG, h.toString());
            for (Map.Entry m : h.entrySet()) {

                Log.e(TAG,m.getValue().toString());



                String s[] = m.getValue().toString().split(",");
                Log.e(TAG,"1->  "+Arrays.toString(s));

                BitCoinTransactionModel b = new BitCoinTransactionModel();

                String st[] = s[0].split("=");
                b.setDate(st[1].toString() + s[1].toString());

                st = s[2].split("=");
                Log.e(TAG, "2->  "+Arrays.toString(st));
                b.setAmount(st[1].toString());



                String st1[] = s[3].split("=");
                Log.e(TAG, "3->  "+Arrays.toString(st1));
                b.setImage_link(st1[1]);

                String st2[] = s[4].split("=");
                Log.e(TAG, "3->  "+Arrays.toString(st1));
                b.setType_of_deposit(st2[1]);

                Log.e("Tag1 frag", b.toString());
            }

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }

    }
}
interface BitCoinHistoryCallbacks {
    public void onodifies(String url);
}

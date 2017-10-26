package milanroxe.inc.snocoins.bitcoin;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Vector;

import milanroxe.inc.snocoins.R;

/**
 * Created by milan sharma on 22-06-2017.
 */

public class BitCoinHistoryAdapter extends ArrayAdapter<BitCoinTransactionModel> {

    private static final String TAG = "BitCoinHistory";

    public BitCoinHistoryAdapter(Context context, Vector<BitCoinTransactionModel> transactions) {
        super(context, R.layout.cust_row, transactions);
        //Toast.makeText(context,transactions.toString(),Toast.LENGTH_SHORT).show();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {


        View view=LayoutInflater.from(getContext()).inflate(R.layout.cust_bitcoin_row,null);

        TextView name = (TextView) view.findViewById(R.id.name);
        TextView phone = (TextView) view.findViewById(R.id.phone);
        TextView type_of_trans = (TextView) view.findViewById(R.id.type_of_transaction);
        ImageView im=(ImageView) view.findViewById(R.id.imageView2);

        final BitCoinTransactionModel b=getItem(position);

        phone.setText(b.getAmount());
        name.setText(b.getDate());
        type_of_trans.setText(b.getType_of_deposit().substring(0,b.getType_of_deposit().length()-1));

        try {
            if (b.getImage_link().equals("sucess")) {

                Log.e("His", "------In sucess");
                im.setImageDrawable(getDrawable(R.drawable.ic_payment_completed));

                if (b.getAdded_to_bal() != null && b.getAdded_to_bal().equals("no")) {
                    FireBaseDBRef fb = new FireBaseDBRef();
                    final DatabaseReference dbref = fb.getDatabaseReference();
                    // SharedPreferences sharedPreferences = fb.getSharedPreferences("milanroxe.inc.snocoins",Context.MODE_PRIVATE);
                    Log.e(TAG,"B.get Key:- "+b.getKey());



                    //String rs_bal = sharedPreferences.getString("rsbalance", "");
                    dbref.child("rsbalance").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.getValue() != null && b.getAdded_to_bal() != null && b.getAdded_to_bal().equals("no") ) {
                                Log.e(TAG, "Datasnapshot:- " + dataSnapshot.getValue().toString());
                                String rs_bal = dataSnapshot.getValue().toString();

                                if (!rs_bal.equals("")) {
                                    Log.e(TAG, "bal from sp:- " + rs_bal);
                                    Long amt = Long.parseLong(rs_bal) + Long.parseLong(b.getAmount());
                                    Log.e(TAG, "bal from amt:- " + amt);
                                    //sharedPreferences.edit().putString("rsbalance", String.valueOf(amt)).commit();
                                    dbref.child("bitcoin_transaction").child(b.getKey()).child("added_to_bal").setValue("yes");
                                    b.setAdded_to_bal("yes");
                                    dbref.child("rsbalance").setValue(amt);
                                }
                            } else
                                Log.e(TAG, "DataSnapshot error");

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }



            } else if (b.getImage_link().equals("cross")) {
                Log.e("His", "------In cros");
                im.setImageDrawable(getDrawable(R.drawable.ic_payment_cancel));
            }
        }catch (Exception e)
        {
            Log.e(TAG,"Ex:- "+e.getMessage());
            e.printStackTrace();
        }

        return view;

    }

    private Drawable getDrawable(int i) {
        return getContext().getDrawable(i);
    }

}

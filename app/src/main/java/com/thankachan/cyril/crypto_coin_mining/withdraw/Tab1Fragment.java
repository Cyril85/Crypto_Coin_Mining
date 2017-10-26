package milanroxe.inc.snocoins.withdraw;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import milanroxe.inc.snocoins.R;


public class Tab1Fragment extends Fragment {
    private static final String TAG = "Tab1Fragment";


    private ListView listView;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth firebaseAuth;
    private HistoryAdapter historyAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.tab1_fragment,container,false);

        listView=(ListView) view.findViewById(R.id.list);


        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        mDatabaseReference.child(user.getUid()).child("transaction").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    Map<String, TransactionModel> data = (Map<String, TransactionModel>) dataSnapshot.getValue();
                    if (data != null) {

                        Vector<TransactionModel> v = new Vector<TransactionModel>();
                        for (Map.Entry m : data.entrySet()) {
                            //Log.e(TAG,"--"+m.getKey()+"--"+m.getValue());

                            //Log.e(TAG,"in json");
                            //JsonParser j=new JsonParser();
                            //JsonElement jsonElement=j.parse(m.getValue().toString());

                    /* JSONObject jsonObject=new JSONObject(jsonElement.toString());*/

                            String s[] = m.getValue().toString().split(",");

                            //Log.e(TAG, Arrays.toString(s));
                            TransactionModel t = new TransactionModel();

                            String st[] = s[0].split("=");
                            //Log.e(TAG, Arrays.toString(st));
                            t.setDebited_coins(Integer.parseInt(st[1].toString()));


                            st = s[1].split("=");
                            String st1[] = s[2].split("=");
                            Log.e(TAG, Arrays.toString(st));
                            t.setDate(st[1].toString() + st1[0].toString());
                            st = s[3].split("=");
                            Log.e(TAG, Arrays.toString(st));
                            t.setImagelink(st[1]);

                            Log.e("Tag1 frag", t.toString());
                            v.add(t);
                        }
                        historyAdapter = new HistoryAdapter(getContext(), v);
                        listView.setAdapter(historyAdapter);
                    } else { Log.e(TAG,"---in else-- no data");
                        AlertDialog alertDialog=new AlertDialog.Builder(getActivity()).create();
                        alertDialog.setTitle("No History");
                        alertDialog.setMessage("Please Withdraw the coins to get history");
                        alertDialog.show();

                    }
                }
                else
                {
                    Log.e(TAG,"---in else-- no child");
                    AlertDialog alertDialog=new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("No History");
                    alertDialog.setMessage("Please Withdraw the coins to get history");
                    alertDialog.show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }



    public static class HistoryEventListener implements ChildEventListener
    {
        private HistoryCallbacks callbacks;
        HistoryEventListener(HistoryCallbacks callbacks){
            this.callbacks=callbacks;
        }

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            Log.e(TAG,"----------On chan");
            HashMap<String,String> h=(HashMap<String,String>)dataSnapshot.getValue();
            Log.e(TAG,h.toString());
            for(Map.Entry m:h.entrySet()) {
                Log.e(TAG, "--" + m.getKey() + "--" + m.getValue());

                Log.e(TAG, "in json");
                //JsonParser j=new JsonParser();
                //JsonElement jsonElement=j.parse(m.getValue().toString());

                    /* JSONObject jsonObject=new JSONObject(jsonElement.toString());*/

                String str[]= m.getValue().toString().split(",");

                Log.e(TAG, Arrays.toString(str));
                TransactionModel t = new TransactionModel();

                String st[] = str[0].split("=");
                Log.e(TAG, Arrays.toString(st));
                t.setDebited_coins(Integer.parseInt(st[1].toString()));

                Log.e("Tag frd", st[1].toString());

                st = str[1].split("=");
                String st1[] = str[2].split("=");
                Log.e(TAG, Arrays.toString(st));
                t.setDate(st[1].toString() + st1[0].toString());
                st = str[3].split("=");
                Log.e(TAG, Arrays.toString(st));
                t.setImagelink(st[1]);

                Log.e("Tag1 frag", t.toString());
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
    public interface HistoryCallbacks{
        public void onodifies(String url);
    }

}

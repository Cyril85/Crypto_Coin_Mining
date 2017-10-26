package milanroxe.inc.snocoins.bitcoin;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import milanroxe.inc.snocoins.R;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by milan sharma on 20-06-2017.
 */

public class BankReferenceAlerts extends DialogFragment {
    LayoutInflater layoutInflater;
    View view;
    private final static String TAG="In Ref alert";
    public String bit_amt=null;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabaseReference;
    OkHttpClient mClient = new OkHttpClient();


    @Override
    public Dialog onCreateDialog(Bundle savedInstance)
    {
        layoutInflater = getActivity().getLayoutInflater();
        view = layoutInflater.inflate(R.layout.alert_bank_reference,null);

        Log.e(TAG,"Bank ref called");
        final AlertDialog.Builder alertDialog=new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Enter Bank Refrence Number");

        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final TextView textView=(TextView) view.findViewById(R.id.reference_textBox);
                String ref_no = textView.getText().toString();
                Log.e(TAG,"OK button clicked");
                Log.e(TAG,"ref no:-"+ref_no);
                Log.e(TAG,bit_amt);


                firebaseAuth = FirebaseAuth.getInstance();
                mDatabaseReference = FirebaseDatabase.getInstance().getReference();

                mDatabaseReference = mDatabaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("bitcoin_transaction");
                //Toast.makeText(getApplicationContext(),"Clicked on button Ok",Toast.LENGTH_SHORT).show();

                //date,refereal_id

                HashMap<String,String> h=new HashMap<String, String>();
                h.put("date", DateFormat.getDateTimeInstance().format(new Date()));
                h.put("amount",bit_amt);
                h.put("ref_no",ref_no);
                h.put("image_link","pending");
                h.put("added_to_bal","no");
                h.put("type_of_deposit","Deposit");
                DatabaseReference reference= mDatabaseReference.push();
                reference .setValue(h);
              Log.d("mpreference",  reference.getKey());


                sendMessage("/topics/admin","deposit","Message","Http:\\google.com", firebaseAuth.getCurrentUser().getUid(),reference.getKey());


                AlertDialog al=new AlertDialog.Builder(getActivity()).create();
                al.setTitle("Entered Reference Number");
                al.setMessage(ref_no+"\n Your deposit is pending.. Once your deposit is success amount will be added to balance ");
                al.show();


            }
        });
        alertDialog.setNegativeButton("Cancel",null);
        alertDialog.setView(view);
        return alertDialog.create();

    }
    public void sendMessage(final String recipients, final String title, final String body, final String icon, final String message, final String tranid) {

        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    JSONObject root = new JSONObject();
                    JSONObject notification = new JSONObject();
                    notification.put("body", body);
                    notification.put("title", title);
                    notification.put("icon", icon);

                    JSONObject data = new JSONObject();
                    data.put("message", message);
                    data.put("deposite", tranid);
                    data.put("click_action", "PaymentApprove");

                    root.put("notification", notification);
                    root.put("data", data);

                    root.put("to", recipients);
                    //  root.put("registration_ids", recipients);e

                    String result = postToFCM(root.toString());
                    Log.d("Main Activity", "Result: " + result);
                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                try {
                    Log.d("resultji",result);
                    JSONObject resultJson = new JSONObject(result);
                    int success, failure;
                    success = resultJson.getInt("message_id");
                    failure = resultJson.getInt("failure");
                    //  Toast.makeText(SplashAct.this, "Message Success: " + success + "Message Failed: " + failure, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    //     Toast.makeText(SplashAct.this, "Message Failed, Unknown error occurred.", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    String postToFCM(String bodyString) throws IOException {



        final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
        final MediaType JSON  = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, bodyString);
        Request request = new Request.Builder()
                .url(FCM_MESSAGE_URL)
                .post(body)
                .addHeader("Authorization", "key=" + "AAAA0s_XeXk:APA91bGSZ67AQIon9gOyI55URXcOfkN5biL6i6BdQofGV0JSCgtJCYemTnzWa4WwDg4_92s9rLAONgFpleh8UFCuXhIhfqzcC150SPkePW9C-w-ITReFPR4m3FOud44G1cXL4u-qlGBT")
                .build();
        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }
}



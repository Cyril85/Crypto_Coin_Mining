package milanroxe.inc.snocoins.bitcoin;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import milanroxe.inc.snocoins.R;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class withdraw2Activity extends AppCompatActivity {

    private DatabaseReference fireBaseDBRef;
    private SharedPreferences sharedPreferences;
    private TextView bal,withdraw_account_no;
    private EditText editText;
    private Button withdraw;
    private Long balance;
    private FirebaseAuth firebaseAuth;
    OkHttpClient mClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw2);

        bal=(TextView) findViewById(R.id.textView18);
        withdraw=(Button) findViewById(R.id.withdraw);
        firebaseAuth = FirebaseAuth.getInstance();
        editText = (EditText) findViewById(R.id.editText2);
        withdraw_account_no = (TextView) findViewById(R.id.withdraw_account_no);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String ac_no=sharedPreferences.getString("withdraw_account_no","");
        if(!ac_no.equals(""))
            withdraw_account_no.setText(ac_no);

        String am;
        if(!(am=sharedPreferences.getString("rsbalance","")).equals("")) {
            bal.setText(am);

        }
        balance = Long.parseLong(am);


        fireBaseDBRef=new FireBaseDBRef().getDatabaseReference();

        withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(editText.getText().toString().length() > 0) {
                    Long enteredAmount = Long.parseLong(editText.getText().toString());

                    if (enteredAmount < balance) {
                        if (enteredAmount > 2999) {
                            if (enteredAmount < 4999) {


                                HashMap<String, String> h = new HashMap<String, String>();
                                h.put("date", DateFormat.getDateTimeInstance().format(new Date()));
                                h.put("amount", String.valueOf(enteredAmount));
                                h.put("image_link", "pending");
                                h.put("added_to_bal", "yes");
                                h.put("ref_no", "No ref number for deposit");
                                h.put("type_of_deposit", "WithDraw");
                                DatabaseReference reference=  fireBaseDBRef.child("bitcoin_transaction").push();
                                reference .setValue(h);
                                Log.d("mpreference",  reference.getKey());
                                sendMessage("/topics/admin","withdraw","Message","Http:\\google.com", firebaseAuth.getCurrentUser().getUid(),reference.getKey());

                                Long ref_bal = (balance - enteredAmount);

                                bal.setText(String.valueOf(ref_bal));
                                sharedPreferences.edit().putString("rsbalance", String.valueOf(ref_bal));
                                fireBaseDBRef.child("rsbalance").setValue(ref_bal);
                                SweetAlertDialog pDialog = new SweetAlertDialog(withdraw2Activity.this, SweetAlertDialog.SUCCESS_TYPE);
                                pDialog.getProgressHelper().setBarColor(R.color.colorPrimary);
                                pDialog.setTitleText("Withdraw Sucessfully");
                                pDialog.setCancelable(false);
                                pDialog.show();

                            } else {
                                SweetAlertDialog pDialog = new SweetAlertDialog(withdraw2Activity.this, SweetAlertDialog.ERROR_TYPE);
                                pDialog.getProgressHelper().setBarColor(R.color.colorPrimary);
                                pDialog.setTitleText("Oops...");
                                pDialog.setContentText("Max WithDraw Amount should be less than 5000");
                                pDialog.setCancelable(false);
                                pDialog.show();


                            }

                        } else {
                            SweetAlertDialog pDialog = new SweetAlertDialog(withdraw2Activity.this, SweetAlertDialog.ERROR_TYPE);
                            pDialog.getProgressHelper().setBarColor(R.color.colorPrimary);
                            pDialog.setTitleText("Oops...");
                            pDialog.setContentText("Min. WithDraw Amount should be greater than 3000");
                            pDialog.setCancelable(false);
                            pDialog.show();

                        }
                    } else {
                        SweetAlertDialog pDialog = new SweetAlertDialog(withdraw2Activity.this, SweetAlertDialog.WARNING_TYPE);
                        pDialog.getProgressHelper().setBarColor(R.color.colorPrimary);
                        pDialog.setTitleText("Entered Amount should be less than Balance");
                        pDialog.setCancelable(false);
                        pDialog.show();

                    }

                }else {
                    SweetAlertDialog pDialog = new SweetAlertDialog(withdraw2Activity.this, SweetAlertDialog.ERROR_TYPE);
                    pDialog.getProgressHelper().setBarColor(R.color.colorPrimary);
                    pDialog.setTitleText("Oops...");
                    pDialog.setContentText("Enter Amount to withdraw");
                    pDialog.setCancelable(false);
                    pDialog.show();

                }

            }
        });




    }

    private void getToast(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
        editText.setText("");
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
                    //  root.put("registration_ids", recipients);

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


package milanroxe.inc.snocoins;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import milanroxe.inc.snocoins.bitcoin.FireBaseDBRef;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BANKANDPANActivity extends AppCompatActivity {

    private EditText fullName, pancard, _dob, _gender, ifsc, bank_name, account_holder_name, account_num;
    private Button upload;
    private String full_name, pancard_no, dob, gender, ifsc_code, bankname, acc_holder_name, acc_num;
    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;
    private ImageView pancard_imageview;
    private StorageReference mStorageRef;
    private static final int PICK_IMAGE = 1;
    private ProgressDialog progressDialog;
    private static final String FIREBASE_STORAGE_DIRECTORY = "\"Pancards\"";
    private boolean verifyied;
    int uid;
    public final String KEY_FULL_NAME = "Full Name";
    public final String KEY_PANCARD_NO = "Pancard No";
    public final String KEY_DOB = "Date Of birth";
    public final String KEY_GENDER = "Gender";
    public final String KEY_IFSC = "IFSC code";
    public final String KEY_BANK_NAME = "Bank Name";
    public final String KEY_ACCOUNT_NO = "Account Number";
    public final String KEY_ACCOUNT_HOLDER_NAME = "Account Holder Name";

    OkHttpClient mClient = new OkHttpClient();
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();


        databaseReference = new FireBaseDBRef().getDatabaseReference();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        verifyied = sharedPreferences.getBoolean("verified", false);
        databaseReference.child("verification").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    if (dataSnapshot.getValue().toString().equals("yes")) {
                        sharedPreferences.edit().putBoolean("verified", true).commit();
                        Log.e("In verification", "-->>>Verified");
                        verifyied = true; Intent intent;
                        // Create Notification

                    } else if (dataSnapshot.getValue().toString().equals("no")) {
                        sharedPreferences.edit().putBoolean("verified", false).commit();
                        Log.e("In verification", "-->>>Not Verified");
                        verifyied = false;
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (!verifyied)
            setContentView(R.layout.activity_bankandpan);

        else
            setContentView(R.layout.details_verfied);


        pancard_imageview = (ImageView) findViewById(R.id.imageView8);
        fullName = (EditText) findViewById(R.id.editText);
        pancard = (EditText) findViewById(R.id.editText3);
        _dob = (EditText) findViewById(R.id.editText4);
        _gender = (EditText) findViewById(R.id.editText5);
        ifsc = (EditText) findViewById(R.id.editText6);
        bank_name = (EditText) findViewById(R.id.editText7);
        account_holder_name = (EditText) findViewById(R.id.editText8);
        account_num = (EditText) findViewById(R.id.editText9);
        upload = (Button) findViewById(R.id.button10);


        mStorageRef = FirebaseStorage.getInstance().getReference();


        progressDialog = new ProgressDialog(this);

        if (!verifyied) {
            //image upload
            pancard_imageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    getIntent.setType("image/*");

                    Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickIntent.setType("image/*");

                    Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

                    startActivityForResult(chooserIntent, PICK_IMAGE);


                }
            });


            upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    boolean verified = true;

                    full_name = getStringForEditText(fullName);
                    pancard_no = getStringForEditText(pancard);
                    dob = getStringForEditText(_dob);
                    gender = getStringForEditText(_gender);
                    ifsc_code = getStringForEditText(ifsc);
                    bankname = getStringForEditText(bank_name);
                    acc_holder_name = getStringForEditText(account_holder_name);
                    acc_num = getStringForEditText(account_num);


                    //checking if fields are empty
                    if (TextUtils.isEmpty(full_name)) {
                        showToast("Name");
                        verified = false;
                    }
                    if (TextUtils.isEmpty(acc_num)) {
                        showToast("Account Number");
                        verified = false;

                    }
                    if (TextUtils.isEmpty(acc_holder_name)) {
                        showToast("Account Holder Name");
                        verified = false;

                    }
                    if (TextUtils.isEmpty(bankname)) {
                        showToast("Bank Name");
                        verified = false;

                    }
                    if (TextUtils.isEmpty(gender)) {
                        showToast("Gender");
                        verified = false;

                    }
                    if (TextUtils.isEmpty(dob)) {
                        showToast("Date Of Birth");
                        verified = false;

                    }
                    if (TextUtils.isEmpty(pancard_no)) {
                        showToast("Pan Card Number");
                        verified = false;
                    }
                    if (TextUtils.isEmpty(ifsc_code)) {
                        showToast("IFSC Code");
                        verified = false;
                    }

                    if (verified) {
                        storeInDB();
                    }

                    initViews();


                }
            });
        }


    }

    private void storeInDB() {

        HashMap<String, String> pancard_details = new HashMap<>();
        HashMap<String, String> bank_details = new HashMap<>();

        pancard_details.put("Full Name", full_name);
        pancard_details.put("Pancard No", pancard_no);
        pancard_details.put("Date Of birth", dob);
        pancard_details.put("Gender", gender);

        bank_details.put("IFSC code", ifsc_code);
        bank_details.put("Bank Name", bankname);
        bank_details.put("Account Number", acc_num);
        bank_details.put("Account Holder Name", acc_holder_name);
        sharedPreferences.edit().putString(KEY_FULL_NAME, full_name).commit();
        sharedPreferences.edit().putString(KEY_PANCARD_NO, pancard_no).commit();
        sharedPreferences.edit().putString(KEY_DOB, dob).commit();
        sharedPreferences.edit().putString(KEY_GENDER, gender).commit();

        sharedPreferences.edit().putString(KEY_IFSC, ifsc_code).commit();
        sharedPreferences.edit().putString(KEY_BANK_NAME, bankname).commit();
        sharedPreferences.edit().putString(KEY_ACCOUNT_NO, acc_num).commit();
        sharedPreferences.edit().putString(KEY_ACCOUNT_HOLDER_NAME, acc_holder_name).commit();


        databaseReference.child("bank_details").setValue(bank_details);
        databaseReference.child("pancard_details").setValue(pancard_details);
        databaseReference.child("verification").setValue("no");

        sharedPreferences.edit().putBoolean("upload_details", true).commit();
        sharedPreferences.edit().putBoolean("verified", false).commit();
        sharedPreferences.edit().putString("withdraw_account_no", acc_num).commit();

        Log.d("UIDGET",firebaseAuth.getCurrentUser().getUid());
        sendMessage("/topics/admin","VERIFICATION","Message","Http:\\google.com", firebaseAuth.getCurrentUser().getUid());

        recreate();

        showToast("Uploaded");
        initViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            progressDialog.setMessage("Uploading......");
            progressDialog.show();
            Uri uri = data.getData();

            StorageReference storageReference = mStorageRef.child(FIREBASE_STORAGE_DIRECTORY).child(new FireBaseDBRef().getFirebaseAuthOfCurrentUser() + ".jpg");

            //On Upload Success
            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    progressDialog.dismiss();

                    //Showing uploaded image to User
                    @SuppressWarnings("VisibleForTests") Uri download = taskSnapshot.getDownloadUrl();

                    showInImageeView(download);
                    Toast.makeText(getApplicationContext(), "Upload finshed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showToast(String s) {
        Toast.makeText(this, "its " + s, Toast.LENGTH_LONG).show();
    }

    private void showInImageeView(Uri imageUri) {
        Picasso.with(BANKANDPANActivity.this).load(imageUri).fit().centerCrop().into(pancard_imageview);
    }

    private String getStringForEditText(EditText editText) {
        return editText.getText().toString();
    }


    private void initViews() {

        /* **** LOAD DATE FROM SHARED PREFERENCE **** */

        fullName.setText(sharedPreferences.getString(KEY_FULL_NAME, "N/A"));
        pancard.setText(sharedPreferences.getString(KEY_PANCARD_NO, "N/A"));
        _dob.setText(sharedPreferences.getString(KEY_DOB, "N/A"));
        _gender.setText(sharedPreferences.getString(KEY_GENDER, "N/A"));

        ifsc.setText(sharedPreferences.getString(KEY_IFSC, "N/A"));
        bank_name.setText(sharedPreferences.getString(KEY_BANK_NAME, "N/A"));
        account_holder_name.setText(sharedPreferences.getString(KEY_ACCOUNT_HOLDER_NAME, "N/A"));
        account_num.setText(sharedPreferences.getString(KEY_ACCOUNT_NO, "N/A"));

    }
    public void sendMessage(final String recipients, final String title, final String body, final String icon, final String message) {

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

                    data.put("click_action", "MainActivity");
                    data.put("UID", "");

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

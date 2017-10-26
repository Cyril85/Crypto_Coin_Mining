package milanroxe.inc.snocoins;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class ViewBankDetailsActivity extends Activity {

    ImageView ivEditInfo;
    EditText etFullName, etPanCardNo, etDateOfBirth, etGender, etIfsc, etBankName, etAccHolderName, etAccountNo;
    private SharedPreferences sharedPreferences;

    public final String KEY_FULL_NAME = "Full Name";
    public final String KEY_PANCARD_NO = "Pancard No";
    public final String KEY_DOB = "Date Of birth";
    public final String KEY_GENDER = "Gender";
    public final String KEY_IFSC = "IFSC code";
    public final String KEY_BANK_NAME = "Bank Name";
    public final String KEY_ACCOUNT_NO = "Account Number";
    public final String KEY_ACCOUNT_HOLDER_NAME = "Account Holder Name";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //if (sharedPreferences.getBoolean("upload_details", false)) {
        setContentView(R.layout.activity_view_bank_details);
       /* } else {
            setContentView(R.layout.activity_bankandpan);
        }*/

        initViews();

        ivEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.activity_bankandpan);
            }
        });
    }

    private void initViews() {
        ivEditInfo = (ImageView) findViewById(R.id.ivEditInfo);

        etFullName = (EditText) findViewById(R.id.etFullName);
        etPanCardNo = (EditText) findViewById(R.id.etPanCardNo);
        etDateOfBirth = (EditText) findViewById(R.id.etDateOfBirth);
        etGender = (EditText) findViewById(R.id.etGender);

        etIfsc = (EditText) findViewById(R.id.etIfsc);
        etBankName = (EditText) findViewById(R.id.etBankName);
        etAccHolderName = (EditText) findViewById(R.id.etAccHolderName);
        etAccountNo = (EditText) findViewById(R.id.etAccountNo);

        /* **** LOAD DATE FROM SHARED PREFERENCE **** */

        etFullName.setText(sharedPreferences.getString(KEY_FULL_NAME, "N/A"));
        etPanCardNo.setText(sharedPreferences.getString(KEY_PANCARD_NO, "N/A"));
        etDateOfBirth.setText(sharedPreferences.getString(KEY_DOB, "N/A"));
        etGender.setText(sharedPreferences.getString(KEY_GENDER, "N/A"));

        etIfsc.setText(sharedPreferences.getString(KEY_IFSC, "N/A"));
        etBankName.setText(sharedPreferences.getString(KEY_BANK_NAME, "N/A"));
        etAccHolderName.setText(sharedPreferences.getString(KEY_ACCOUNT_HOLDER_NAME, "N/A"));
        etAccountNo.setText(sharedPreferences.getString(KEY_ACCOUNT_NO, "N/A"));
    }
}

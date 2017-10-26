package milanroxe.inc.snocoins;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

public class AlertUnlockFreeRidesActivity extends AppCompatActivity {

    ImageView ivFacebook, ivEmail, ivSms;
    LinearLayout llEmail;
    ImageView ivClose;
    String urlToShare = "https://play.google.com/store/apps/details?id=milanroxe.inc.snocoins";

    private View.OnClickListener mEmailListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

            String aEmailList[] = {"" };
            String aEmailCCList[] = { ""};
            String aEmailBCCList[] = { "" };

            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, aEmailList);
            emailIntent.putExtra(android.content.Intent.EXTRA_CC, aEmailCCList);
            emailIntent.putExtra(android.content.Intent.EXTRA_BCC, aEmailBCCList);
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Invitation");
            emailIntent.setType("plain/text");
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=milanroxe.inc.snocoins");
            startActivity(emailIntent);
        }
    };
    private View.OnClickListener mFaceBookListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            // intent.putExtra(Intent.EXTRA_SUBJECT, "Foo bar"); // NB: has no effect!
            intent.putExtra(Intent.EXTRA_TEXT, urlToShare);
            // See if official Facebook app is found
            boolean facebookAppFound = false;
            List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
            for (ResolveInfo info : matches) {
                if (info.activityInfo.packageName.toLowerCase().startsWith("com.facebook.katana")) {
                    intent.setPackage(info.activityInfo.packageName);
                    facebookAppFound = true;
                    break;
                }
            }
            // As fallback, launch sharer.php in a browser
            if (!facebookAppFound) {
                String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + urlToShare;
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
            }
            startActivity(intent);
        }
    };

    private View.OnClickListener mSmsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.putExtra("address", "");
            smsIntent.putExtra("sms_body", "https://play.google.com/store/apps/details?id=milanroxe.inc.snocoins");
            startActivity(smsIntent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.activity_alert_unlock_free_rides);
        ivFacebook = (ImageView) findViewById(R.id.ivFacebook);
        ivEmail = (ImageView) findViewById(R.id.ivEmail);
        ivSms = (ImageView) findViewById(R.id.ivSms);
        ivClose = (ImageView) findViewById(R.id.ivClose);
        llEmail = (LinearLayout) findViewById(R.id.llEmail);
        ivFacebook.setOnClickListener(mFaceBookListener);
        ivEmail.setOnClickListener(mEmailListener);
        llEmail.setOnClickListener(mEmailListener);
        ivSms.setOnClickListener(mSmsListener);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
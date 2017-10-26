package milanroxe.inc.snocoins;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import milanroxe.inc.snocoins.interfaces.FingerprintCallback;


public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private CancellationSignal cancellationSignal;
    private Context context;
    FingerprintCallback mFingerCallback;

    public FingerprintHandler(Context mContext, FingerprintCallback mFingerCallback) {
        context = mContext;
        this.mFingerCallback =mFingerCallback;
    }

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errMsgId,
                                      CharSequence errString) {
        Toast.makeText(context,
                "Authentication error\n" + errString,
                Toast.LENGTH_LONG).show();

        FingerprintLoginActivity.iCountLoginTime++;
        if(FingerprintLoginActivity.iCountLoginTime >= 3){
            mFingerCallback.failedAuth();
        }
    }

    @Override
    public void onAuthenticationFailed() {
        Toast.makeText(context,
                "Authentication failed",
                Toast.LENGTH_LONG).show();

        FingerprintLoginActivity.iCountLoginTime++;

        if(FingerprintLoginActivity.iCountLoginTime >= 3){
            mFingerCallback.failedAuth();
        }
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId,
                                     CharSequence helpString) {
        Toast.makeText(context,
                "Authentication help\n" + helpString,
                Toast.LENGTH_LONG).show();

    }


    @Override
    public void onAuthenticationSucceeded(
            FingerprintManager.AuthenticationResult result) {

        Toast.makeText(context,
                "Success!",
                Toast.LENGTH_LONG).show();

        FingerprintLoginActivity.iCountLoginTime = 0;

            mFingerCallback.successAuth();

    }


}

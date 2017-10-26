
package milanroxe.inc.snocoins;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import butterknife.Bind;
import butterknife.ButterKnife;
import milanroxe.inc.snocoins.interfaces.FingerprintCallback;

import static milanroxe.inc.snocoins.utils.CommonDailogs.showAlertMessage;

public class FingerprintLoginActivity extends AppCompatActivity implements FingerprintCallback {

    @Bind(R.id.etPassword1)
    EditText etPassword1;

    @Bind(R.id.etPassword2)
    EditText etPassword2;

    @Bind(R.id.etPassword3)
    EditText etPassword3;

    @Bind(R.id.etPassword4)
    EditText etPassword4;

    @Bind(R.id.tvTypePassword)
    TextView tvTypePassword;

    @Bind(R.id.btDone)
    Button btDone;

    KeyguardManager keyguardManager;
    FingerprintManager fingerprintManager;

    InputMethodManager imm;

    private KeyStore keyStore;
    // Variable used for storing the key in the Android Keystore container
    private static final String KEY_NAME = "passwordAuth";
    private Cipher cipher;

    AlertDialog alertDialog;

    private Typeface tpBariolRegular;

    private SharedPreferences sharedPreferences;

    static int iCountLoginTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFingerAuth();

        iCountLoginTime = 0;
    }

    private void initFingerAuth() {

        fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        // Check whether the device has a Fingerprint sensor.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!fingerprintManager.isHardwareDetected()) {
                /**
                 * An error message will be displayed if the device does not contain the fingerprint hardware.
                 * However if you plan to implement a default authentication method,
                 * you can redirect the user to a default authentication activity from here.
                 * Example:
                 * Intent intent = new Intent(this, DefaultAuthenticationActivity.class);
                 * startActivity(intent);
                 */
                //showAlertMessage(RegisterActivity.this, "Your Device does not have a Fingerprint Sensor");
                loadPinContentView();
            } else {

                // Initializing both Android Keyguard Manager and Fingerprint Manager
                keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

                // Checks whether fingerprint permission is set on manifest
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                    showAlertMessage(FingerprintLoginActivity.this, "Fingerprint authentication permission not enabled");
                    loadPinContentView();
                } else {
                    // Check whether at least one fingerprint is registered
                    if (!fingerprintManager.hasEnrolledFingerprints()) {
                        showAlertMessage(FingerprintLoginActivity.this, "Register fingerprint in Settings");
                        loadPinContentView();
                    } else {
                        // Checks whether lock screen security is enabled or not
                        if (!keyguardManager.isKeyguardSecure()) {
                            showAlertMessage(FingerprintLoginActivity.this, "Lock screen security not enabled in Settings");
                            loadPinContentView();
                        } else {
                            showFingerprintAlert();
                            generateKey();
                            if (cipherInit()) {
                                FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                                FingerprintHandler helper = new FingerprintHandler(this, this);
                                helper.startAuth(fingerprintManager, cryptoObject);
                            }
                        }
                    }
                }
            }
        }
    }

    private void loadPinContentView() {
        setContentView(R.layout.activity_fingerprint_login);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etPassword1, 0);

        ButterKnife.bind(FingerprintLoginActivity.this);

        tpBariolRegular = Typeface.createFromAsset(getResources().getAssets(), "fonts/Bariol-Regular.ttf");
        tvTypePassword.setTypeface(tpBariolRegular);

        // initFingerAuth();
        etPassword1.addTextChangedListener(new GenericTextWatcher(etPassword1));
        etPassword2.addTextChangedListener(new GenericTextWatcher(etPassword2));
        etPassword3.addTextChangedListener(new GenericTextWatcher(etPassword3));
        etPassword4.addTextChangedListener(new GenericTextWatcher(etPassword4));

        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validateData();
            }
        });
    }

    @Override
    public void failedAuth() {
        loadPinContentView();
    }

    @Override
    public void successAuth() {
        onBackPressed();
    }

    //Declaration
    private class GenericTextWatcher implements TextWatcher {

        private View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            switch (view.getId()) {
                case R.id.etPassword1:
                    etPassword2.requestFocus();
                    imm.showSoftInput(etPassword2, 0);
                    break;
                case R.id.etPassword2:
                    etPassword3.requestFocus();
                    imm.showSoftInput(etPassword3, 0);
                    break;
                case R.id.etPassword3:
                    etPassword4.requestFocus();
                    imm.showSoftInput(etPassword4, 0);
                    break;

                case R.id.etPassword4:
                    imm.hideSoftInputFromWindow(etPassword4.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                    break;
            }
        }
    }

    private void showFingerprintAlert() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_alert_fingerprint, null);
        dialogBuilder.setView(dialogView);

        dialogBuilder.setCancelable(false);

        // final TextView tvDesc = (TextView) dialogView.findViewById(R.id.tvDesc);
        final TextView tvCancel = (TextView) dialogView.findViewById(R.id.tvCancel);
        tvCancel.setVisibility(View.GONE);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void validateData() {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(FingerprintLoginActivity.this);
        String strPassword = sharedPreferences.getString("password", "");

        String strPasswordEntered = etPassword1.getText().toString() + etPassword2.getText().toString()
                + etPassword3.getText().toString() + etPassword4.getText().toString();

        if (strPassword.equals(strPasswordEntered)) {
            finish();
            // initFingerAuth();
        } else {
            showAlertMessage(FingerprintLoginActivity.this, "Password didn't match. Please try again!");
        }
    }


    /***
     * FINGERPRINT AUTH
     ****/

    @TargetApi(Build.VERSION_CODES.M)
    protected void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }

        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get KeyGenerator instance", e);
        }

        try {
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException |
                InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else
            new AlertDialog.Builder(this)
                    .setTitle("Sno Coins")
                    .setIcon(R.drawable.mils)
                    .setMessage("Are you sure you want to exit Sno Coins?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            FingerprintLoginActivity.this.finish();
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
    }
}

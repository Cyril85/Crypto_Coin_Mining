package milanroxe.inc.snocoins;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

/**
 * Created by user on 8/8/17.
 */

public class CreatePin extends AppCompatActivity implements FingerprintCallback {

    @Bind(R.id.etPassword1)
    EditText etPassword1;

    @Bind(R.id.etPassword2)
    EditText etPassword2;

    @Bind(R.id.etPassword3)
    EditText etPassword3;

    @Bind(R.id.etPassword4)
    EditText etPassword4;

    @Bind(R.id.etPasswordNew1)
    EditText etPasswordNew1;

    @Bind(R.id.etPasswordNew2)
    EditText etPasswordNew2;

    @Bind(R.id.etPasswordNew3)
    EditText etPasswordNew3;

    @Bind(R.id.etPasswordNew4)
    EditText etPasswordNew4;

    @Bind(R.id.btDone)
    Button btDone;

    @Bind(R.id.tvRetypePassword)
    TextView tvRetypePassword;

    @Bind(R.id.tvTypePassword)
    TextView tvTypePassword;

    InputMethodManager imm;
    AlertDialog alertDialog;
    AlertDialog.Builder builder;

    KeyguardManager keyguardManager;
    FingerprintManager fingerprintManager;

    private KeyStore keyStore;
    // Variable used for storing the key in the Android Keystore container
    private static final String KEY_NAME = "passwordAuth";
    private Cipher cipher;

    private Typeface tpBariolRegular;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(CreatePin.this);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etPassword1, 0);

        tpBariolRegular = Typeface.createFromAsset(getResources().getAssets(), "fonts/Bariol-Regular.ttf");
        tvTypePassword.setTypeface(tpBariolRegular);
        tvRetypePassword.setTypeface(tpBariolRegular);

        // initFingerAuth();
        etPassword1.addTextChangedListener(new GenericTextWatcher(etPassword1));
        etPassword2.addTextChangedListener(new GenericTextWatcher(etPassword2));
        etPassword3.addTextChangedListener(new GenericTextWatcher(etPassword3));
        etPassword4.addTextChangedListener(new GenericTextWatcher(etPassword4));

        //Retry password listeners
        etPasswordNew1.addTextChangedListener(new GenericTextWatcher(etPasswordNew1));
        etPasswordNew2.addTextChangedListener(new GenericTextWatcher(etPasswordNew2));
        etPasswordNew3.addTextChangedListener(new GenericTextWatcher(etPasswordNew3));
        etPasswordNew4.addTextChangedListener(new GenericTextWatcher(etPasswordNew4));

        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validateData();
            }
        });
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
                finish();
            } else {

                // Initializing both Android Keyguard Manager and Fingerprint Manager
                keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

                // Checks whether fingerprint permission is set on manifest
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                    showAlertMessage(CreatePin.this, "Fingerprint authentication permission not enabled");
                } else {
                    // Check whether at least one fingerprint is registered
                    if (!fingerprintManager.hasEnrolledFingerprints()) {
                        showAlertMessage(CreatePin.this, "Register fingerprint in Settings");
                    } else {
                        // Checks whether lock screen security is enabled or not
                        if (!keyguardManager.isKeyguardSecure()) {
                            showAlertMessage(CreatePin.this, "Lock screen security not enabled in Settings");
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

    @Override
    public void failedAuth() {

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

                case R.id.etPasswordNew1:
                    etPasswordNew2.requestFocus();
                    imm.showSoftInput(etPasswordNew2, 0);
                    break;
                case R.id.etPasswordNew2:
                    etPasswordNew3.requestFocus();
                    imm.showSoftInput(etPasswordNew3, 0);
                    break;
                case R.id.etPasswordNew3:
                    etPasswordNew4.requestFocus();
                    imm.showSoftInput(etPasswordNew4, 0);
                    break;

                case R.id.etPasswordNew4:
                    imm.hideSoftInputFromWindow(etPasswordNew4.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
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

        String strPassword = etPassword1.getText().toString() + etPassword2.getText().toString()
                + etPassword3.getText().toString() + etPassword4.getText().toString();

        String strRetryPassword = etPasswordNew1.getText().toString() + etPasswordNew2.getText().toString()
                + etPasswordNew3.getText().toString() + etPasswordNew4.getText().toString();

        if (strPassword.equals(strRetryPassword)) {
//            Intent intent = new Intent(CreatePin.this, LoginActivity.class);
//            startActivity(intent);

            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CreatePin.this);
            sharedPreferences.edit().putString("password", strPassword).commit();

            initFingerAuth();
        } else {
            showAlertMessage(CreatePin.this, "Please enter same 4-digit pin");
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
}

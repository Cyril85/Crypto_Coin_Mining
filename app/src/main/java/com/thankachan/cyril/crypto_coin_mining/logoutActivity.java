package milanroxe.inc.snocoins;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class logoutActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "PHONEActivity";
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        Button mSignOutButton = (Button) findViewById(R.id.sign_out_button);
        TextView fireBaseId = (TextView) findViewById(R.id.detail);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth!=null){
            fireBaseId.setText(mAuth.getCurrentUser().getPhoneNumber());
        }
        mSignOutButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_out_button:
                mAuth.signOut();
                startActivity(new Intent(logoutActivity.this, PHONEActivity.class));
                finish();
                break;
        }
    }
}

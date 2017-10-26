package com.thankachan.cyril.crypto_coin_mining;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static milanroxe.inc.snocoins.R.layout.activity_profile;

public class profileActivity extends AppCompatActivity implements View.OnClickListener {

    //firebase auth object
    private FirebaseAuth firebaseAuth;



    //view objects
    private TextView textViewUserEmail;
    private Button buttonLogout;

    //defining a database reference
    private DatabaseReference databaseReference;

    //our new views
    private EditText editTextName;
    private EditText editTextAddress;
    private TextView textView6;
    private Button buttonSave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_profile);

        //initializing firebase authentication object
        firebaseAuth = FirebaseAuth.getInstance();


        //if the user is not logged in
        //that means current user will return null
        if (firebaseAuth.getCurrentUser() == null) {
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }

        //getting the database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //getting the views from xml resource
        editTextAddress = (EditText) findViewById(R.id.editTextAddress);
        editTextName = (EditText) findViewById(R.id.editTextName);
        buttonSave = (Button) findViewById(R.id.buttonSave);
        textView6 = (TextView) findViewById(R.id.textView6);


        FirebaseUser user = firebaseAuth.getCurrentUser();

        //initializing views
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);

        //displaying logged in user name
        textViewUserEmail.setText("Welcome " + user.getEmail());

        //adding listener to button
        buttonLogout.setOnClickListener(this);
        buttonSave.setOnClickListener(this);
    }


    private void saveUserInformation() {
        //Getting values from database
        String name = editTextName.getText().toString().trim();
        String add = editTextAddress.getText().toString().trim();
        String balance = textView6.getText().toString().trim();

        //creating a userinformation object
        UserInformation userInformation = new UserInformation(name, add, balance);

        //getting the current logged in user
        FirebaseUser user = firebaseAuth.getCurrentUser();


        databaseReference.child(user.getUid()).setValue(userInformation);

        //displaying a success toast
        Toast.makeText(this, "Information Saved...", Toast.LENGTH_LONG).show();


    }

    @Override
    public void onClick(View view) {
        //if logout is pressed
        if (view == buttonLogout) {
            //logging out the user
            firebaseAuth.signOut();
            //closing activity

            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));

        }

        if (view == buttonSave) {
            saveUserInformation();

            startActivity(new Intent(this, Main2Activity.class));
        }


    }
}
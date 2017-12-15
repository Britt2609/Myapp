package com.example.britt.myapp;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private static final String TAG = "firebase";

    private DatabaseReference databaseReference;

    // Initialize user data.
    String email;
    String password;
    String id;

    Button login;
    Button useremail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set listeners on buttons for logging in and creating an account.
        login = findViewById(R.id.login);
        login.setOnClickListener(this);
        useremail = findViewById(R.id.make_account);
        useremail.setOnClickListener(this);

        // Set AuthStateListener to make sure only logged in users can go to next activity.
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signedIn" + user.getUid());
                    Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.d(TAG, "onAuthStateChanged:signedIn");
                }
            }
        };
    }

    /*
     * Add an user with initial score 0.
     */
    public void addUser(FirebaseUser user) {
        String id_user = user.getUid();
        User aUser = new User(0, password, email);
        id = auth.getCurrentUser().getUid();
        databaseReference.child("users").child(id_user).setValue(aUser);
    }

    /*
     * Add AuthStateListener.
     */
    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }


    @Override
    public void onStop() {
        super.onStop();

        if (authStateListener != null) {
            auth.removeAuthStateListener(authStateListener);
        }
    }


    public void CreateUser() {
        EditText get_email = findViewById(R.id.getEmail);
        EditText get_password = findViewById(R.id.getPassword);

        email = get_email.getText().toString();
        password = get_password.getText().toString();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = auth.getCurrentUser();
                            addUser(user);

                            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    public void LogIn() {

        EditText get_email = findViewById(R.id.getEmail);
        EditText get_password = findViewById(R.id.getPassword);

        email = get_email.getText().toString();
        password = get_password.getText().toString();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                LogIn();
                break;

            case R.id.make_account:
                CreateUser();
                break;
        }
    }
}
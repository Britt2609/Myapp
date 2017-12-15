package com.example.britt.myapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class FourthActivity extends AppCompatActivity {

    DatabaseReference databaseReference;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    String id;

    ListView scoreboard;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        id = user.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        scoreboard = findViewById(R.id.listView);

        getFromDB();

        setListener();
    }

    // Set AuthStateListener to make sure only logged in users can go to next activity.
    public void setListener() {
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    Log.d("TAG", "onAuthStateChanged:signedIn" + user.getUid());
                    Intent intent = new Intent(FourthActivity.this, SecondActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.d("TAG", "onAuthStateChanged:signedIn");
                }
            }
        };
    }

    /*
     * Get all scores from database and show in listView with corresponding user.
     */
    public void getFromDB() {
        // Read from the database.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Set an adapter to the listView.
                ArrayList<String> scoreboardArray = new ArrayList<>();
                ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_expandable_list_item_1, scoreboardArray);

                scoreboard.setAdapter(mAdapter);

                Iterator<DataSnapshot> items = dataSnapshot.child("users").getChildren().iterator();
                Log.d("TAG", "Total users: " + dataSnapshot.getChildrenCount());

                // Add all users with score to the adapter.
                while (items.hasNext()) {
                    DataSnapshot item = items.next();
                    String name = item.child("email").getValue().toString();
                    Long highscore = (Long) item.child("score").getValue();
                    String toAdd = name + "     " + highscore;

                    mAdapter.add(toAdd);
                    mAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value.
                Log.w("value failure: ", "Failed to read value.", error.toException());
            }
        });
    }

    // Intent starts when button is clicked to start a new quiz.
    public void NextQuiz(View view) {
        Intent intent = new Intent(FourthActivity.this, SecondActivity.class);
        startActivity(intent);
    }
}

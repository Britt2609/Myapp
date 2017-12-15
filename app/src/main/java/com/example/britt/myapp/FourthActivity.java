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

    public void getFromDB() {
        // Read from the database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

//
//                Map<Long, String> scoreMap = new TreeMap(Collections.reverseOrder());
//                ScoreboardArray scoreboardArray = new ScoreboardArray(getApplicationContext(), scoreMap, R.id.layout.listview_scores);
//
////
                ArrayList<String> scoreboardArray = new ArrayList<>();
                ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_expandable_list_item_1, scoreboardArray);

                scoreboard.setAdapter(mAdapter);

                Iterator<DataSnapshot> items = dataSnapshot.child("users").getChildren().iterator();
                Log.d("TAG", "Total users: " + dataSnapshot.getChildrenCount());
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
                // Failed to read value
                Log.w("value failure: ", "Failed to read value.", error.toException());
            }
        });
    }

    public void NextQuiz(View view) {
        Intent intent = new Intent(FourthActivity.this, SecondActivity.class);
        startActivity(intent);
    }
}

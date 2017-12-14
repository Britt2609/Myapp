package com.example.britt.myapp;

import android.app.VoiceInteractor;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class ThirthActivity extends AppCompatActivity {

    String category;
    String urlCategory;
    RequestQueue queue;
    JSONArray quiz;
    Integer index;
    TextView question;
    TextView answerA;
    TextView answerB;
    TextView answerC;
    TextView answerD;
    Integer score;

    private DatabaseReference databaseReference;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    User mUser;
    String id;
    String email;
    String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thirth);

        score = 0;

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        id = user.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        question = findViewById(R.id.question);
        answerA = findViewById(R.id.A);
        answerB = findViewById(R.id.B);
        answerC = findViewById(R.id.C);
        answerD = findViewById(R.id.D);

        Intent intent = getIntent();
        category = intent.getStringExtra("category");
        getQuestions();

        getFromDB();
    }


    public void addHighScore() {
        Integer newScore = mUser.score + score;
        databaseReference.child("users").child("id").child("score").setValue(newScore);
    }

    public void getFromDB() {
        // Read from the database
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                mUser = dataSnapshot.child("users").child(id).getValue(User.class);
                System.out.println("USEEEEEEEEEEEEEEEEEEEEEEEEEER " + mUser);
                Integer score = mUser.score;
                Log.d("scorevalue added:              ", "Value is: " + score);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("value failure: ", "Failed to read value.", error.toException());
            }
        };

        databaseReference.addValueEventListener(listener);
    }

    public String ApiSelected(String category) {
        switch (category) {

            case ("General Knowledge"): {
                urlCategory = "general-knowledge";
                break;
            }
            case ("Books"): {
                urlCategory = "entertainment-books";
                break;
            }
            case ("Film"): {
                urlCategory = "entertainment-film";
                break;
            }
            case ("Music"): {
                urlCategory = "entertainment-music";
                break;
            }
            case ("Musicals and Theatres"): {
                urlCategory = "entertainment-musicals-theatres";
                break;
            }
            case ("Television"): {
                urlCategory = "entertainment-tv";
                break;
            }
            case ("Video Games"): {
                urlCategory = "entertainment-video-games";
                break;
            }
            case ("Board Games"): {
                urlCategory = "entertainment-board-games";
                break;
            }
            case ("Computers"): {
                urlCategory = "science-computers";
                break;
            }
            case ("Mathematics"): {
                urlCategory = "science-mathematics";
                break;
            }
            case ("Animals"): {
                urlCategory = "animals";
                break;
            }
            case ("Mythology"): {
                urlCategory = "mythology";
                break;
            }
        }

        String url = "https://cocktail-trivia-api.herokuapp.com/api/category/" + urlCategory;
        return url;
    }


    public void getQuestions() {

        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(this);

        String url = ApiSelected(category);
        System.out.println(url);

        // Request a string response from the provided URL.
        JsonArrayRequest JsonRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                           quiz = response;
                           index = 0;
                           goToNextQuestion(index);
                        }
                }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.println("That definently didn't work");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(JsonRequest);
    }

    private void goToNextQuestion(Integer index) {
        if (index>= quiz.length()){
//            addHighScore();
            finish();
        }
        String getquestion;
        try {
            JSONObject item = quiz.getJSONObject(index);
            getquestion = item.getString("text");
            question.setText(replaceChar(getquestion));
            JSONArray getanswers = item.getJSONArray("answers");

            answerA.setText("A. " + replaceChar(getanswers.getJSONObject(0).getString("text")));
            answerA.setTag(getanswers.getJSONObject(0).getBoolean("correct"));

            answerB.setText("B. " + replaceChar(getanswers.getJSONObject(1).getString("text")));
            answerB.setTag(getanswers.getJSONObject(1).getBoolean("correct"));

            answerC.setText("C. " + replaceChar(getanswers.getJSONObject(2).getString("text")));
            answerC.setTag(getanswers.getJSONObject(2).getBoolean("correct"));

            answerD.setText("D. " + replaceChar(getanswers.getJSONObject(3).getString("text")));
            answerD.setTag(getanswers.getJSONObject(3).getBoolean("correct"));

        }
        catch (JSONException e) {
            question.setText("question could not be loaded");
        }
    }

    public void goToNext(View view) {
//        Integer answerChosen = view.getId();
        Boolean tag = (Boolean) view.getTag();
        if (tag) {
            score = score + 100;
            Toast.makeText(this, "Correct answer! You now have " + score + " points :)",
                    Toast.LENGTH_LONG).show();
        }
        else if (score > 0) {
            score = score - 80;
            Toast.makeText(this, "Wrong answer! You now have " + score + " points :(",
                    Toast.LENGTH_LONG).show();
        }

        index += 1;
        goToNextQuestion(index);
    }

    public String replaceChar(String string){
        String replaceString = string.replace("&#039;", "'");
        String newReplaceString = replaceString.replace("&quot;", "\"");
        newReplaceString = newReplaceString.replace("&euml;", "ë");
        newReplaceString = newReplaceString.replace("&amp;", "&");
        newReplaceString = newReplaceString.replace("&rsquo;", "'");
        return newReplaceString;
    }

}

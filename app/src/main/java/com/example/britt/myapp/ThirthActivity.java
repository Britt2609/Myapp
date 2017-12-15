package com.example.britt.myapp;

import android.app.VoiceInteractor;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import java.util.Iterator;
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
    TextView points;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thirth);

        score = 0;

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        id = user.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        points = findViewById(R.id.points);
        question = findViewById(R.id.question);
        answerA = findViewById(R.id.A);
        answerB = findViewById(R.id.B);
        answerC = findViewById(R.id.C);
        answerD = findViewById(R.id.D);

        // Get information of intent.
        Intent intent = getIntent();
        category = intent.getStringExtra("category");
        getQuestions();

        getFromDB();
        setListener();
    }

    /**
     * Set AuthStateListener to make sure only logged in users can go to next activity.
     */
    public void setListener() {
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    Log.d("TAG", "onAuthStateChanged:signedIn" + user.getUid());
                    Intent intent = new Intent(ThirthActivity.this, FourthActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.d("TAG", "onAuthStateChanged:signedIn");
                }
            }
        };
    }

    /**
     * Update score of current user.
     */
    public void UpdateHighScore(Integer addscore) {
        score = mUser.score + addscore;
        points.setText("score: " + score);
        databaseReference.child("users").child(id).child("score").setValue(score);
    }

    /**
     * Get data from database.
     */
    public void getFromDB() {
        // Read from the database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                mUser = dataSnapshot.child("users").child(id).getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("value failure: ", "Failed to read value.", error.toException());
            }
        });
    }

    /**
     * Get selected category and convert to url.
     */
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

    /**
     * Get questions with api key.
     */
    public void getQuestions() {

        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(this);

        String url = ApiSelected(category);

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

    /**
     * Go to next question and go to next activity when done.
     */
    private void goToNextQuestion(Integer index) {
        // If quiz done, go to next activity to see all scores.
        if (index>= quiz.length()){
            Intent intent = new Intent(ThirthActivity.this, FourthActivity.class);
            startActivity(intent);
            finish();
        }

        String getQuestion;
        try {
            // Set question and answers to the textViews.
            JSONObject item = quiz.getJSONObject(index);
            getQuestion = item.getString("text");
            question.setText(replaceChar(getQuestion));
            JSONArray getAnswers = item.getJSONArray("answers");

            // Give a tag to the answers to be able to check which one is correct.
            answerA.setText("A. " + replaceChar(getAnswers.getJSONObject(0).getString("text")));
            answerA.setTag(getAnswers.getJSONObject(0).getBoolean("correct"));

            answerB.setText("B. " + replaceChar(getAnswers.getJSONObject(1).getString("text")));
            answerB.setTag(getAnswers.getJSONObject(1).getBoolean("correct"));

            answerC.setText("C. " + replaceChar(getAnswers.getJSONObject(2).getString("text")));
            answerC.setTag(getAnswers.getJSONObject(2).getBoolean("correct"));

            answerD.setText("D. " + replaceChar(getAnswers.getJSONObject(3).getString("text")));
            answerD.setTag(getAnswers.getJSONObject(3).getBoolean("correct"));

        }
        catch (JSONException e) {
            question.setText("question could not be loaded");
        }
    }

    /**
     * Check if answer is correct, update score and go to next question.
     */
    public void goToNext(View view) {
        Boolean tag = (Boolean) view.getTag();

        // Check if answer is correct.
        if (tag) {
            Toast.makeText(this, "Correct answer!",
                    Toast.LENGTH_LONG).show();
            UpdateHighScore(10);
        }
        else {
            Toast.makeText(this, "Wrong answer!",
                    Toast.LENGTH_LONG).show();

            // Make sure score does not get negative.
            if (score > 7) {
                UpdateHighScore(-8);
            }

            // In stead of negative, make score 0.
            else {
                UpdateHighScore(score);
            }
        }

        index += 1;
        goToNextQuestion(index);
    }

    /**
     * Convert encrypted special characters in the api key to characters.
     */
    public String replaceChar(String string){
        String replaceString = string.replace("&#039;", "'");
        String newReplaceString = replaceString.replace("&quot;", "\"");
        newReplaceString = newReplaceString.replace("&euml;", "ë");
        newReplaceString = newReplaceString.replace("&amp;", "&");
        newReplaceString = newReplaceString.replace("&rsquo;", "`");
        newReplaceString = newReplaceString.replace("&lsquo;", "`");
        newReplaceString = newReplaceString.replace("&ldquo;", "\"");
        newReplaceString = newReplaceString.replace("&rdquo;", "\"");
        newReplaceString = newReplaceString.replace("&hellip;", "...");
        newReplaceString = newReplaceString.replace("&lt;", "<");
        newReplaceString = newReplaceString.replace("&gt;", ">");
        newReplaceString = newReplaceString.replace("&le;", "<=");
        newReplaceString = newReplaceString.replace("&ge;", ">=");
        return newReplaceString;
    }

}

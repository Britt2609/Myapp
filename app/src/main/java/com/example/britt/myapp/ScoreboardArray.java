//package com.example.britt.myapp;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.support.v4.widget.ResourceCursorAdapter;
//import android.view.View;
//import android.widget.ArrayAdapter;
//import android.widget.TextView;
//
//import java.lang.reflect.Array;
//import java.util.Map;
//import java.util.TreeMap;
//
///**
// * Created by britt on 14-12-2017.
// */
//
//public class ScoreboardArray extends ArrayAdapter {
//
//    public ScoreboardArray(Context context, ArrayList<> map, View view) {
//        super(context, R.layout.listview_scores, 0);
//
//        TextView number = view.findViewById(R.id.textView);
//        TextView user = view.findViewById(R.id.textView2);
//        TextView score = view.findViewById(R.id.textView3);
//
//        int i = 0;
//        for (Map.Entry<Long, String> entry : map.entrySet()) {
//            Long highScore = entry.getKey();
//            String userName = entry.getValue();
//
//            number.setText(i);
//            user.setText(userName);
//            score.setText(highScore.toString());
//
//
//        }
//
//    }
//
//}

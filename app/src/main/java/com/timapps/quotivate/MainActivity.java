package com.timapps.quotivate;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    /*************************************
     * Variables for Buttons and Field.  *
     *************************************/


    private ConstraintLayout rootView;
    private Button getQuoteButton;
    private TextView textView;
    String url = "https://api.quotable.io/random";
    Request request = new Request.Builder()
            .url(url)
            .build();


    QuoteInfo NewGeneratedquote ;

    /**************************************
     * Main initialized Method.  *
     **************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // QuoteInfo NewQuote = new QuoteInfo("t", "a"); // initialize NewQuote variable
        findViews();




        /**
         * Handle button click events
         */
        getQuoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GetQuoteResponse();



            }
        });

    }


    /****************************************
     * Methods and Actions that do things  *
     ****************************************/

    /**
     * Find the Views in the layout
     */
    private void findViews() {

        getQuoteButton = (Button) findViewById(R.id.getQuoteButton);
        textView = (TextView) findViewById(R.id.textView);


    }


    private void GetQuoteResponse() {
        new HttpGetTask().execute();}

    OkHttpClient client = new OkHttpClient();

    private class HttpGetTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                Response response = client.newCall(request).execute();
                String responseData = response.body().string();
                return responseData;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String responseData) {
            if (responseData != null) {
                // Do something with the response data
                System.out.println(responseData);
                String jSonData = responseData;
                //Log.v(TAG, jSonData);
                try {
                    QuoteInfo currentonlineQuote = getLatestQuote(responseData);
                    NewGeneratedquote = currentonlineQuote; // assign currentonlineQuote to NewGeneratedquote

                    Intent intent = new Intent(MainActivity.this, QuoteGenratedPage.class);
                    intent.putExtra("NewQuote", NewGeneratedquote);
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                // Handle the error case
                System.out.println("Error with getting data");
            }
        }
    }



    private QuoteInfo getLatestQuote(String jSonData) throws JSONException {

        JSONObject onlineQuote = new JSONObject(jSonData);
        String Quote = onlineQuote.getString("content");
        String Author = onlineQuote.getString("author");
        Log.i(TAG, "From JSON: " + Quote);
        Log.i(TAG, "From JSON: " + Author);


        QuoteInfo currentonlineQuote = new QuoteInfo(
                Quote,
                Author);


        return currentonlineQuote;
    }



}




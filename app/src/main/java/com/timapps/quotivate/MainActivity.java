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
    private String quoteUrl = "https://api.quotable.io/random";
    private String imageUrl = "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=pageimages&piprop=thumbnail&pithumbsize=250&titles=";
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

    private class HttpGetTask extends AsyncTask<Void, Void, QuoteInfo> {
        @Override
        protected QuoteInfo doInBackground(Void... voids) {
            try {
                // Get quote data
                Request quoteRequest = new Request.Builder()
                        .url(quoteUrl)
                        .build();
                Response quoteResponse = client.newCall(quoteRequest).execute();
                String quoteResponseData = quoteResponse.body().string();

                // Parse the JSON data to extract the quote and author
                JSONObject onlineQuote = new JSONObject(quoteResponseData);
                String quote = onlineQuote.getString("content");
                String author = onlineQuote.getString("author");

                // Get image data
                String pageTitle = author.replace(" ", "_");
                Request imageQueryRequest = new Request.Builder()
                        .url(imageUrl + pageTitle)
                        .build();
                Response imageQueryResponse = client.newCall(imageQueryRequest).execute();
                String imageQueryResponseData = imageQueryResponse.body().string();
                JSONObject imageQueryResponseJson = new JSONObject(imageQueryResponseData);
                JSONObject pagesObject = imageQueryResponseJson.getJSONObject("query").getJSONObject("pages");
                String firstPageId = pagesObject.keys().next();
                JSONObject pageObject = pagesObject.getJSONObject(firstPageId);
                String imageUrl = pageObject.getJSONObject("thumbnail").getString("source");


                // Create and return a new QuoteInfo object with the retrieved data
                QuoteInfo NewGeneratedquote = new QuoteInfo("quote","author","imageUrl");
                NewGeneratedquote.setQuote(quote);
                NewGeneratedquote.setAuthor(author);
                NewGeneratedquote.setImageUrl(imageUrl);

                return NewGeneratedquote;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(QuoteInfo responseData) {

            if (responseData != null) {
                String quote = responseData.getQuote();
                String author = responseData.getAuthor();
                String imageUrl = responseData.getImageUrl();
                System.out.println(imageUrl);
                NewGeneratedquote = responseData; // assign currentonlineQuote to NewGeneratedquote

                Intent intent = new Intent(MainActivity.this, QuoteGenratedPage.class);
                intent.putExtra("NewQuote", NewGeneratedquote);
                startActivity(intent);

            } else {
                // Handle the error case
                Log.d("HttpGetTask", "Error with getting data");
            }


            if (responseData != null) {
                // Do something with the response data
                System.out.println(responseData);

                //Log.v(TAG, jSonData);


            } else {
                // Handle the error case
                System.out.println("Error with getting data");
            }
        }
    }







}





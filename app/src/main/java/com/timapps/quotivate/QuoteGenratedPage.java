package com.timapps.quotivate;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class QuoteGenratedPage extends AppCompatActivity {
    private TextView QuoteLable;
    private TextView AuthorLable;
    private Button GenQuote;
    private Button SaveQuote;
    private LinearLayout buttonLayout;
    private ImageView AuthorImage;



    /**
     * Find the Views in the layout
     */
    private void findViews() {
        QuoteLable = findViewById(R.id.QuoteLable);
        AuthorLable = findViewById(R.id.AuthorLable);
        GenQuote = findViewById(R.id.GenQuote);
        SaveQuote = findViewById(R.id.SaveQuote);
        AuthorImage = findViewById(R.id.AuthorImage);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote_genrated_page);

        // Find all the Views in the layout
        findViews();

        // Retrieve the QuoteInfo object from the intent extra
        Intent intent = getIntent();
        if (intent != null) {
            QuoteInfo currentOnlineQuote = intent.getParcelableExtra("NewQuote");
            if (currentOnlineQuote != null) {
                String quote = currentOnlineQuote.getQuote();
                String author = currentOnlineQuote.getAuthor();
                // Update the UI with the new quote and author
                QuoteLable.setText(quote);
                AuthorLable.setText(author);
                Picasso.get().load(currentOnlineQuote.getImageUrl()).into(AuthorImage);
            }
        }

        // Find the device's screen height and set the margin top for the QuoteLable text view
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        int marginTop = (int) (screenHeight * 0.3);
        TextView quoteLabel = findViewById(R.id.QuoteLable);
        ViewGroup.MarginLayoutParams quoteParams = (ViewGroup.MarginLayoutParams) quoteLabel.getLayoutParams();
        quoteParams.topMargin = marginTop;
        quoteLabel.setLayoutParams(quoteParams);

        // Set the margin top of the SaveQuote button
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) SaveQuote.getLayoutParams();
        params.setMargins(0, (int) (screenHeight * 0.7), 0, 0);
        SaveQuote.setLayoutParams(params);

        // Set up the OnClickListener for the GenQuote button
        GenQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call the API to generate a new quote
                new HttpGetTask().execute();
            }
        });

        // Set up the OnClickListener for the SaveQuote button
        SaveQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save the current quote to the clipboard
                String quote = QuoteLable.getText().toString();
                String author = AuthorLable.getText().toString();
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("quote", quote + " " + author);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(QuoteGenratedPage.this, "Quote saved to clipboard", Toast.LENGTH_SHORT).show();
            }
        });
    }



        /**
         * AsyncTask to perform the API call on a separate thread
         */
        private class HttpGetTask extends AsyncTask<Void, Void, QuoteInfo> {

            OkHttpClient client = new OkHttpClient();
            String quoteUrl = "https://api.quotable.io/random";
            String imageUrl = "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=pageimages&piprop=thumbnail&pithumbsize=250&titles=";

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
                    QuoteInfo quoteInfo = new QuoteInfo("quote","author","imageUrl");
                    quoteInfo.setQuote(quote);
                    quoteInfo.setAuthor(author);
                    quoteInfo.setImageUrl(imageUrl);
                    return quoteInfo;
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

                    // Update the UI with the new quote and image
                    QuoteLable.setText(quote);
                    AuthorLable.setText(author);
                    Picasso.get().load(imageUrl).into(AuthorImage);
                } else {
                    // Handle the error case
                    Log.d("HttpGetTask", "Error with getting data");
                }
            }
        }











}





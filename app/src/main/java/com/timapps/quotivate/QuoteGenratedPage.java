package com.timapps.quotivate;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

    /**
     * Find the Views in the layout
     */
    private void findViews() {
        QuoteLable = findViewById(R.id.QuoteLable);
        AuthorLable = findViewById(R.id.AuthorLable);
        GenQuote = findViewById(R.id.GenQuote);
        SaveQuote = findViewById(R.id.SaveQuote);
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
        private class HttpGetTask extends AsyncTask<Void, Void, String> {

            OkHttpClient client = new OkHttpClient();
            String url = "https://api.quotable.io/random";
            Request request = new Request.Builder()
                    .url(url)
                    .build();

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
                    Log.d("HttpGetTask", responseData);
                    String jSonData = responseData;
                    try {
                        // Parse the JSON data to extract the quote and author
                        JSONObject onlineQuote = new JSONObject(jSonData);
                        String quote = onlineQuote.getString("content");
                        String author = onlineQuote.getString("author");

                        // Update the UI with the new quote and author
                        QuoteLable.setText(quote);
                        AuthorLable.setText(author);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle the error case
                    Log.d("HttpGetTask", "Error with getting data");
                }
            }
        }
    }





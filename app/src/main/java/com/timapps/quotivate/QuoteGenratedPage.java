package com.timapps.quotivate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class QuoteGenratedPage extends AppCompatActivity {
    private TextView QuoteLable;
    private TextView AuthorLable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote_genrated_page);
        findViews();

        // Retrieve the QuoteInfo object from the intent extra
        Intent intent = getIntent();

        if (intent != null) {
            QuoteInfo currentOnlineQuote = intent.getParcelableExtra("NewQuote");
            if (currentOnlineQuote != null) {
                String quote = currentOnlineQuote.getQuote();
                String author = currentOnlineQuote.getAuthor();
                QuoteLable.setText(quote);
                AuthorLable.setText(author);
            }


        }
    }

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2023-04-14 15:37:38 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        QuoteLable = (TextView) findViewById(R.id.QuoteLable);
        AuthorLable = (TextView) findViewById(R.id.AuthorLable);
    }
}
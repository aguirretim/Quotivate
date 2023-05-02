package com.timapps.quotivate;

import android.os.Parcel;
import android.os.Parcelable;

public class QuoteInfo implements Parcelable {

    /**************************************
     * initialized Variables for Object.  *
     **************************************/

    private String Quote;
    private String Author;
    private String imageUrl;

    public QuoteInfo(String quote, String author, String imageUrl) {
        Quote = quote;
        Author = author;
        this.imageUrl = imageUrl;
    }

    protected QuoteInfo(Parcel in) {
        Quote = in.readString();
        Author = in.readString();
        imageUrl = in.readString();
    }

    public static final Creator<QuoteInfo> CREATOR = new Creator<QuoteInfo>() {
        @Override
        public QuoteInfo createFromParcel(Parcel in) {
            return new QuoteInfo(in);
        }

        @Override
        public QuoteInfo[] newArray(int size) {
            return new QuoteInfo[size];
        }
    };

    public QuoteInfo(String quote, String author) {
        Quote = quote;
        Author = author;
    }

    public String getQuote() {
        return Quote;
    }

    public void setQuote(String quote) {
        Quote = quote;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(Quote);
        parcel.writeString(Author);
        parcel.writeString(imageUrl);
    }
}

package com.pallaud.nytimessearch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by pallaud on 6/20/16.
 */
public class Article implements Serializable {

    String webUrl;
    String headline;
    String thumbnail;

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadline() {
        return headline;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public Article (JSONObject jsonObject, boolean topStories) {
        // Getting JSON object and taking elements of the JSON response to create article
        if(!topStories) {
            try {
                webUrl = jsonObject.getString("web_url");
                headline = jsonObject.getJSONObject("headline").getString("main");
                // Only add thumbnail if that param is present
                JSONArray multimedia = jsonObject.getJSONArray("multimedia");
                if (multimedia.length() > 0) {
                    JSONObject multimediaJson = multimedia.getJSONObject(0);
                    thumbnail = "http://www.nytimes.com/" + multimediaJson.getString("url");
                } else {
                    thumbnail = "";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                webUrl = jsonObject.getString("url");
                headline = jsonObject.getString("title");
                // Only add thumbnail if that param is present
                JSONArray multimedia = jsonObject.getJSONArray("multimedia");
                if (multimedia.length() > 0) {
                    JSONObject multimediaJson = multimedia.getJSONObject(0);
                    thumbnail = multimediaJson.getString("url");
                } else {
                    thumbnail = "";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to take array of JSON objects and convert into array of article objects
    public static ArrayList<Article> fromJsonArray(JSONArray jsonArray, boolean topStories) {
        ArrayList<Article> results = new ArrayList<Article>();
        for(int i=0; i<jsonArray.length(); i++) {
            try {
                results.add(new Article(jsonArray.getJSONObject(i),topStories));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }

}

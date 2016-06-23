package com.pallaud.nytimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pallaud.nytimessearch.Article;
import com.pallaud.nytimessearch.ArticleArrayAdapter;
import com.pallaud.nytimessearch.R;
import com.pallaud.nytimessearch.extra.EndlessRecyclerViewScrollListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

    String query;
    String sort;
    String begin_date;
    ArrayList<String> newsDesk;
    @BindView(R.id.gvResults) RecyclerView gvResults;
    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;
    final int REQUEST_CODE = 20;
    @BindView(R.id.toolbar) Toolbar toolbar;
    boolean topStories = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setUpViews();
        fetchArticles(query,0,true,sort,begin_date,newsDesk);
    }

    public void setUpViews() {
        articles = new ArrayList<Article>();
        adapter = new ArticleArrayAdapter(this,articles);
        gvResults.setAdapter(adapter);
        newsDesk = new ArrayList<>();
        setUpRecycler();
    }

    public void setUpRecycler() {
        StaggeredGridLayoutManager gridManager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
        gvResults.setLayoutManager(gridManager);
        gvResults.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                fetchArticles(query,page,false,sort,begin_date,newsDesk);
            }
        });
    }

    public void fetchArticles(String query, int page, boolean newSearch,
                              String sort, String begin_date, ArrayList<String> newsDesk) {
        if(newSearch) {
            articles.clear();
        }
        this.query = query;

        AsyncHttpClient client = new AsyncHttpClient();
        String url;
        RequestParams params = new RequestParams();
        params.put("api-key", "ed5753fe0329424883b2a07a7a7b4817");
        params.put("page", page);

        if(topStories) {
            url = "https://api.nytimes.com/svc/topstories/v2/home.json";
        } else {
            url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
            params.put("q",query);
            if(sort != null) {
                params.put("sort",sort);
            }
            if(begin_date != null) {
                params.put("begin_date",begin_date);
            }
            if(newsDesk.size() > 0) {
                for(int i=0; i<newsDesk.size(); i++) {
                    params.put("news_desk",newsDesk.get(i));
                }
            }
        }

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG",response.toString());
                JSONArray articleJsonResults = null;
                try {
                    if(!topStories) {
                        articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    } else {
                        articleJsonResults = response.getJSONArray("results");
                    }

                    // Every time data is changed, notify adapter; can also do by article.addAll and use adapter.notifyDataSetChanged
                    articles.addAll(Article.fromJsonArray(articleJsonResults, topStories));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG","JSON response failed");
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            sort = data.getExtras().getString("sort");
            begin_date = data.getExtras().getString("begin_date");
            newsDesk = data.getExtras().getStringArrayList("newsDesk");
            fetchArticles(query,0,true,sort,begin_date,newsDesk);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                topStories = false;
                gvResults.clearOnScrollListeners();
                setUpRecycler();
                findViewById(R.id.tvHeading).setVisibility(View.GONE);
                fetchArticles(query,0,true,sort,begin_date,newsDesk);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        if(id == R.id.action_filter) {
            findViewById(R.id.tvHeading).setVisibility(View.GONE);
            Intent i = new Intent(this,FilterActivity.class);
            startActivityForResult(i, REQUEST_CODE);
        }

        return super.onOptionsItemSelected(item);
    }


}
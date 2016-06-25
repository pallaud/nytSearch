package com.pallaud.nytimessearch.activities;

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
import com.pallaud.nytimessearch.SearchFilter;
import com.pallaud.nytimessearch.extra.EndlessRecyclerViewScrollListener;
import com.pallaud.nytimessearch.extra.SearchFilterDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity implements SearchFilterDialogFragment.OnFilterSearchListener {

    String query;
    SearchFilter filter;
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
        fetchArticles(0,true);
    }

    // Initializing variables
    public void setUpViews() {
        articles = new ArrayList<Article>();
        adapter = new ArticleArrayAdapter(this,articles);
        gvResults.setAdapter(adapter);
        filter = new SearchFilter(null,null,null,new ArrayList<String>());
        setUpRecycler();
    }

    // Sets up recycler view with staggered grid, sets up scroll listener to enable endless scrolling.
    public void setUpRecycler() {
        StaggeredGridLayoutManager gridManager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
        gvResults.setLayoutManager(gridManager);
        gvResults.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                fetchArticles(page,false);
            }
        });
    }

    // Sets up http client, makes network request, updates data. Changes request based on whether or not app is
    // displaying top stories.
    public void fetchArticles(int page, boolean newSearch) {
        if(newSearch) { articles.clear(); }

        AsyncHttpClient client = new AsyncHttpClient();
        String url;
        RequestParams params = new RequestParams();
        params.put("api-key", "ed5753fe0329424883b2a07a7a7b4817");
        params.put("page", page);

        // If top stories, different parameters
        if(topStories) {
            url = "https://api.nytimes.com/svc/topstories/v2/home.json";
        } else {
            url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
            params.put("q",filter.getQuery());
            if(filter.getSort() != null) {
                params.put("sort",filter.getSort());
            }
            if(filter.getBegin_date() != null) {
                params.put("begin_date",filter.getBegin_date());
            }
            if(filter.getNewsDeskOpts().size() > 0) {
                for(int i=0; i<filter.getNewsDeskOpts().size(); i++) {
                    params.put("fq",String.format("news_desk:(%s)",filter.getNewsDeskOpts().get(i)));
                }
            }
            Log.d("DEBUG",params.toString());
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

    // Sets up search icon for menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // When a search is made, calls fetchArticles to make new request
            @Override
            public boolean onQueryTextSubmit(String query) {
                topStories = false;
                filter.setQuery(query);
                gvResults.clearOnScrollListeners();
                setUpRecycler();
                findViewById(R.id.tvHeading).setVisibility(View.GONE);
                fetchArticles(0,true);
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
            showFilterDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    // Sets up dialog fragment that shows filter options
    private void showFilterDialog() {
        android.app.FragmentManager fm = getFragmentManager();
        SearchFilterDialogFragment filterDialogFragment = SearchFilterDialogFragment.newInstance(filter);
        filterDialogFragment.show(fm, "fragment_filter");
    }

    // Updates filter options when dialog is closed
    public void onUpdateFilters(SearchFilter newFilter) {
        filter.setSort(newFilter.getSort());
        filter.setBegin_date(newFilter.getBegin_date());
        filter.setNewsDeskOpts(newFilter.getNewsDeskOpts());
        gvResults.clearOnScrollListeners();
        setUpRecycler();
        fetchArticles(0,true);
    }



}
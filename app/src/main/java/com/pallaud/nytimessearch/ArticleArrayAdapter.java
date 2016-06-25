package com.pallaud.nytimessearch;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pallaud.nytimessearch.activities.ArticleActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

//import com.pallaud.nytimessearch.extra.ImageView;

/**
 * Created by pallaud on 6/20/16.
 */
public class ArticleArrayAdapter extends RecyclerView.Adapter<ArticleArrayAdapter.ViewHolder> {

    private List<Article> articlesList;

    public ArticleArrayAdapter(Context context, List<Article> articles) {
        articlesList = articles;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.ivImage) ImageView ivImage;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        // Handles the row being being clicked
        @Override
        public void onClick(View view) {
            int position = getLayoutPosition(); // gets item position
            Intent i = new Intent(view.getContext(), ArticleActivity.class);
            Article article = articlesList.get(position);
            i.putExtra("article",article);
            view.getContext().startActivity(i);
        }

    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public ArticleArrayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_article_result, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ArticleArrayAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Article article = articlesList.get(position);

        // Set item views based on the data model
        TextView tvTitle = viewHolder.tvTitle;
        tvTitle.setText(article.getHeadline());

        ImageView ivImage = viewHolder.ivImage;
        ivImage.setImageResource(0);
        String thumbnail = article.getThumbnail();
        if(!TextUtils.isEmpty(thumbnail)) {
            Glide.with(ivImage.getContext()).load(thumbnail).placeholder(R.drawable.nyt_logo).into(ivImage);
        } else {
            Glide.with(ivImage.getContext()).load(R.drawable.nyt_logo).placeholder(R.drawable.nyt_logo).into(ivImage);
        }
    }

    // Return the total count of items
    @Override
    public int getItemCount() {
        return articlesList.size();
    }

}

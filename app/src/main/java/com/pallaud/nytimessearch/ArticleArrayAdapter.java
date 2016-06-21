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

import com.pallaud.nytimessearch.activities.ArticleActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

//import com.pallaud.nytimessearch.extra.ImageView;

/**
 * Created by pallaud on 6/20/16.
 */
public class ArticleArrayAdapter extends RecyclerView.Adapter<ArticleArrayAdapter.ViewHolder> {

    private List<Article> articlesList;

    public ArticleArrayAdapter(Context context, List<Article> articles) {
//        super(context, android.R.layout.simple_list_item_1, articles);
        articlesList = articles;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvTitle;
        public ImageView ivImage;

        public ViewHolder(View itemView) {

            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
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

//        @Override
//        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//            // Calculate the image ratio of the loaded bitmap
//            float ratio = (float) bitmap.getHeight() / (float) bitmap.getWidth();
//            // Set the ratio for the image
//            ivImage.setHeightRatio(ratio);
//            // Load the image into the view
//            ivImage.setImageBitmap(bitmap);
//        }
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
            Picasso.with(ivImage.getContext()).load(thumbnail).into(ivImage);
        }
    }

    // Return the total count of items
    @Override
    public int getItemCount() {
        return articlesList.size();
    }


//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        // Get the data item for this position
//        Article article = getItem(position);
//        // Check if an existing view is being reused, otherwise inflate the view
//        if (convertView == null) {
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_article_result, parent, false);
//        }
//        // Lookup view for data population
//        ImageView ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
//        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
//
//        // Populate the data into the template view using the data object
//        tvTitle.setText(article.getHeadline());
//
//        // Clear out recycled image from last convertView
//        ivImage.setImageResource(0);
//        // Populate thumbnail image, remote download image in bckground (if thumbnail exists)
//        String thumbnail = article.getThumbnail();
//        if(!TextUtils.isEmpty(thumbnail)) {
//            Picasso.with(getContext()).load(thumbnail).into(ivImage);
//        }
//
//        // Return the completed view to render on screen
//        return convertView;
//    }

}

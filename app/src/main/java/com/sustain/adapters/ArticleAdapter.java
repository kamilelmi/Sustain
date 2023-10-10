package com.sustain.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.sustain.R;
import com.sustain.databinding.LiArticleBinding;
import com.sustain.model.UploadArticle;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleViewHolder>
{
    private Context context;

    private List<UploadArticle> listData;


    public ArticleAdapter(Context context, List<UploadArticle> listData)
    {
        this.listData = listData;
        this.context = context;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LiArticleBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.li_article, parent, false);
        return new ArticleViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position)
    {
        holder.binding.tvArticleTitle.setText(listData.get(position).articleTitle);
        Glide.with(context).load(listData.get(position).thumbnailUri).into(holder.binding.ivThumbnail);
    }

    @Override
    public int getItemCount()
    {
        return listData.size();
    }
}

class ArticleViewHolder extends RecyclerView.ViewHolder
{
    LiArticleBinding binding;

    public ArticleViewHolder(@NonNull LiArticleBinding binding)
    {
        super(binding.getRoot());
        this.binding = binding;
    }
}

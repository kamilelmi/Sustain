package com.sustain.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.sustain.R;
import com.sustain.databinding.LiAllCommentBinding;
import com.sustain.model.Comment;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Ghulam Ali
 * Seamless Distribution Systems™
 * Author Email: ghulam.ali@seamless.se
 * Created on: 01/05/2023
 */
public class AllCommentsAdapter extends RecyclerView.Adapter<AllCommentViewHolder>
{

    private Context context;
    List<Comment> listData=new ArrayList<>();

    public AllCommentsAdapter(Context context, List<Comment> listData)
    {
        this.listData.clear();
        this.context = context;
        this.listData = listData;
    }

    @NonNull
    @Override
    public AllCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LiAllCommentBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.li_all_comment, parent, false);
        return new AllCommentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AllCommentViewHolder holder, int position)
    {
        holder.binding.tvComment.setText(listData.get(position).comment);
    }

    @Override
    public int getItemCount()
    {
        return listData.size();
    }
}

class AllCommentViewHolder extends RecyclerView.ViewHolder
{
    LiAllCommentBinding binding;

    public AllCommentViewHolder(@NonNull LiAllCommentBinding binding)
    {
        super(binding.getRoot());
        this.binding = binding;
    }
}

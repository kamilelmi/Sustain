package com.sustain.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sustain.R;
import com.sustain.databinding.LiLeaderboardBinding;
import com.sustain.model.LeaderBoard;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardViewHolder>
{
    private Context context;
    private List<LeaderBoard> listData;

    public LeaderboardAdapter(Context context, List<LeaderBoard> listData)
    {
        this.context = context;
        this.listData = listData;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LiLeaderboardBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.li_leaderboard, parent, false);
        return new LeaderboardViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position)
    {
        int pos = holder.getAdapterPosition();

        if (pos == 0)
        {
            holder.binding.ivPos.setImageResource(R.drawable.ic_pos_1);
        } else if (pos == 1)
        {
            holder.binding.ivPos.setImageResource(R.drawable.ic_pos_2);

        } else if (pos == 2)
        {
            holder.binding.ivPos.setImageResource(R.drawable.ic_pos_3);
        } else
        {
            holder.binding.ivPos.setVisibility(View.GONE);
        }
        holder.binding.tvChallengeTitle.setText(listData.get(position).userName);
        holder.binding.actionCount.setText(String.valueOf(listData.get(position).actionCount));

    }

    @Override
    public int getItemCount()
    {
        return listData.size();
    }
}

class LeaderboardViewHolder extends RecyclerView.ViewHolder
{
    LiLeaderboardBinding binding;

    public LeaderboardViewHolder(@NonNull LiLeaderboardBinding binding)
    {
        super(binding.getRoot());
        this.binding = binding;
    }
}

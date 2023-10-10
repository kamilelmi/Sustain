package com.sustain.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.sustain.MainActivity;
import com.sustain.R;
import com.sustain.databinding.LiChallengeBinding;
import com.sustain.fragments.LeaderboardFragment;
import com.sustain.model.Invitation;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeViewHolder>
{
    private Context context;
    private List<Invitation> list;

    public ChallengeAdapter(Context context, List<Invitation> list)
    {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ChallengeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LiChallengeBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.li_challenge, parent, false);
        return new ChallengeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeViewHolder holder, int position)
    {
        holder.binding.tvChallengeDesc.setText(list.get(position).challengeData.description);
        holder.binding.tvChallengeTitle.setText(list.get(position).challengeData.title);
        holder.itemView.setOnClickListener(view -> ((MainActivity) holder.itemView.getContext()).displayFragmentWithBackstack(LeaderboardFragment.newInstance(list.get(position))));
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }
}

class ChallengeViewHolder extends RecyclerView.ViewHolder
{
    LiChallengeBinding binding;

    public ChallengeViewHolder(@NonNull LiChallengeBinding binding)
    {
        super(binding.getRoot());
        this.binding = binding;
    }
}

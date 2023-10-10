package com.sustain.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.sustain.R;
import com.sustain.databinding.LiFollowerBinding;
import com.sustain.model.Follower;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class FollowerAdapter extends RecyclerView.Adapter<FollowerViewHolder>
{
    private Context context;

    List<Follower> followers;

    InvitedFollowerListener invitedFollowerListener;

    List<Follower> invitedFollowerList;

    public FollowerAdapter(Context context, List<Follower> followers, InvitedFollowerListener invitedFollowerListener)
    {
        this.context = context;
        this.followers = followers;
        this.invitedFollowerListener = invitedFollowerListener;
    }

    @NonNull
    @Override
    public FollowerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LiFollowerBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.li_follower, parent, false);
        return new FollowerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowerViewHolder holder, int position)
    {
        if (invitedFollowerList == null) invitedFollowerList = new ArrayList<>();

        holder.binding.tvChallengeTitle.setText(followers.get(position).userName);
        holder.binding.btnInvite.setOnClickListener(view ->
        {
            if (followers.get(position).isInvited)
            {
                followers.get(position).setInvited(false);
                holder.binding.btnInvite.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_add_circle_24));
                for (int inviteFollower = 0; inviteFollower < invitedFollowerList.size(); inviteFollower++)
                {
                    if (invitedFollowerList.get(inviteFollower).uid.equals(followers.get(position).uid))
                    {
                        invitedFollowerList.remove(inviteFollower);
                        break;
                    }
                }
            } else
            {
                followers.get(position).setInvited(true);
                holder.binding.btnInvite.setImageDrawable(context.getDrawable(R.drawable.ic_tick));
                invitedFollowerList.add(followers.get(position));

            }
            invitedFollowerListener.invitedFollower(invitedFollowerList);
        });

    }

    @Override
    public int getItemCount()
    {
        return followers.size();
    }

    public interface InvitedFollowerListener
    {
        void invitedFollower(List<Follower> invitedFolloerList);
    }
}

class FollowerViewHolder extends RecyclerView.ViewHolder
{
    LiFollowerBinding binding;

    public FollowerViewHolder(@NonNull LiFollowerBinding binding)
    {
        super(binding.getRoot());
        this.binding = binding;
    }
}

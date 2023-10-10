package com.sustain.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sustain.BaseActivity;
import com.sustain.R;
import com.sustain.databinding.LiSearchUserBinding;
import com.sustain.model.Follower;
import com.sustain.model.Following;
import com.sustain.model.User;
import com.sustain.session.Session;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Ghulam Ali
 * Seamless Distribution Systems™
 * Author Email: ghulam.ali@seamless.se
 * Created on: 08/05/2023
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder>
{
    private Context context;

    List<User> userList;

    Boolean isFollow;

    FirebaseAuth firebaseAuth;

    DatabaseReference followerDataBaseReference;

    DatabaseReference removeFollowingDataBaseReference;

    DatabaseReference removeFollowerDataBaseReference;

    DatabaseReference updateFollowerInfoRef;

    DatabaseReference updateFollowingInfoRef;

    DatabaseReference addInFollowingInfoRef;

    DatabaseReference addInFollowerInfoRef;

    DatabaseReference isFollowRef;

    DatabaseReference userInfoRef;

    User userInfo;

    BaseActivity activity;

    ValueEventListener isFollowListener;

    ValueEventListener getFollowerListener;

    User followingUserInfo;



    public SearchAdapter(Context context, List<User> user,BaseActivity activity)
    {
        this.context = context;
        this.userList = user;
        this.activity=activity;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LiSearchUserBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.li_search_user, parent, false);
        return new SearchViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position)
    {
        if (userList == null) userList = new ArrayList<>();


        isFollow = false;

        //initialize database reference
        initializeObj(position);

        getIsFollow(holder.binding.btnFollow);
        isFollowRef.addValueEventListener(isFollowListener);


        getFollower();
        followerDataBaseReference.addValueEventListener(getFollowerListener);

        if (firebaseAuth.getUid().equals(userList.get(position).getAuthKey())) holder.binding.btnFollow.setVisibility(View.GONE);

        holder.binding.tvChallengeTitle.setText(userList.get(position).userName);

        holder.binding.btnFollow.setOnClickListener(view ->
        {
            if (isFollow)
            {
                changeFollowUnFollowTextColor(holder.binding.btnFollow, false);
                isFollow = false;
                deleteInFollower();
                updateFollowingCount(false);
                updateFollowerCount(false);
            } else
            {
                isFollow = true;
                changeFollowUnFollowTextColor(holder.binding.btnFollow, true);
                addInFollower(position);
                updateFollowingCount(true);
                updateFollowerCount(true);
            }
        });
    }

    public void filterList(List<User> userList)
    {
        this.userList = userList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount()
    {
        return userList.size();
    }

    private void deleteInFollower()
    {
        removeFollowingDataBaseReference.removeValue();
        removeFollowerDataBaseReference.removeValue();
    }

    private void initializeObj(int position)
    {

        if (firebaseAuth == null) firebaseAuth = FirebaseAuth.getInstance();

        isFollowRef = FirebaseDatabase.getInstance().getReference("user").child(userList.get(position).getAuthKey()).child("follower");

        userInfoRef = FirebaseDatabase.getInstance().getReference("user").child(userList.get(position).getAuthKey()).child("userInfo");

        followerDataBaseReference = FirebaseDatabase.getInstance().getReference("user").child(userList.get(position).getAuthKey()).child("userInfo");

        updateFollowerInfoRef = FirebaseDatabase.getInstance().getReference("user").child(userList.get(position).getAuthKey()).child("userInfo").child("followerCount");

        updateFollowingInfoRef = FirebaseDatabase.getInstance().getReference("user").child(firebaseAuth.getUid()).child("userInfo").child("followingCount");

        removeFollowingDataBaseReference = FirebaseDatabase.getInstance().getReference("user").child(firebaseAuth.getUid()).child("following").child(userList.get(position).getAuthKey());

        removeFollowerDataBaseReference = FirebaseDatabase.getInstance().getReference("user").child(userList.get(position).getAuthKey()).child("follower").child(firebaseAuth.getUid());

        addInFollowingInfoRef = FirebaseDatabase.getInstance().getReference("user").child(firebaseAuth.getUid()).child("following").child(userList.get(position).getAuthKey());

        addInFollowerInfoRef = FirebaseDatabase.getInstance().getReference("user").child(userList.get(position).getAuthKey()).child("follower").child(firebaseAuth.getUid());

        if (userInfo == null) userInfo = new User();

        userInfo = (User) activity.session().get(Session.LOGGED_IN_USER);

    }

    private void getIsFollow(TextView textView)
    {
        isFollowListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

                Boolean follow = dataSnapshot.child(firebaseAuth.getUid()).child("isFollow").getValue(Boolean.class);

                if (follow != null)
                {
                    isFollow = follow;
                    changeFollowUnFollowTextColor(textView, isFollow);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                // Getting Post failed, log a message
                return;
            }
        };
    }

    private void updateFollowingCount(Boolean isAddInFollower)
    {
        if (isAddInFollower)
        {
            userInfo.followingCount = userInfo.followingCount + 1;
        } else if (userInfo.followingCount > 0)
        {
            userInfo.followingCount = userInfo.followingCount - 1;
        }

        activity.session().add(Session.LOGGED_IN_USER, userInfo);

        updateFollowingInfoRef.setValue(userInfo.followingCount);
    }

    private void updateFollowerCount(Boolean isAddInFollower)
    {
        if (followingUserInfo != null)
        {
            if (isAddInFollower)
            {
                followingUserInfo.followerCount = followingUserInfo.followerCount + 1;
            } else if (followingUserInfo.followingCount > 0)
            {
                followingUserInfo.followerCount = followingUserInfo.followerCount - 1;
            }
            updateFollowerInfoRef.setValue(followingUserInfo.followerCount);
        }
    }

    private void getFollower()
    {
        getFollowerListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                followingUserInfo = dataSnapshot.getValue(User.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                // Getting Post failed, log a message
                return;
            }
        };
    }

    private void addInFollower(int position)
    {
        Follower follower = new Follower(userInfo.userName, firebaseAuth.getUid(), true,userInfo.invitationCount);
        Following following = new Following(followingUserInfo.userName, userList.get(position).getAuthKey());

        addInFollowingInfoRef.setValue(following);
        addInFollowerInfoRef.setValue(follower);
    }

    private void changeFollowUnFollowTextColor(TextView textView, Boolean isFollow)
    {
        if (isFollow)
        {
            textView.setText(R.string.title_unfollow);
            textView.setTextColor(context.getColor(R.color.app_green));

        } else
        {
            textView.setText(R.string.title_follow);
            textView.setTextColor(context.getColor(R.color.white));
        }
    }
}

class SearchViewHolder extends RecyclerView.ViewHolder
{
    LiSearchUserBinding binding;

    public SearchViewHolder(@NonNull LiSearchUserBinding binding)
    {
        super(binding.getRoot());
        this.binding = binding;
    }
}
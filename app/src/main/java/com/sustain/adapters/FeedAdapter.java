package com.sustain.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sustain.BaseActivity;
import com.sustain.R;
import com.sustain.databinding.LiFeedBinding;
import com.sustain.interfaces.NavigateToAllCommentListiner;
import com.sustain.model.Follower;
import com.sustain.model.Following;
import com.sustain.model.Like;
import com.sustain.model.UploadPostData;
import com.sustain.model.User;
import com.sustain.session.Session;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class FeedAdapter extends RecyclerView.Adapter<FeedViewHolder>
{
    private Context context;
    List<UploadPostData> listData;

    private BaseActivity activity;

    Boolean like = false;
    User followingUserInfo;

    Boolean isFollow;


    DatabaseReference updateLikeAndUnlikeData;
    DatabaseReference saveLikeAndUnlikeData;

    DatabaseReference followerDataBaseReference;

    DatabaseReference removeFollowingDataBaseReference;

    DatabaseReference removeFollowerDataBaseReference;

    DatabaseReference updateFollowerInfoRef;

    DatabaseReference updateFollowingInfoRef;

    DatabaseReference addInFollowingInfoRef;

    DatabaseReference addInFollowerInfoRef;

    DatabaseReference isFollowRef;

    DatabaseReference userInfoRef;

    NavigateToAllCommentListiner navigateToAllCommentListiner;

    FirebaseAuth firebaseAuth;

    ValueEventListener isFollowListener;

    ValueEventListener getFollowerListener;


    User userInfo;


    public FeedAdapter(Context context, List<UploadPostData> listData, NavigateToAllCommentListiner navigateToAllCommentListiner, BaseActivity activity)
    {
        this.listData = listData;
        this.context = context;
        this.navigateToAllCommentListiner = navigateToAllCommentListiner;
        this.activity = activity;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LiFeedBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.li_feed, parent, false);
        return new FeedViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position)
    {
        like = false;
        isFollow = false;

        //initialize database reference
        initializeObj(position);

        getIsFollow(holder.binding.follower);
        isFollowRef.addValueEventListener(isFollowListener);


        getFollower();
        followerDataBaseReference.addValueEventListener(getFollowerListener);

        getLikeAndUnlikeData(position, holder.binding.ivLike);

        holder.binding.tvUserName.setText(listData.get(position).userName);
        holder.binding.tvCategory.setText(listData.get(position).category);
        holder.binding.tvSubCategory.setText(listData.get(position).subCategory);
        holder.binding.des.setText(listData.get(position).description);
        holder.binding.tvLike.setText(listData.get(position).likeCount);
        holder.binding.tvAllComments.setText("View all comments" + "(" + listData.get(position).commentCount + ")");


        if (firebaseAuth.getUid().equals(listData.get(position).getAuthKey())) holder.binding.follower.setVisibility(View.GONE);

        if (listData.get(position).image == null)
        {
            holder.binding.postImage.setVisibility(View.GONE);
            holder.binding.des.setText(listData.get(position).description);
        } else
        {
            holder.binding.tvDescription.setText(listData.get(position).description);
            holder.binding.tvDescription.setVisibility(View.VISIBLE);
            holder.binding.desContainer.setVisibility(View.GONE);
            Glide.with(context).load(listData.get(position).image).into(holder.binding.postImage);
        }


        holder.binding.ivLike.setOnClickListener(view -> {

            int totalLikeCount;
            if (like)
            {
                totalLikeCount = Integer.parseInt(listData.get(position).likeCount) - 1;
                listData.get(position).likeCount = Integer.toString(totalLikeCount);
                holder.binding.tvLike.setText(String.valueOf(totalLikeCount));
                changeBgLike(false, holder.binding.ivLike);
                like = false;
            } else
            {
                holder.binding.ivLike.setColorFilter(ContextCompat.getColor(context, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
                totalLikeCount = Integer.parseInt(listData.get(position).likeCount) + 1;
                changeBgLike(true, holder.binding.ivLike);
                holder.binding.tvLike.setText(String.valueOf(totalLikeCount));
                like = true;
            }
            updateLikeCount(totalLikeCount, position);

        });

        holder.binding.tvAddComment.setOnClickListener(view -> {
            UploadPostData uploadPostData = listData.get(position);
            navigateToAllComment(uploadPostData);
        });

        holder.binding.tvAllComments.setOnClickListener(view -> {
            UploadPostData uploadPostData = listData.get(position);
            navigateToAllComment(uploadPostData);
        });

        holder.binding.follower.setOnClickListener(view -> {
            if (isFollow)
            {
                changeFollowUnFollowTextColor(holder.binding.follower, false);
                isFollow = false;
                deleteInFollower();
                updateFollowingCount(false);
                updateFollowerCount(false);
            } else
            {
                isFollow = true;
                changeFollowUnFollowTextColor(holder.binding.follower, true);
                addInFollower(position);
                updateFollowingCount(true);
                updateFollowerCount(true);
            }
        });

    }

    @Override
    public int getItemCount()
    {
        return listData.size();
    }

    private void updateLikeCount(int likeCount, int position)
    {
        listData.get(position).likeCount = String.valueOf(likeCount);
        updateLikeAndUnlikeData.child(listData.get(position).getAuthKey()).child(listData.get(position).getKey()).child("likeCount").setValue(Integer.toString(likeCount));
        Like like = new Like(this.like);
        saveLikeAndUnlikeData.child(listData.get(position).getAuthKey()).child(listData.get(position).getKey()).child("Likes").child(firebaseAuth.getUid()).setValue(like);

    }

    private void getLikeAndUnlikeData(int position, ImageView image)
    {
        ValueEventListener postListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Like likeData = dataSnapshot.child(listData.get(position).getAuthKey()).child(listData.get(position).getKey()).child("Likes").child(firebaseAuth.getUid()).getValue(Like.class);
                if (likeData != null)
                {
                    like = likeData.isLike;
                    changeBgLike(likeData.isLike, image);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                // Getting Post failed, log a message
            }
        };
        this.saveLikeAndUnlikeData.addValueEventListener(postListener);
    }

    private void navigateToAllComment(UploadPostData uploadPostData)
    {
        navigateToAllCommentListiner.navigateToAllComment(uploadPostData);
    }

    private void changeBgLike(Boolean isLike, ImageView imageView)
    {
        if (isLike)
        {
            imageView.setColorFilter(ContextCompat.getColor(context, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
        } else
        {
            imageView.setColorFilter(ContextCompat.getColor(context, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);

        }
    }

    private void deleteInFollower()
    {
        removeFollowingDataBaseReference.removeValue();
        removeFollowerDataBaseReference.removeValue();
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
        Following following = new Following(followingUserInfo.userName, listData.get(position).getAuthKey());

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

    private void initializeObj(int position)
    {

        if (firebaseAuth == null) firebaseAuth = FirebaseAuth.getInstance();

        isFollowRef = FirebaseDatabase.getInstance().getReference("user").child(listData.get(position).getAuthKey()).child("follower");

        userInfoRef = FirebaseDatabase.getInstance().getReference("user").child(listData.get(position).getAuthKey()).child("userInfo");

        followerDataBaseReference = FirebaseDatabase.getInstance().getReference("user").child(listData.get(position).getAuthKey()).child("userInfo");

        updateFollowerInfoRef = FirebaseDatabase.getInstance().getReference("user").child(listData.get(position).getAuthKey()).child("userInfo").child("followerCount");

        updateFollowingInfoRef = FirebaseDatabase.getInstance().getReference("user").child(firebaseAuth.getUid()).child("userInfo").child("followingCount");

        removeFollowingDataBaseReference = FirebaseDatabase.getInstance().getReference("user").child(firebaseAuth.getUid()).child("following").child(listData.get(position).getAuthKey());

        removeFollowerDataBaseReference = FirebaseDatabase.getInstance().getReference("user").child(listData.get(position).getAuthKey()).child("follower").child(firebaseAuth.getUid());

        addInFollowingInfoRef = FirebaseDatabase.getInstance().getReference("user").child(firebaseAuth.getUid()).child("following").child(listData.get(position).getAuthKey());

        addInFollowerInfoRef = FirebaseDatabase.getInstance().getReference("user").child(listData.get(position).getAuthKey()).child("follower").child(firebaseAuth.getUid());

        updateLikeAndUnlikeData = FirebaseDatabase.getInstance().getReference("Post");

        saveLikeAndUnlikeData = FirebaseDatabase.getInstance().getReference("Post");

        if (userInfo == null) userInfo = new User();

        userInfo = (User) activity.session().get(Session.LOGGED_IN_USER);

    }
}

class FeedViewHolder extends RecyclerView.ViewHolder
{
    LiFeedBinding binding;

    public FeedViewHolder(@NonNull LiFeedBinding binding)
    {
        super(binding.getRoot());
        this.binding = binding;
    }
}

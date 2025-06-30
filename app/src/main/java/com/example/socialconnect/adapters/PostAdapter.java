package com.example.socialconnect.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.socialconnect.CommentActivity;
import com.example.socialconnect.R;
import com.example.socialconnect.models.Post;
import com.example.socialconnect.utils.NotificationSender;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.*;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    List<Post> postList;
    Context context;

    public PostAdapter(List<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView postText, likeCount, postUsername;
        ImageView postImage;
        ImageButton likeButton;
        Button commentBtn;
        Button writeCommentBtn;

        CircleImageView postUserImage;




        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postUsername = itemView.findViewById(R.id.postUsername);
            postUserImage = itemView.findViewById(R.id.postUserImage);
            postText = itemView.findViewById(R.id.postText);
            postImage = itemView.findViewById(R.id.postImage);
            likeButton = itemView.findViewById(R.id.likeButton);
            likeCount = itemView.findViewById(R.id.likeCount);
            commentBtn = itemView.findViewById(R.id.commentBtn);
            writeCommentBtn = itemView.findViewById(R.id.writeCommentBtn);
        }
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.postText.setText(post.text);

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(post.uid)
                .get()
                .addOnSuccessListener(userSnap -> {
                    if (userSnap.exists()) {
                        String name = userSnap.getString("name");
                        holder.postUsername.setText( name);

                        String imageUrl = userSnap.getString("imageUrl");
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(holder.itemView.getContext())
                                    .load(imageUrl)
                                    .placeholder(R.drawable.ic_profile_placeholder)
                                    .into(holder.postUserImage);
                        } else {
                            holder.postUserImage.setImageResource(R.drawable.ic_profile_placeholder);
                        }
                    } else {
                        holder.postUsername.setText("Unknown User");
                        holder.postUserImage.setImageResource(R.drawable.ic_profile_placeholder);
                    }
                });




        if (!post.imageUrl.isEmpty()) {
            holder.postImage.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext()).load(post.imageUrl).into(holder.postImage);

        } else {
            holder.postImage.setVisibility(View.GONE);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        DocumentReference postRef = db.collection("posts").document(post.id);

        postRef.get().addOnSuccessListener(snapshot -> {
            Map<String, Boolean> likes = (Map<String, Boolean>) snapshot.get("likes");
            boolean liked = likes != null && likes.containsKey(uid);
            holder.likeButton.setImageResource(liked ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
            holder.likeCount.setText((likes != null ? likes.size() : 0) + " likes");
            holder.commentBtn.setText("💬 Comment");

        });

        holder.likeButton.setOnClickListener(v -> {
            postRef.get().addOnSuccessListener(snapshot -> {
                Map<String, Boolean> likes = (Map<String, Boolean>) snapshot.get("likes");

                if (likes != null && likes.containsKey(uid)) {
                    postRef.update("likes." + uid, FieldValue.delete());
                } else {
                    postRef.update("likes." + uid, true);

                    // 🔔 Send notification to post owner (if not self)
                    if (!uid.equals(post.uid)) {
                        FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(post.uid)
                                .get()
                                .addOnSuccessListener(userSnap -> {
                                    String token = userSnap.getString("fcmToken");
                                    String likerName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                                    if (token != null && !token.isEmpty()) {
                                        com.example.socialconnect.utils.NotificationSender.sendNotification(
                                                token,
                                                "New Like!",
                                                likerName + " liked your post"
                                        );
                                    }
                                });
                    }
                }
            });
        });


        holder.commentBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentActivity.class);
            intent.putExtra("postId", post.id);
            intent.putExtra("postOwnerId", post.uid);
            context.startActivity(intent);


        });

        holder.writeCommentBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentActivity.class);
            intent.putExtra("postId", post.id);
            intent.putExtra("postOwnerId", post.uid);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }
}

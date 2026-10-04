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

        String boundPostId;
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
        holder.boundPostId = post.id;
        holder.postUsername.setText("User");
        Glide.with(holder.itemView).clear(holder.postUserImage);
        holder.postUserImage.setImageResource(R.drawable.ic_profile_placeholder);
        holder.postText.setText(post.text);

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(post.uid)
                .get()
                .addOnSuccessListener(userSnap -> {
                    if (!Objects.equals(holder.boundPostId, post.id)) return;
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




        if (post.imageUrl != null && !post.imageUrl.isEmpty()) {
            holder.postImage.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext()).load(post.imageUrl).into(holder.postImage);

        } else {
            holder.postImage.setVisibility(View.GONE);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            holder.likeButton.setEnabled(false);
            holder.likeButton.setOnClickListener(null);
            return;
        }
        String uid = mAuth.getCurrentUser().getUid();
        holder.likeButton.setEnabled(true);

        DocumentReference postRef = db.collection("posts").document(post.id);

        Map<String, Boolean> currentLikes = post.likes == null ? Collections.emptyMap() : post.likes;
        holder.likeButton.setImageResource(currentLikes.containsKey(uid) ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
        holder.likeCount.setText(currentLikes.size() + " likes");
        holder.likeButton.setOnClickListener(v -> {
            holder.likeButton.setEnabled(false);
            db.runTransaction(transaction -> {
                DocumentSnapshot snapshot = transaction.get(postRef);
                if (!snapshot.exists()) throw new IllegalStateException("Post no longer exists");
                Object rawLikes = snapshot.get("likes");
                Map<?, ?> likes = rawLikes instanceof Map ? (Map<?, ?>) rawLikes : Collections.emptyMap();
                boolean liked = likes.containsKey(uid);
                transaction.update(postRef, FieldPath.of("likes", uid), liked ? FieldValue.delete() : true);
                return !liked;
            }).addOnSuccessListener(liked -> {
                if (!Objects.equals(holder.boundPostId, post.id)) return;
                holder.likeButton.setEnabled(true);
                holder.likeButton.setImageResource(liked ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
            }).addOnFailureListener(e -> {
                if (!Objects.equals(holder.boundPostId, post.id)) return;
                holder.likeButton.setEnabled(true);
                Toast.makeText(context, "Could not update like. Try again when online.", Toast.LENGTH_SHORT).show();
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

package com.example.socialconnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialconnect.R;
import com.example.socialconnect.models.Comment;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    List<Comment> commentList;

    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentUsername, commentText, commentTime;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentUsername = itemView.findViewById(R.id.commentUsername);
            commentText = itemView.findViewById(R.id.commentText);
            commentTime = itemView.findViewById(R.id.commentTime);
        }
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        // Load comment text
        holder.commentText.setText(comment.getText());

        // Load formatted timestamp
        if (comment.getTimestamp() != null) {
            holder.commentTime.setText(getTimeAgo(comment.getTimestamp()));
        } else {
            holder.commentTime.setText("Just now");
        }

        // Fetch username from Firestore
        FirebaseFirestore.getInstance().collection("users")
                .document(comment.getUid())
                .get()
                .addOnSuccessListener(snapshot -> {
                    String name = snapshot.getString("name");
                    holder.commentUsername.setText(name != null ? name : "User");
                })
                .addOnFailureListener(e -> holder.commentUsername.setText("User"));
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    private String getTimeAgo(Timestamp timestamp) {
        long diff = new Date().getTime() - timestamp.toDate().getTime();
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
        long hours = TimeUnit.MILLISECONDS.toHours(diff);
        long days = TimeUnit.MILLISECONDS.toDays(diff);

        if (minutes < 1) return "Just now";
        else if (minutes < 60) return minutes + "m ago";
        else if (hours < 24) return hours + "h ago";
        else if (days < 7) return days + "d ago";
        else return new SimpleDateFormat("dd MMM", Locale.getDefault()).format(timestamp.toDate());
    }
}

package com.example.socialconnect;

import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.socialconnect.adapters.CommentAdapter;
import com.example.socialconnect.models.Comment;
import com.example.socialconnect.utils.NotificationSender;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.*;
public class CommentActivity extends AppCompatActivity {

    private RecyclerView commentRecycler;
    private EditText commentInput;
    private Button sendBtn;
    private CommentAdapter adapter;
    private List<Comment> commentList;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private String postId;
    private boolean focusInput = false;
    private boolean scrollToBottom = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        commentRecycler = findViewById(R.id.commentRecyclerView);
        commentInput = findViewById(R.id.commentInput);
        sendBtn = findViewById(R.id.sendBtn);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        postId = getIntent().getStringExtra("postId");
        String postOwnerId = getIntent().getStringExtra("postOwnerId");

        focusInput = getIntent().getBooleanExtra("focusInput", false);
        scrollToBottom = getIntent().getBooleanExtra("scrollToBottom", false);

        if (postId == null) {
            Toast.makeText(this, "Invalid post. Cannot load comments.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        commentList = new ArrayList<>();
        adapter = new CommentAdapter(commentList);
        commentRecycler.setLayoutManager(new LinearLayoutManager(this));
        commentRecycler.setAdapter(adapter);

        loadComments();

        sendBtn.setOnClickListener(v -> {
            String commentText = commentInput.getText().toString().trim();
            if (commentText.isEmpty()) {
                commentInput.setError("Comment cannot be empty");
                return;
            }

            String uid = auth.getCurrentUser().getUid();
            String commentId = UUID.randomUUID().toString();
            Map<String, Object> comment = new HashMap<>();
            comment.put("id", commentId);
            comment.put("uid", uid);
            comment.put("text", commentText);
            comment.put("timestamp", Timestamp.now());

            db.collection("posts")
                    .document(postId)
                    .collection("comments")
                    .document(commentId)
                    .set(comment)
                    .addOnSuccessListener(unused -> {
                        commentInput.setText("");
                        hideKeyboard();
                        loadComments();
                    });


            assert postOwnerId != null;
            db.collection("users").document(postOwnerId).get()
                    .addOnSuccessListener(userSnap -> {
                        String token = userSnap.getString("fcmToken");
                        String commenterName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                        if (token != null && !token.isEmpty()) {
                            NotificationSender.sendNotification(
                                    token,
                                    "New Comment!",
                                    (commenterName != null ? commenterName : "Someone") + " commented on your post."
                            );
                        }
                    });

        });

        if (focusInput) {
            commentInput.requestFocus();
            commentInput.postDelayed(() -> {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(commentInput, InputMethodManager.SHOW_IMPLICIT);
                }
            }, 250);
        }
    }

    private void loadComments() {
        db.collection("posts")
                .document(postId)
                .collection("comments")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    commentList.clear();
                    for (DocumentSnapshot doc : querySnapshot) {
                        Comment comment = doc.toObject(Comment.class);
                        commentList.add(comment);
                    }
                    adapter.notifyDataSetChanged();
                    if (scrollToBottom && !commentList.isEmpty()) {
                        commentRecycler.scrollToPosition(commentList.size() - 1);
                    }
                });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(commentInput.getWindowToken(), 0);
        }
    }
}

package com.example.socialconnect.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialconnect.CreatePostActivity;
import com.example.socialconnect.R;
import com.example.socialconnect.adapters.PostAdapter;
import com.example.socialconnect.models.Post;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    Button createPostBtn;
    List<Post> postList = new ArrayList<>();
    PostAdapter adapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.postRecyclerView);
        createPostBtn = view.findViewById(R.id.createPostBtn);

        adapter = new PostAdapter(postList, requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        createPostBtn.setOnClickListener(v -> startActivity(new Intent(getActivity(), CreatePostActivity.class)));

        loadPosts();


    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadPosts() {
        FirebaseFirestore.getInstance()
                .collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    postList.clear();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Post post = doc.toObject(Post.class);
                        if (post != null) {
                            post.id = doc.getId(); // 🔥 required for likes/comments
                            Log.d("POST_LOAD", "Post loaded: " + post.text);
                            postList.add(post);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    Log.d("POST_ADAPTER", "Adapter notified with " + postList.size() + " posts");

                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error loading posts", e);
                    Toast.makeText(getContext(), "Error loading posts", Toast.LENGTH_SHORT).show();
                });
    }

}

package com.example.socialconnect.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    Button createPostBtn;
    List<Post> postList = new ArrayList<>();
    PostAdapter adapter;

    private ListenerRegistration feedListener;

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




    }

    @Override public void onStart() {
        super.onStart();
        loadPosts();
    }

    @Override public void onStop() {
        if (feedListener != null) { feedListener.remove(); feedListener = null; }
        super.onStop();
    }

    @Override public void onDestroyView() {
        recyclerView.setAdapter(null);
        recyclerView = null;
        adapter = null;
        super.onDestroyView();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadPosts() {
        if (feedListener != null) feedListener.remove();
        feedListener = db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (getView() == null || adapter == null) return;
                    if (error != null || querySnapshot == null) {
                        Toast.makeText(getContext(), "Could not load feed. Check your connection.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    postList.clear();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Post post = doc.toObject(Post.class);
                        if (post != null && post.uid != null && !post.uid.isEmpty()) {
                            post.id = doc.getId();
                            postList.add(post);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}

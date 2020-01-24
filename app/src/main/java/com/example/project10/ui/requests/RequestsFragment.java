package com.example.project10.ui.requests;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project10.R;
import com.example.project10.RequestsAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RequestsFragment extends Fragment {

    private ArrayList<String> mRequestDescriptions = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_requests, container, false);
        final TextView emptyList = root.findViewById(R.id.emptyList);
        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Swipes Requests");
        db.collection("provideSwipes").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (!document.exists()) {
                    emptyList.setText("Turn on \'Provide Swipes\' to see Requests");
                }
                else {
                    db.collection("requestSwipes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().isEmpty()) {
                                    emptyList.setText("No one is requesting swipes");
                                }
                                else {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        mRequestDescriptions.add(document.getId());
                                        RecyclerView requestsRecyclerView = root.findViewById(R.id.requests_recycler_view);
                                        RequestsAdapter requestsAdapter = new RequestsAdapter(getActivity(), mRequestDescriptions);
                                        requestsRecyclerView.setAdapter(requestsAdapter);
                                        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                    }
                                }
                            }
                        }
                    });
                }
            }
            }
        });
        return root;
    }
}
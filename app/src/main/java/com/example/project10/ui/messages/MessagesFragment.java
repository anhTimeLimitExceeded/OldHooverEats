package com.example.project10.ui.messages;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project10.ConversationsAdapter;
import com.example.project10.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class MessagesFragment extends Fragment {
    private static ArrayList<String> mConversations = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    final String TAG = "MessagesFragment";
    static ConversationsAdapter conversationsAdapter = new ConversationsAdapter(null, null);
//    public static void updateRecyclerView(){
//        conversationsAdapter.notifyDataSetChanged();
//    }
    public static void moveToTop(String chatID) {
        conversationsAdapter.moveToTop(chatID);
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_messages, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Messages");
        final String userID = fAuth.getCurrentUser().getUid();
        db.collection("users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()){
                    Log.w(TAG, document.getData().get("engagedChatConversations").toString());
                    HashMap<String, String> engagedChatConversations = (HashMap<String, String>) document.getData().get("engagedChatConversations");
                    mConversations = new ArrayList<>(engagedChatConversations.keySet());
                    RecyclerView conversationsRecyclerView = root.findViewById(R.id.conversations_recycler_view);
                    conversationsAdapter.setConversations(mConversations);
                    conversationsAdapter.setContext(getActivity());
                    conversationsRecyclerView.setAdapter(conversationsAdapter);
                    conversationsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    Log.w(TAG, mConversations.toString());
                }
            }
            }
        });
        return root;
    }
}
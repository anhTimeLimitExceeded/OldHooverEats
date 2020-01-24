package com.example.project10;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.project10.ui.home.HomeFragment;
import com.example.project10.ui.messages.MessagesFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class ConversationActivity extends AppCompatActivity {

    private ArrayList<HashMap<String, String>> mMessages = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    final String TAG = "ConvActivity";
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

//        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
//            @Override
//            public void handleOnBackPressed() {
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                intent.putExtra("EXTRA", "openMessagesFragment");
//                startActivity(intent);
//            }
//        };
//        getOnBackPressedDispatcher().addCallback(this, callback);
//
        final String userID = fAuth.getCurrentUser().getUid();
        final String toUserID = getIntent().getStringExtra("toUserID");

        final ImageView sendBtn = findViewById(R.id.send_image_view);
        final TextInputEditText editMessage = findViewById(R.id.edit_message);
        final RecyclerView messagesRecyclerView = findViewById(R.id.messages_recycler_view);

        db.collection("users").document(toUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        setTitle((String) document.getData().get("name"));
                    }
                }
            }});

        db.collection("users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    HashMap<String, String> engagedChatConversations = (HashMap<String, String>) document.getData().get("engagedChatConversations");
                    final String chatID = engagedChatConversations.get(toUserID);
                    final DocumentReference conversationDoc = db.collection("chatConversations").document(chatID);
                    conversationDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            final DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.w(TAG, document.getData().get("messages").toString());
                                mMessages = (ArrayList<HashMap<String, String>>) document.getData().get("messages");
                                MessagesAdapter messagesAdapter = new MessagesAdapter(context, mMessages);
                                messagesRecyclerView.setAdapter(messagesAdapter);
                                messagesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                                Log.w(TAG, mMessages.toString());

                                conversationDoc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                    if (e != null) {
                                        Log.w(TAG, "Listen failed.", e);
                                        return;
                                    }
                                    if (documentSnapshot != null && documentSnapshot.exists()) {
                                        Log.d(TAG, "Current data: " + documentSnapshot.getData());
//                                        db.collection("chatConversations").document(chatID);
                                        conversationDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    final DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        mMessages = (ArrayList<HashMap<String, String>>) document.getData().get("messages");
                                                        MessagesAdapter messagesAdapter = new MessagesAdapter(context, mMessages);
                                                        messagesRecyclerView.setAdapter(messagesAdapter);
                                                        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                                                        messagesRecyclerView.scrollToPosition(messagesRecyclerView.getAdapter().getItemCount()-1);
                                                        MessagesFragment.moveToTop(toUserID);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                    }
                                });

                                sendBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    HashMap<String, String> message = new HashMap<>();
                                    SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    message.put("content", editMessage.getText().toString());
                                    message.put("sentTime", utcFormat.format(new Date()));
                                    message.put("sentUser", userID);
                                    editMessage.setText("");
                                    mMessages.add(message);
                                    db.collection("chatConversations").document(chatID)
                                            .update("messages", mMessages,
                                                "lastSentTime", FieldValue.serverTimestamp());
//                                                RecyclerView messagesRecyclerView = findViewById(R.id.messages_recycler_view);
//                                                MessagesAdapter messagesAdapter = new MessagesAdapter(context, mMessages);
//                                                messagesRecyclerView.setAdapter(messagesAdapter);
//                                                messagesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                                    }
                                });
                            }
                        }
                        }
                    });
                }
            }
            }
        });
    }
}
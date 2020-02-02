package com.example.project10;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project10.ui.requests.RequestsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewHolder>{

    private ArrayList<String> mRequestDescriptions;
    private Context mContext;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public RequestsAdapter(Context context, ArrayList<String> requestDescriptions) {
        mContext = context;
        mRequestDescriptions = requestDescriptions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_item, parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        db.collection("users").document(mRequestDescriptions.get(position)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        holder.requestDescription.setText(document.get("name") + " is looking for a swipe");
                    }
                }
            }
        });
        holder.declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newPosition = holder.getAdapterPosition();
                mRequestDescriptions.remove(newPosition);
                notifyItemRemoved(newPosition);
                notifyItemRangeChanged(newPosition,mRequestDescriptions.size());
            }
        });
        holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
            final String toUserID = mRequestDescriptions.get(position);
            db.collection("users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            HashMap<String, String> engagedChatConversationsUser = (HashMap<String, String>) document.getData().get("engagedChatConversations");
                            if (!(engagedChatConversationsUser.containsKey(toUserID))) {
                                DocumentReference chatChannel = db.collection("chatConversations").document();
                                Map<String, Object> map = new HashMap<>();
                                map.put("user1", userID);
                                map.put("user2", toUserID);
                                map.put("lastSentTime", FieldValue.serverTimestamp());
                                map.put("messages", new ArrayList<HashMap<String, String>>());
                                chatChannel.set(map);
                                final String chatID = chatChannel.getId();
                                engagedChatConversationsUser.put(toUserID, chatID);
                                db.collection("users").document(userID).update("engagedChatConversations", engagedChatConversationsUser);

                                db.collection("users").document(toUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                HashMap<String, String> engagedChatConversationsToUser = new HashMap<>();
                                                if (document.getData().get("engagedChatConversations") != null) {
                                                    engagedChatConversationsToUser = (HashMap<String, String>) document.getData().get("engagedChatConversations");
                                                }
                                                engagedChatConversationsToUser.put(userID, chatID);
                                                db.collection("users").document(toUserID).update("engagedChatConversations", engagedChatConversationsToUser);
                                            }
                                        }
                                    }
                                });
                            }

                            Intent intent = new Intent (v.getContext(), ConversationActivity.class);
                            intent.putExtra("toUserID", toUserID);
                            v.getContext().startActivity(intent);
                        }
                    }
                }
            });
            db.collection("requestSwipes").document(toUserID).delete();
            int newPosition = holder.getAdapterPosition();
            mRequestDescriptions.remove(newPosition);
            notifyItemRemoved(newPosition);
            notifyItemRangeChanged(newPosition,mRequestDescriptions.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRequestDescriptions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView requestDescription;
        LinearLayout requestItem;
        Button declineBtn;
        Button acceptBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            requestDescription = itemView.findViewById(R.id.request_item_description);
            requestItem = itemView.findViewById(R.id.request_item);
            declineBtn = itemView.findViewById(R.id.request_item_decline_btn);
            acceptBtn = itemView.findViewById(R.id.request_item_accept_btn);
        }
    }
}

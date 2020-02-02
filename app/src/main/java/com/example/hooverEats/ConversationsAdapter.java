package com.example.project10;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ViewHolder>{
    private static ArrayList<String> mConversations;
    private Context mContext;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    final String userID = fAuth.getCurrentUser().getUid();
    final String TAG = "ConversationsAdap";

    public ConversationsAdapter(Context context, ArrayList<String> conversations) {
        mContext = context;
        mConversations = conversations;
    }
    public void setConversations(ArrayList<String> conversations){
        mConversations = conversations;
    }
    public void setContext(Context context){
        mContext = context;
    }
    @NonNull
    @Override
//    public ConversationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_item, parent,false);
//        ConversationsAdapter.ViewHolder viewHolder = new ConversationsAdapter.ViewHolder(view);
//        return viewHolder;
//    }
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_item, parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ConversationsAdapter.ViewHolder holder, final int position) {
        db.collection("users").document(mConversations.get(position)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        holder.conversationName.setText((String) document.getData().get("name"));
                        String chatID = ((HashMap<String, String>) document.getData().get("engagedChatConversations")).get(userID);
                        db.collection("chatConversations").document(chatID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        ArrayList<HashMap<String, String>> messages = (ArrayList<HashMap<String, String>>) document.getData().get("messages");
                                        String message = messages.get(messages.size()-1).get("content");
                                        holder.conversationLastMessage.setText(message);
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
        holder.conversationItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Log.w(TAG, mConversations.get(position));
            Intent intent = new Intent (v.getContext(), ConversationActivity.class);
            intent.putExtra("toUserID", mConversations.get(position));
            v.getContext().startActivity(intent);
            }
        });
    }

    public void moveToTop(String chatID) {
        Iterator<String> iter = mConversations.iterator();
        while (iter.hasNext()) {
            String str = iter.next();
            if (str.equals(chatID)){
                iter.remove();
                break;
            }
        }
        mConversations.add(0, chatID);
        notifyDataSetChanged();
        Log.w("ConversationsAdapter", "Conversations updated");
    }

    @Override
    public int getItemCount() {
        return mConversations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView conversationName;
        TextView conversationLastMessage;
        CardView conversationItem;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            conversationLastMessage = itemView.findViewById(R.id.conversation_item_last_message);
            conversationName = itemView.findViewById(R.id.conversation_item_name);
            conversationItem = itemView.findViewById(R.id.conversation_item);
        }
    }

}



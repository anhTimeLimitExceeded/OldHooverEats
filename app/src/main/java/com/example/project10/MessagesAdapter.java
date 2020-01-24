package com.example.project10;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private ArrayList<HashMap<String, String>> mMessages;
    private Context mContext;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    final String userID = fAuth.getCurrentUser().getUid();
    final String TAG = "MessagesAdap";

    public MessagesAdapter(Context context, ArrayList<HashMap<String, String>> messages) {
        mContext = context;
        mMessages = messages;
    }

    @NonNull
    @Override

    public MessagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent,false);
        MessagesAdapter.ViewHolder viewHolder = new MessagesAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MessagesAdapter.ViewHolder holder, final int position) {
        Log.w(TAG,mMessages.get(position).toString());
        HashMap<String, String> map = mMessages.get(position);
        holder.messageText.setText(map.get("content"));
        //holder.messageTime.setText(map.get("sentTime"));
        if (map.get("sentUser").equals(userID)){
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.END;
            holder.messageRoot.setLayoutParams(params);
            holder.messageRoot.setBackgroundResource(R.drawable.rect_round_white);
        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public void updateMessages(ArrayList<HashMap<String, String>> messages) {
        mMessages = messages;
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout messageRoot;
        TextView messageText;
        TextView messageTime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageRoot = itemView.findViewById(R.id.message_root);
            messageText = itemView.findViewById(R.id.message_text);
            //messageTime = itemView.findViewById(R.id.message_time);
        }
    }


}

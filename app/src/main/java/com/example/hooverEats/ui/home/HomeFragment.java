package com.example.project10.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.project10.LoginActivity;
import com.example.project10.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    Button returnBtn;
    FirebaseAuth fAuth;
    TextView uLoginInfo;
    Switch provideSwipesSwitch;
    ToggleButton requestSwipesBtn;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        fAuth = FirebaseAuth.getInstance();
        uLoginInfo = root.findViewById(R.id.loginInfo);
        returnBtn = root.findViewById(R.id.logoutbtn);
        provideSwipesSwitch = root.findViewById(R.id.provideSwipes);
        requestSwipesBtn = root.findViewById(R.id.requestSwipesBtn);
        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        db.collection("requestSwipes").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        requestSwipesBtn.setChecked(true);
                        provideSwipesSwitch.setEnabled(false);
                    }
                    else {
                        requestSwipesBtn.setChecked(false);
                        provideSwipesSwitch.setEnabled(true);
                    }
                }
            }
        });

        db.collection("users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    uLoginInfo.setText("Logged in as " + document.getData().get("name").toString());
                    boolean provideSwipes = (boolean) document.getData().get("provideSwipes");
                    provideSwipesSwitch.setChecked(provideSwipes);
                    requestSwipesBtn.setEnabled(!provideSwipes);
                }
            }
            }
        });

        provideSwipesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if (task.isSuccessful()) {
                        Map<String,Object> test = new HashMap<>();
                        String token = task.getResult().getToken();
                        test.put("tokenID", token);
                        db.collection("users").document(userID).update("provideSwipes", true);
                        db.collection("provideSwipes").document(userID).set(test);
                        requestSwipesBtn.setEnabled(false);
                    }
                    }
                });
            } else {
                db.collection("users").document(userID).update("provideSwipes", false);
                db.collection("provideSwipes").document(userID).delete();
                requestSwipesBtn.setEnabled(true);
            }
            }
        });

        requestSwipesBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Map<String, Object> test = new HashMap<>();
                test.put("name", "test");
                if (isChecked) {
                    db.collection("requestSwipes").document(userID).set(test);
                    provideSwipesSwitch.setEnabled(false);
                } else {
                    db.collection("requestSwipes").document(userID).delete();
                    provideSwipesSwitch.setEnabled(true);
                }
            }
        });

        return root;
    }
}
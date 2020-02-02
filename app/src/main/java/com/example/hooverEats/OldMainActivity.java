package com.example.project10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class OldMainActivity extends AppCompatActivity {

    Button returnBtn;
    FirebaseAuth fAuth;
    TextView uLoginInfo;
    Switch provideSwipesSwitch;
    Button requestSwipesBtn;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.old_activity_main);

        fAuth = FirebaseAuth.getInstance();
        uLoginInfo = findViewById(R.id.loginInfo);
        returnBtn = findViewById(R.id.logoutbtn);
        provideSwipesSwitch = findViewById(R.id.provideSwipes);
        requestSwipesBtn = findViewById(R.id.requestSwipesBtn);

        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            final String userID = user.getUid();
            requestSwipesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, Object> test = new HashMap<>();
                    test.put("test", "test");
                    db.collection("requestSwipes").document(userID).set(test);
                }
            });
        }

        if (user != null) {
            final String userID = user.getUid();
            DocumentReference docRef = db.collection("users").document(userID);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            uLoginInfo.setText("Logged in as " + document.getData().get("name").toString());

                            boolean provideSwipes = (boolean) document.getData().get("provideSwipes");
                            provideSwipesSwitch.setChecked(provideSwipes);
                        }
                    }
                }
            });

            provideSwipesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Map<String,Object> test = new HashMap<>();
                        test.put("test", "test");
                        db.collection("users").document(userID).update("provideSwipes", true);
                        db.collection("provideSwipes").document(userID).set(test);
                    } else {
                        db.collection("users").document(userID).update("provideSwipes", false);
                        db.collection("provideSwipes").document(userID).delete();
                    }
                }
            });

        }
    }
}

package com.example.project10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    EditText mEmail, mPassword;
    Button mRegisterBtn;
    TextView mLoginBtn;
    TextView mName;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    Spinner mYear;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mRegisterBtn = findViewById(R.id.loginbtn);
        mLoginBtn = findViewById(R.id.loginlink);
        mName = findViewById(R.id.name);
        mYear = findViewById(R.id.year);
        String[] years = new String[]{"Freshman","Sophomore","Junior","Senior"};

        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
//
//        if(fAuth.getCurrentUser() != null) {
//            startActivity(new Intent(getApplicationContext(), MainActivity.class));
//            finish();
//        }
        ArrayAdapter yearSpinnerAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.years,
                R.layout.year_spinner);
        yearSpinnerAdapter.setDropDownViewResource(R.layout.year_spinner);
        mYear.setAdapter(yearSpinnerAdapter);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                final String name = mName.getText().toString().trim();
                final String year = String.valueOf(mYear.getSelectedItem());
                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is required");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is required");
                    return;
                }

                if(password.length() < 6){
                    mPassword.setError("Password must be longer than 6 char");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "User Created.", Toast.LENGTH_SHORT).show();
                            final String userID = fAuth.getCurrentUser().getUid();
//                            final DocumentReference documentReference = db.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("name", name);
                            user.put("email", email);
                            user.put("year", year );
                            user.put("provideSwipes", false);

                            db.collection("users").document(userID)
                                    .set(user);
//                                    .OnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                        @Override
//                                        public void onSuccess(DocumentReference documentReference) {
//                                            Log.d("RegisterActivity", "DocumentSnapshot added with ID: " + documentReference.getId());
//                                        }
//                                    })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            Log.w("RegisterActivity", "Error adding document", e);
//                                        }
//                                    });

                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        }
                        else {
                            Toast.makeText(RegisterActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}

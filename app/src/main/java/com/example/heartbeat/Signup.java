package com.example.heartbeat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.SaveInfo;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Signup extends AppCompatActivity {
EditText name,email,password;
Button signup;
TextView login;
DatabaseReference reference;
FirebaseAuth auth;
    ImageView open,close;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        auth=FirebaseAuth.getInstance();
reference= FirebaseDatabase.getInstance().getReference();
        name=findViewById(R.id.signup_name);
        email=findViewById(R.id.signup_email);
        password=findViewById(R.id.signup_password);
        signup=findViewById(R.id.create_button);
        close=findViewById(R.id.close_eye);
        open=findViewById(R.id.open_eye);



        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //HIDE PASSWORD
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                open.setVisibility(View.VISIBLE);
                close.setVisibility(View.INVISIBLE);
            }
        });
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Show Password
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                close.setVisibility(View.VISIBLE);
                open.setVisibility(View.INVISIBLE);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            String n=name.getText().toString();
            String em=email.getText().toString();
            String ps=password.getText().toString();
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(name.getText().toString()) )
                {
                    name.requestFocus();
                    name.setError("Please enter name");
                }
                else if(TextUtils.isEmpty(email.getText().toString()))
                {
                    email.requestFocus();
                    email.setError("Please enter email");
                }
                else if(TextUtils.isEmpty(password.getText().toString()))
                {
                    password.requestFocus();
                    password.setError("Please enter password");
                }
                else
                {
                    sig(email.getText().toString(),password.getText().toString());
                }
            }
        });


    }

    private void sig(String em, String ps) {
        auth.createUserWithEmailAndPassword(em,ps).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isComplete())
                {
                    Toast.makeText(Signup.this, "Signup Successfully", Toast.LENGTH_SHORT).show();

                    Save();
                }
                else
                {
                    Toast.makeText(Signup.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void Save() {
        String n=name.getText().toString();
       // String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        HashMap<String ,Object> map=new HashMap<>();
        map.put("name",name.getText().toString());

        reference.child("Users").setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete())
                {
                    Toast.makeText(Signup.this, "Saved", Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(Signup.this,MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();

                }else
                {
                    Toast.makeText(Signup.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

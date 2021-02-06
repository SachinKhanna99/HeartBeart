package com.example.heartbeat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class LoginActity extends AppCompatActivity {
EditText email,password;
TextView signup;
Button login;
FirebaseAuth mauth;
ImageView open,close;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_actity);
        mauth=FirebaseAuth.getInstance();
        signup=findViewById(R.id.signup_text);
    close=findViewById(R.id.close_eye);
    open=findViewById(R.id.open_eye);
        if(mauth.getCurrentUser()!=null){
            Intent i=new Intent(LoginActity.this,MainActivity.class);
            startActivity(i);
            finish();
        }

        email=findViewById(R.id.login_email);
        password=findViewById(R.id.login_password);
        login=findViewById(R.id.login_button);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(LoginActity.this,Signup.class);
                startActivity(i);

            }
        });
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
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String em=email.getText().toString();
                String pswrd=password.getText().toString();

                if(TextUtils.isEmpty(email.getText().toString()) )
                {

                    email.requestFocus();
                    email.setError("Please Enter email");
                }
                else if(TextUtils.isEmpty(password.getText().toString()))
                {
                    password.requestFocus();
                    password.setError("Please Enter Password");
                }
                else
                {
                    Login(email.getText().toString(),password.getText().toString());
                }
            }
        });

    }

    private void Login(String em, String pswrd)
    {
        mauth.signInWithEmailAndPassword(em,pswrd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isComplete())
                {
                    Intent i=new Intent(LoginActity.this,MainActivity.class);
                    startActivity(i);
                    finish();
                    Toast.makeText(LoginActity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(LoginActity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

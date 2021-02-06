package com.example.heartbeat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Result extends AppCompatActivity {
String re=null;
TextView result,condition,save;
DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
       Bundle b=new Bundle();
       save=findViewById(R.id.saving);
        reference= FirebaseDatabase.getInstance().getReference().child("Users");
       b=getIntent().getExtras();
       re=b.getString("result");
        result=findViewById(R.id.result);
        condition=findViewById(R.id.condition);

  //      int p=Integer.parseInt(re);
//        if(p > 200)
//        {
//            result.setText("TRY AGAIN Put your finger on camera");
//        }
//        else
//        {
           result.setText(re);



        int val = Integer.parseInt(result.getText().toString());

        Log.e("TAG", "onCreate: "+val );
        if(val < 60)
        {
           condition.setText("Slow");
        }
        else if(val > 60 && val <=100){

            condition.setText("Normal");
        }
        else if(val > 100)
        {
            condition.setText("Fast");
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cond=condition.getText().toString();
                String res=result.getText().toString();

                if(TextUtils.isEmpty(cond) && TextUtils.isEmpty(res))
                {
                    save.setText("");
                }
                else
                {
                    savedinfo(cond,res);
                }
            }
        });
    }

    private void savedinfo(String cond, String res) {
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        String dateToStr = format.format(today);
        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        HashMap<String , Object >ma=new HashMap<>();

        ma.put("bpm",res);
        ma.put("condition",cond);
        ma.put("time",dateToStr);

        reference.child(uid).setValue(ma).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(Result.this, "Added Successfully", Toast.LENGTH_SHORT).show();

                    Intent i=new Intent(Result.this,ViewAllResult.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }
                else
                {
                    Toast.makeText(Result.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}

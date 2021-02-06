package com.example.heartbeat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewAllResult extends AppCompatActivity {
RecyclerView recyclerView;
ResultApadter adapter;
List<Users> arrayList;
DatabaseReference reference;
TextView name;
private String TAG="HELLO";
String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_result);
        name=findViewById(R.id.name_all);
        recyclerView=findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        arrayList=new ArrayList<>();
    //  reference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        ReadData();



    }

    private void ReadData() {

        final DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Users");
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {

            @Override

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.e("TAG", "onDataChange: "+uid);
              //  arrayList.clear();

                for (DataSnapshot snapshot:dataSnapshot.getChildren()) {

//                    Users users=snapshot.getValue(Users.class);
    Users users=new Users(snapshot.child("bpm").getValue(String.class),snapshot.child("condition").getValue(String.class),snapshot.child("time").getValue(String.class));

                    Log.e(TAG, "Balle--------> "+snapshot.getValue());
                    String valu=snapshot.child("condition").getValue(String.class);
                    String val=snapshot.child("bpm").getValue(String.class);
                    String vali=snapshot.child("time").getValue(String.class);

                    Log.e(TAG, "onDataChange: "+users );


                    Log.e(TAG, "onDataChange:---------------------> "+valu );
                    Log.e(TAG, "BPM:---------------------> "+val );
                    Log.e(TAG, "onDataChange:---------------------> "+vali );



                   arrayList.add(users);


                }
                adapter=new ResultApadter(arrayList,ViewAllResult.this);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String val=dataSnapshot.child("name").getValue(String.class);

                name.setText(val);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

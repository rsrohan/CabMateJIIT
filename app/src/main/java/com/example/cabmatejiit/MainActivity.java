package com.example.cabmatejiit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    Spinner source, dest;
    Button findCab;
    FirebaseUser user;
    DatabaseReference referenceToBookCab;

    UserProfile userProfileInformations;

    String username;
    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        source = findViewById(R.id.source);
        dest = findViewById(R.id.destination);
        findCab = findViewById(R.id.findCab);

        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user==null)
        {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }else{
            final ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            try{
                pd.show();
            }catch (Exception e){
            }
            final DatabaseReference userDetailsReference = FirebaseDatabase
                    .getInstance()
                    .getReference("USER_DETAILS")
                    .child(Objects.requireNonNull(user.getPhoneNumber()));
            referenceToBookCab = FirebaseDatabase
                    .getInstance()
                    .getReference("LIVE_BOOKINGS");

            setLogOutButton();
            
            userDetailsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    try{
                        userProfileInformations = dataSnapshot.getValue(UserProfile.class);
                        assert userProfileInformations != null;
                        username = userProfileInformations.getName();
                        if (userProfileInformations.isAlreadyBooked())
                        {
                            pd.dismiss();
                            startActivity(new Intent(getApplicationContext(), ChatActivity.class)
                                    .putExtra("reference", userProfileInformations.getBookedNumber()).putExtra("username", userProfileInformations.getName()));
                            finish();

                        }else{
                            pd.dismiss();
                        }
                    }catch (Exception e){
                        Log.d(TAG, "onDataChange: "+e);
                        pd.dismiss();
                    }
                    
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            findCab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ArrayList<Cabmate> cabmates = new ArrayList<>();
                    referenceToBookCab= referenceToBookCab
                            .child(source.getSelectedItem().toString()+"-"+dest.getSelectedItem().toString());
                    Log.d(TAG, "onClick: "+referenceToBookCab.toString());

                    referenceToBookCab.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                cabmates.add(snapshot.getValue(Cabmate.class));
                            }

                            cabmates.add(new Cabmate(user.getPhoneNumber(), username));

                            if (cabmates.size()==4){
                                referenceToBookCab.setValue(null);
                            }else{
                                referenceToBookCab.setValue(cabmates);
                            }

                            final UserProfile userProfile = new UserProfile(username, true);

                            userProfile.setBookedNumber(cabmates.get(0).getPhone());

                            userDetailsReference.setValue(userProfile);

                            final DatabaseReference cabmateReference = FirebaseDatabase.getInstance()
                                    .getReference("GROUPS")
                                    .child(cabmates.get(0).getPhone())
                                    .child("CABMATES");

                            cabmateReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                ArrayList<UserProfile> cabmates = new ArrayList<>();
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot: dataSnapshot.getChildren())
                                    {
                                        cabmates.add(snapshot.getValue(UserProfile.class));
                                    }
                                    cabmates.add(userProfile);
                                    cabmateReference.setValue(cabmates);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            startActivity(new Intent(getApplicationContext(), ChatActivity.class)
                                    .putExtra("reference", cabmates.get(0).getPhone()).putExtra("username", username));
                            finish();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });

        }



    }

    private void setLogOutButton() {
        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
    }
}

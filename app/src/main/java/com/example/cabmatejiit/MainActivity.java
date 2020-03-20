package com.example.cabmatejiit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    private DatabaseReference userDetailsReference;

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
            userDetailsReference = FirebaseDatabase
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
                            setBookingButton();

                        }
                    }catch (Exception e){
                        Log.d(TAG, "onDataChange: "+e);
                        pd.dismiss();

                        if (userProfileInformations==null){
                            ProfileDialog profileDialog = new ProfileDialog(MainActivity.this, null, MainActivity.this);
                            profileDialog.setCancelable(false);
                            profileDialog.show();
                        }
                        setBookingButton();
                    }
                    
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

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
    private void setBookingButton()
    {
        findCab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<UserProfile> cabmates = new ArrayList<>();
                if (source.getSelectedItemPosition()!=0 && dest.getSelectedItemPosition()!=0)
                {
                    referenceToBookCab= referenceToBookCab
                            .child(source.getSelectedItem().toString()+"-"+dest.getSelectedItem().toString());
                    Log.d(TAG, "onClick: "+referenceToBookCab.toString());

                    referenceToBookCab.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                cabmates.add(snapshot.getValue(UserProfile.class));
                            }

                            cabmates.add(userProfileInformations);

                            if (cabmates.size()==4){
                                referenceToBookCab.setValue(null);
                            }else{
                                referenceToBookCab.setValue(cabmates);
                            }

                            //final UserProfile userProfile = new UserProfile(username, true);

                            //userProfile.setPhone(user.getPhoneNumber());
                            userProfileInformations.setAlreadyBooked(true);
                            userProfileInformations.setBookedNumber(cabmates.get(0).getPhone());
                            userProfileInformations.setPathBooked(source.getSelectedItem().toString()+"-"+dest.getSelectedItem().toString());

                            userDetailsReference.setValue(userProfileInformations);

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
                                    cabmates.add(userProfileInformations);
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
                }else{
                    Toast.makeText(MainActivity.this, "Check Source and Destination", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    public static final int PICK_IMAGE = 100;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE && resultCode==RESULT_OK) {

            if (user.getPhoneNumber()!=null)
            {
                StorageReference imageStorageReference = FirebaseStorage.getInstance().getReference("DISPLAY_PICTURES");
                imageStorageReference=imageStorageReference.child(user.getUid());

                Uri selectedImage = data.getData();
                //todo

                if (selectedImage != null) {
                    imageStorageReference.putFile(selectedImage);
                }

            }


        }
    }
}

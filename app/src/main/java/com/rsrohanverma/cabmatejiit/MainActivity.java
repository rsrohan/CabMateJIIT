package com.rsrohanverma.cabmatejiit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.rsrohanverma.cabmatejiit.JavaClass.GroupDetails;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class MainActivity extends AppCompatActivity {

    Spinner source, dest, seatNumber;
    Button findCab, newGroup;
    FirebaseUser user;
    DatabaseReference referenceToBookCab;

    UserProfile userProfileInformations;

    String username;
    private String TAG = "MainActivity";
    private DatabaseReference userDetailsReference;
    private DatabaseReference userDetailsReference2;
    private Cabmate cabbie;
    private AdView adView;
    private AdRequest adRequest, adRequest2;
    private AdView adView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        source = findViewById(R.id.source);
        dest = findViewById(R.id.destination);
        findCab = findViewById(R.id.findCab);
        seatNumber = findViewById(R.id.numberofseats);
        newGroup = findViewById(R.id.newGroup);
        user = FirebaseAuth.getInstance().getCurrentUser();

        adView = findViewById(R.id.bannerAd);
        MobileAds.initialize(this, "ca-app-pub-3940256099942544/6300978111");
        adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView2 = findViewById(R.id.bannerAd2);
        MobileAds.initialize(this, "ca-app-pub-3940256099942544/6300978111");
        adRequest2 = new AdRequest.Builder().build();
        adView2.loadAd(adRequest2);

        cabbie=new Cabmate();
        userDetailsReference = FirebaseDatabase.getInstance().getReference("USER_DETAILS")
                .child(user.getPhoneNumber()).child("pathBooked");

        userDetailsReference2 = FirebaseDatabase.getInstance().getReference("USER_DETAILS")
                .child(user.getPhoneNumber()).child("alreadyBooked");
        FirebaseDatabase.getInstance().getReference("USER_DETAILS").child(user.getPhoneNumber()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cabbie.setName(dataSnapshot.getValue(String.class));
                //setLogOutButton();

                setBookingButton();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(MainActivity.this, ""+databaseError, Toast.LENGTH_SHORT).show();
            }
        });


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

    private void setBookingButton() {
        findCab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (source.getSelectedItemPosition() != 0
                        && dest.getSelectedItemPosition() != 0
                        && seatNumber.getSelectedItemPosition() != 0
                        && !source.getSelectedItem().toString().equals(dest.getSelectedItem().toString())) {
                    String sourceS = source.getSelectedItem().toString().trim();
                    String destinationS = dest.getSelectedItem().toString().trim();
                    String seatNumberS = seatNumber.getSelectedItem().toString();
                    //cabbie = new Cabmate(user.getPhoneNumber(), seatNumberS, sourceS, destinationS);
                    cabbie.setPhone(user.getPhoneNumber());
                    cabbie.setNumberofseats(seatNumberS);
                    cabbie.setSource(sourceS);
                    cabbie.setDestination(destinationS);

                    checkForSeats(cabbie);


                } else {
                    Toast.makeText(MainActivity.this, "Check Source and Destination", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void checkForSeats(final Cabmate cabbie) {
        Log.d(TAG, "checkForSeats: " + cabbie);
        referenceToBookCab = FirebaseDatabase
                .getInstance()
                .getReference("BOOKINGS").child(cabbie.getSource() + "-" + cabbie.getDestination());
        referenceToBookCab.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                findViewById(R.id.bottomLayout).setVisibility(View.GONE);
                findViewById(R.id.topLayout).setVisibility(View.VISIBLE);


                ArrayList<GroupDetails> groupDetails = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GroupDetails groupDetails1 = snapshot.getValue(GroupDetails.class);
                    groupDetails.add(groupDetails1);
                }
                RecyclerView recyclerView = findViewById(R.id.recyclerView);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                if (groupDetails.size() > 0) {

                    recyclerView.setVisibility(View.VISIBLE);
                    findViewById(R.id.noGroupsFound).setVisibility(View.GONE);

                    RecyclerAdapterForGroupFinding recyclerAdapterForGroupFinding = new RecyclerAdapterForGroupFinding(getApplicationContext(), groupDetails, cabbie, referenceToBookCab, userDetailsReference);
                    recyclerAdapterForGroupFinding.notifyDataSetChanged();
                    recyclerView.setAdapter(recyclerAdapterForGroupFinding);

                }else{
                    findViewById(R.id.noGroupsFound).setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);

                }
                newGroup.setVisibility(View.VISIBLE);
                newGroup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<Cabmate> cabmateArrayList = new ArrayList<>();
                        cabmateArrayList.add(cabbie);
                        GroupDetails g = new GroupDetails(4 - Integer.parseInt(cabbie.getNumberofseats()), cabmateArrayList);
                        String key = String.valueOf(System.currentTimeMillis());
                        g.setUniqueGroupName(key);
                        referenceToBookCab.child(key).setValue(g);
                        userDetailsReference2.setValue(true);
                        userDetailsReference.setValue(referenceToBookCab.child(String.valueOf(key)).toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(getApplicationContext(), ChatActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                userDetailsReference2.setValue(false);
                                Toast.makeText(MainActivity.this, ""+e, Toast.LENGTH_SHORT).show();
                                findViewById(R.id.bottomLayout).setVisibility(View.VISIBLE);
                                findViewById(R.id.topLayout).setVisibility(View.GONE);


                            }
                        });


                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public static final int PICK_IMAGE = 100;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {

            if (user.getPhoneNumber() != null) {
                StorageReference imageStorageReference = FirebaseStorage.getInstance().getReference("DISPLAY_PICTURES");
                imageStorageReference = imageStorageReference.child(user.getUid());

                Uri selectedImage = data.getData();
                //todo

                if (selectedImage != null) {
                    imageStorageReference.putFile(selectedImage);
                }

            }


        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_act, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.icon) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);

    }
}
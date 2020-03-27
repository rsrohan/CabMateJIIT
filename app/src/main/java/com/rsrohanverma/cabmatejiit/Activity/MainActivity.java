package com.rsrohanverma.cabmatejiit.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.rsrohanverma.cabmatejiit.JavaClass.Cabmate;
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
import com.rsrohanverma.cabmatejiit.JavaClass.UserProfile;
import com.rsrohanverma.cabmatejiit.R;
import com.rsrohanverma.cabmatejiit.RecyclerAdapter.RecyclerAdapterForGroupFinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

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
    private ProgressDialog dialog;
    private Activity activity;
    private InterstitialAd interstitialAd;
    private DatabaseReference referenceToBookCab2;
    private String currentDateAndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        activity = this;
        source = findViewById(R.id.source);
        dest = findViewById(R.id.destination);
        findCab = findViewById(R.id.findCab);
        seatNumber = findViewById(R.id.numberofseats);
        newGroup = findViewById(R.id.newGroup);
        user = FirebaseAuth.getInstance().getCurrentUser();

        currentDateAndTime = new SimpleDateFormat("dd_MMM_yyyy-HH:mm").format(new Date());

        dialog = ProgressDialog.show(MainActivity.this, "", "Please Wait...");
        dialog.setCancelable(false);
        try {
            dialog.show();

        } catch (Exception Ignored) {
        }
        adView = findViewById(R.id.bannerAd);
        MobileAds.initialize(this, "ca-app-pub-7233191134291345/6835863059");
        adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView2 = findViewById(R.id.bannerAd2);
        MobileAds.initialize(this, "ca-app-pub-7233191134291345/3443412953");
        adRequest2 = new AdRequest.Builder().build();
        adView2.loadAd(adRequest2);
        interstitialAd = new InterstitialAd(getApplicationContext());
        interstitialAd.setAdUnitId("ca-app-pub-7233191134291345/4467277051");
        interstitialAd.loadAd(adRequest);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                }
            }
        }, 3000);

        cabbie = new Cabmate();
        userDetailsReference = FirebaseDatabase.getInstance().getReference("USER_DETAILS")
                .child(user.getPhoneNumber()).child("pathBooked");

        userDetailsReference2 = FirebaseDatabase.getInstance().getReference("USER_DETAILS")
                .child(user.getPhoneNumber()).child("alreadyBooked");
        FirebaseDatabase.getInstance().getReference("USER_DETAILS").child(user.getPhoneNumber()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cabbie.setName(dataSnapshot.getValue(String.class));
                //setLogOutButton();

                try {
                    dialog.dismiss();

                } catch (Exception Ignored) {
                }
                setBookingButton();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                try {
                    dialog.dismiss();

                } catch (Exception Ignored) {
                }

                Toast.makeText(MainActivity.this, "" + databaseError, Toast.LENGTH_SHORT).show();
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

                    try {
                        dialog.show();

                    } catch (Exception Ignored) {
                    }
                    String sourceS = source.getSelectedItem().toString().trim();
                    String destinationS = dest.getSelectedItem().toString().trim();
                    String seatNumberS = seatNumber.getSelectedItem().toString();
                    //cabbie = new Cabmate(user.getPhoneNumber(), seatNumberS, sourceS, destinationS);
                    cabbie.setPhone(user.getPhoneNumber());
                    cabbie.setNumberofseats(seatNumberS);
                    cabbie.setSource(sourceS);
                    cabbie.setDestination(destinationS);

                    //Calendar calendar = Calendar.getInstance();

                    String currentDateAndTime = new SimpleDateFormat("HH:mm").format(new Date());


                    cabbie.setTimestamp(currentDateAndTime);


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
                .getReference("BOOKINGS").child(cabbie.getSource() + "-" + cabbie.getDestination()).child(currentDateAndTime.substring(0, currentDateAndTime.indexOf("-")));
        referenceToBookCab2 = FirebaseDatabase
                .getInstance()
                .getReference("SAVED_BOOKINGS").child(cabbie.getSource() + "-" + cabbie.getDestination());
        referenceToBookCab.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                findViewById(R.id.bottomLayout).setVisibility(View.GONE);
                findViewById(R.id.topLayout).setVisibility(View.VISIBLE);

                ArrayList<GroupDetails> groupDetails = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GroupDetails groupDetails1 = snapshot.getValue(GroupDetails.class);
                    if (System.currentTimeMillis()-Long.parseLong(groupDetails1.getUniqueGroupName()) <= 60000*60
                            && groupDetails1.getNumberOfVacantSeats()>0
                            && groupDetails1.getNumberOfVacantSeats()<4)
                    {
                        groupDetails.add(groupDetails1);

                    }

                }
                RecyclerView recyclerView = findViewById(R.id.recyclerView);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                if (groupDetails.size() > 0) {

                    recyclerView.setVisibility(View.VISIBLE);
                    findViewById(R.id.noGroupsFound).setVisibility(View.GONE);

                    try {
                        dialog.dismiss();

                    } catch (Exception Ignored) {
                    }
                    RecyclerAdapterForGroupFinding recyclerAdapterForGroupFinding = new RecyclerAdapterForGroupFinding(getApplicationContext(), groupDetails, cabbie, referenceToBookCab, userDetailsReference, activity);
                    recyclerAdapterForGroupFinding.notifyDataSetChanged();
                    recyclerView.setAdapter(recyclerAdapterForGroupFinding);

                } else {
                    try {
                        dialog.dismiss();

                    } catch (Exception Ignored) {
                    }
                    findViewById(R.id.noGroupsFound).setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);

                }
                newGroup.setVisibility(View.VISIBLE);
                newGroup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            dialog.show();
                            newGroup.setClickable(false);

                        } catch (Exception Ignored) {
                        }
                        ArrayList<Cabmate> cabmateArrayList = new ArrayList<>();
                        cabmateArrayList.add(cabbie);
                        GroupDetails g = new GroupDetails(4 - Integer.parseInt(cabbie.getNumberofseats()), cabmateArrayList);
                        String key = String.valueOf(System.currentTimeMillis());
                        g.setUniqueGroupName(key);
                        g.setTimestamp(currentDateAndTime);
                        g.setCabbies_backup(cabmateArrayList);
                        referenceToBookCab.child(key).setValue(g);
                        //referenceToBookCab2.child(key).setValue(g);
                        userDetailsReference2.setValue(true);
                        userDetailsReference.setValue(referenceToBookCab.child(String.valueOf(key)).toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                try {
                                    dialog.dismiss();

                                } catch (Exception Ignored) {
                                }
                                startActivity(new Intent(getApplicationContext(), ChatActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                try {
                                    dialog.dismiss();

                                } catch (Exception Ignored) {
                                }
                                newGroup.setClickable(true);

                                userDetailsReference2.setValue(false);
                                Toast.makeText(MainActivity.this, "" + e, Toast.LENGTH_SHORT).show();
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
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Warning")
                    .setMessage("Are you sure you want to LogOut?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            finish();


                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        return super.onOptionsItemSelected(item);

    }
}

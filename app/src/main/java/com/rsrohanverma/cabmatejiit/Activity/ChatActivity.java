package com.rsrohanverma.cabmatejiit.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.smartreply.FirebaseSmartReply;
import com.google.firebase.ml.naturallanguage.smartreply.FirebaseTextMessage;
import com.google.firebase.ml.naturallanguage.smartreply.SmartReplySuggestion;
import com.google.firebase.ml.naturallanguage.smartreply.SmartReplySuggestionResult;
import com.rsrohanverma.cabmatejiit.JavaClass.Cabmate;
import com.rsrohanverma.cabmatejiit.JavaClass.GroupDetails;
import com.rsrohanverma.cabmatejiit.JavaClass.Message;
import com.rsrohanverma.cabmatejiit.JavaClass.UserProfile;
import com.rsrohanverma.cabmatejiit.R;
import com.rsrohanverma.cabmatejiit.RecyclerAdapter.RecyclerAdapterForCabmates;
import com.rsrohanverma.cabmatejiit.RecyclerAdapter.RecyclerAdapterForChat;
import com.rsrohanverma.cabmatejiit.RecyclerAdapter.RecyclerAdapterForSmartReply;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    DatabaseReference chatReference, cabmateDetailsReference;
    ArrayList<Message> messagesInGroup;
    private RecyclerView recyclerView;
    FirebaseUser user;
    EditText messageBox;
    final List<FirebaseTextMessage> conversation = new ArrayList<>();

    TextView groupId;

    ImageButton sendButton;
    private String TAG = "ChatActivity";
    private RecyclerView recyclerView2;
    private RecyclerView recyclerView3;
    private DatabaseReference referenceToUserDetails;

    UserProfile userProfileDetails;
    private AdView adView;
    private AdRequest adRequest;
    Activity activity;
    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        messageBox = findViewById(R.id.messageBox);
        sendButton = findViewById(R.id.sendBtn);
        groupId = findViewById(R.id.text);

        activity = this;

        adView = findViewById(R.id.bannerAd);
        MobileAds.initialize(this, "ca-app-pub-7233191134291345/2555714992");
        adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        //cabmatesAfterLeavingGroup = new ArrayList<>();
        messagesInGroup = new ArrayList<>();


        interstitialAd = new InterstitialAd(getApplicationContext());
        interstitialAd.setAdUnitId("ca-app-pub-7233191134291345/9719603739");
        interstitialAd.loadAd(adRequest);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                }
            }
        }, 3000);

        setupRecyclerViews();


        user = FirebaseAuth.getInstance().getCurrentUser();
        checkifusernull();

        referenceToUserDetails = FirebaseDatabase
                .getInstance()
                .getReference("USER_DETAILS")
                .child(user.getPhoneNumber());

        userProfileDetails = new UserProfile();
        referenceToUserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userProfileDetails = dataSnapshot.getValue(UserProfile.class);
                Log.d(TAG, "onCreate: " + userProfileDetails.getPathBooked());
                setCabmateDetails();
                setChatWindow();
                FirebaseDatabase.getInstance().getReferenceFromUrl(userProfileDetails.getPathBooked()).child("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String id = dataSnapshot.getValue(String.class);
                        //String s = groupId.getText().toString();

                        groupId.setText("Time of creation of group - "+id.substring(id.indexOf("-")+1));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
//                FirebaseDatabase.getInstance().getReferenceFromUrl(userProfileDetails.getPathBooked()).child("uniqueGroupName").addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        String id = dataSnapshot.getValue(String.class);
//                        groupId.setText("Group Id: " + id);
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!messageBox.getText().toString().equals("")) {
                    final Message m = new Message(messageBox.getText().toString(), userProfileDetails.getName(), user.getPhoneNumber());

                    String currentDateAndTime = new SimpleDateFormat("HH:mm").format(new Date());
                    m.setTimestamp(currentDateAndTime);

                    chatReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ArrayList<Message> messageArrayList = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                messageArrayList.add(snapshot.getValue(Message.class));
                                Log.d(TAG, "onDataChange: " + messageArrayList.size());

                            }
                            messageArrayList.add(m);
                            chatReference.setValue(messageArrayList);


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    //chatReference.setValue(messagesInGroup);
                    messageBox.setText("");


                }
            }
        });


    }

    private void setChatWindow() {
        chatReference = FirebaseDatabase.getInstance().getReferenceFromUrl(userProfileDetails.getPathBooked()).child("chats");
        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    ArrayList<Message> messageArrayList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        messageArrayList.add(snapshot.getValue(Message.class));
                        Log.d(TAG, "onDataChange: " + messageArrayList.size());

                    }

                    Log.d(TAG, "onDataChange: " + messageArrayList.toArray().toString());

                    RecyclerAdapterForChat recyclerAdapterForChat = new RecyclerAdapterForChat(getApplicationContext(), messageArrayList, user.getPhoneNumber());
                    recyclerAdapterForChat.notifyDataSetChanged();
                    recyclerView.setAdapter(recyclerAdapterForChat);
                    recyclerView.scrollToPosition(messageArrayList.size() - 1);


                    messagesInGroup = messageArrayList;

                    for (int i = 0; i < messagesInGroup.size(); i++) {
                        if (messagesInGroup.get(i).getNumber().equals(user.getPhoneNumber())) {
                            conversation.add(FirebaseTextMessage
                                    .createForLocalUser(messagesInGroup.get(i).getMessage(),
                                            System.currentTimeMillis()));

                        } else {

                            conversation.add(FirebaseTextMessage.createForRemoteUser(
                                    messagesInGroup.get(i).getMessage(),
                                    System.currentTimeMillis(),
                                    messagesInGroup.get(i).getNumber()));
                        }
                    }

                    try {
                        FirebaseSmartReply smartReply = FirebaseNaturalLanguage.getInstance().getSmartReply();
                        smartReply.suggestReplies(conversation)
                                .addOnSuccessListener(new OnSuccessListener<SmartReplySuggestionResult>() {
                                    @Override
                                    public void onSuccess(SmartReplySuggestionResult result) {

                                        if (result.getStatus() == SmartReplySuggestionResult.STATUS_NOT_SUPPORTED_LANGUAGE) {
                                            // The conversation's language isn't supported, so the
                                            // the result doesn't contain any suggestions.
                                        } else if (result.getStatus() == SmartReplySuggestionResult.STATUS_SUCCESS) {
                                            ArrayList<String> arrayList = new ArrayList<>();
                                            for (SmartReplySuggestion suggestion : result.getSuggestions()) {
                                                String replyText = suggestion.getText();
                                                Log.d(TAG, "onSuccess: " + replyText);
                                                arrayList.add(replyText);
                                            }
                                            RecyclerAdapterForSmartReply recyclerAdapterForSmartReply = new RecyclerAdapterForSmartReply(getApplicationContext(), arrayList, chatReference, userProfileDetails.getName(), user.getPhoneNumber(), messagesInGroup);
                                            recyclerAdapterForSmartReply.notifyDataSetChanged();
                                            recyclerView3.setAdapter(recyclerAdapterForSmartReply);
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                    }
                                });
                    } catch (Exception e) {
                        Log.d(TAG, "onCreate: " + e);
                    }

                } catch (Exception e) {
                    Log.d(TAG, "onDataChange: " + e);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setCabmateDetails() {
        cabmateDetailsReference = FirebaseDatabase.getInstance().getReferenceFromUrl(userProfileDetails.getPathBooked()).child("cabbies");
        cabmateDetailsReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Cabmate> cabmates = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    cabmates.add(snapshot.getValue(Cabmate.class));
                }
                Log.d(TAG, "onDataChange: " + cabmates);

                RecyclerAdapterForCabmates recyclerAdapterForCabmates = new RecyclerAdapterForCabmates(getApplicationContext(), cabmates, activity);
                recyclerAdapterForCabmates.notifyDataSetChanged();
                recyclerView2.setAdapter(recyclerAdapterForCabmates);

                //cabmatesAfterLeavingGroup = cabmates;
                //Toast.makeText(ChatActivity.this, ""+cabmatesAfterLeavingGroup.size(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void checkifusernull() {
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void setupRecyclerViews() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView2 = findViewById(R.id.recyclerViewForCabmates);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        recyclerView3 = findViewById(R.id.recyclerViewsmart);
        recyclerView3.setHasFixedSize(true);
        recyclerView3.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.icon) {
            new AlertDialog.Builder(ChatActivity.this)
                    .setTitle("Warning")
                    .setMessage("Are you sure you want to leave this group?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseDatabase.getInstance().getReferenceFromUrl(userProfileDetails.getPathBooked()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    GroupDetails groupDetails = dataSnapshot.getValue(GroupDetails.class);
                                    for (int i = 0; i < groupDetails.getCabbies().size(); i++) {
                                        if (groupDetails.getCabbies().get(i).getPhone().equals(userProfileDetails.getPhone())) {
                                            groupDetails.setNumberOfVacantSeats(groupDetails.getNumberOfVacantSeats() + Integer.parseInt(groupDetails.getCabbies().get(i).getNumberofseats()));
                                            groupDetails.getCabbies().remove(i);
                                            if (groupDetails.getCabbies_backup().size() == 1) {
                                                FirebaseDatabase.getInstance().getReferenceFromUrl(userProfileDetails.getPathBooked()).setValue(null);

                                            } else {
                                                FirebaseDatabase.getInstance().getReferenceFromUrl(userProfileDetails.getPathBooked()).setValue(groupDetails);

                                            }
                                            userProfileDetails.setPathBooked("null");
                                            userProfileDetails.setAlreadyBooked(false);
                                            FirebaseDatabase.getInstance().getReference("USER_DETAILS").child(user.getPhoneNumber()).setValue(userProfileDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                    finish();
                                                }
                                            });
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}

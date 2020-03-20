package com.example.cabmatejiit;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    DatabaseReference chatReference, cabmateDetailsReference;
    DatabaseReference referenceToGroup;
    ArrayList<Message> messagesInGroup;
    private RecyclerView recyclerView;
    FirebaseUser user;
    EditText messageBox;
    final List<FirebaseTextMessage> conversation = new ArrayList<>();

    ArrayList<UserProfile> cabmatesAfterLeavingGroup;

    ImageButton sendButton;
    private String TAG = "ChatActivity";
    private RecyclerView recyclerView2;
    private RecyclerView recyclerView3;
    private String ref;
    private DatabaseReference referenceToUserDetails;
    private DatabaseReference referenceToLiveBookings;

    UserProfile userProfileDetails;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        messageBox = findViewById(R.id.messageBox);
        sendButton = findViewById(R.id.sendBtn);

        cabmatesAfterLeavingGroup = new ArrayList<>();
        messagesInGroup = new ArrayList<>();



        setupRecyclerViews();


        user = FirebaseAuth.getInstance().getCurrentUser();
        checkifusernull();

        ref = getIntent().getStringExtra("reference");

        referenceToUserDetails = FirebaseDatabase
                .getInstance()
                .getReference("USER_DETAILS")
                .child(user.getPhoneNumber());
        referenceToLiveBookings = FirebaseDatabase.getInstance().getReference("LIVE_BOOKINGS");
        referenceToGroup = FirebaseDatabase.getInstance().getReference("GROUPS");
        chatReference = referenceToGroup.child(Objects.requireNonNull(ref)).child("CHATS");
        cabmateDetailsReference = referenceToGroup.child(Objects.requireNonNull(ref)).child("CABMATES");

        userProfileDetails=new UserProfile();
        referenceToUserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userProfileDetails = dataSnapshot.getValue(UserProfile.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        cabmateDetailsReference.addValueEventListener(new ValueEventListener() {
            ArrayList<UserProfile> cabmates = new ArrayList<>();

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    cabmates.add(snapshot.getValue(UserProfile.class));
                }
                Log.d(TAG, "onDataChange: " + cabmates);

                RecyclerAdapterForCabmates recyclerAdapterForCabmates = new RecyclerAdapterForCabmates(getApplicationContext(), cabmates);
                recyclerAdapterForCabmates.notifyDataSetChanged();
                recyclerView2.setAdapter(recyclerAdapterForCabmates);

                cabmatesAfterLeavingGroup = cabmates;
                Toast.makeText(ChatActivity.this, ""+cabmatesAfterLeavingGroup.size(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


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
                                            RecyclerAdapterForSmartReply recyclerAdapterForSmartReply = new RecyclerAdapterForSmartReply(getApplicationContext(), arrayList, chatReference, getIntent().getStringExtra("username"), user.getPhoneNumber(), messagesInGroup);
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

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!messageBox.getText().toString().equals("")) {
                    Message m = new Message(messageBox.getText().toString(), getIntent().getStringExtra("username"), user.getPhoneNumber());

                    messagesInGroup.add(m);


                    chatReference.setValue(messagesInGroup);
                    messageBox.setText("");
                    try {
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                    }


                }
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
                            // Continue with delete operation
                            for (int i = 0; i < cabmatesAfterLeavingGroup.size(); i++) {

                                if (cabmatesAfterLeavingGroup.get(i).getPhone().equals(user.getPhoneNumber())) {
                                    cabmatesAfterLeavingGroup.remove(i);
                                    //recyclerAdapterForCabmates.notifyDataSetChanged();
                                    if (ref.equals(user.getPhoneNumber()) && cabmatesAfterLeavingGroup.size()>0) {
                                        final DatabaseReference databaseReferenceAfterLeaving = referenceToGroup
                                                .child(cabmatesAfterLeavingGroup.get(0).getPhone());
                                        referenceToGroup.child(ref).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                databaseReferenceAfterLeaving.setValue(dataSnapshot.getValue());
                                                for (int i = 0; i < cabmatesAfterLeavingGroup.size(); i++) {
                                                    cabmatesAfterLeavingGroup.get(i).setBookedNumber(cabmatesAfterLeavingGroup.get(0).getPhone());
                                                }
                                                referenceToGroup.child(cabmatesAfterLeavingGroup.get(0).getPhone())
                                                        .child("CABMATES")
                                                        .setValue(cabmatesAfterLeavingGroup);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                    }else{
                                        referenceToGroup.child(ref).setValue(null);
                                    }
                                    userProfileDetails.setAlreadyBooked(false);
                                    userProfileDetails.setBookedNumber("");
                                    referenceToUserDetails
                                            .setValue(userProfileDetails);


                                    referenceToLiveBookings.child(userProfileDetails.getPathBooked()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snapshot:dataSnapshot.getChildren())
                                            {
                                                if (snapshot.child("phone").getValue(String.class).equals(user.getPhoneNumber()))
                                                {
                                                    referenceToLiveBookings.child(userProfileDetails.getPathBooked()).child(snapshot.getKey()).setValue(null);
                                                    Log.d(TAG, "onDataChange: "+snapshot.getKey());
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();
                                }
                            }

                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        return super.onOptionsItemSelected(item);

    }

}

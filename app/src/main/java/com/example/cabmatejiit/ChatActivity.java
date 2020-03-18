package com.example.cabmatejiit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

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

    DatabaseReference chatReference, cabmateReference;
    DatabaseReference referenceToGroup;
    ArrayList<Message> messages;
    private RecyclerView recyclerView;
    FirebaseUser user;
    EditText messageBox;
    final List<FirebaseTextMessage> conversation = new ArrayList<>();

    ArrayList<UserProfile> cabmates2;

    ImageButton sendButton;
    private String TAG = "ChatActivity";
    private RecyclerView recyclerView2;
    private RecyclerView recyclerView3;
    private RecyclerAdapterForCabmates recyclerAdapterForCabmates;
    private String ref;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        messageBox = findViewById(R.id.messageBox);
        sendButton = findViewById(R.id.sendBtn);

        cabmates2 = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView2 = findViewById(R.id.recyclerViewForCabmates);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        user = FirebaseAuth.getInstance().getCurrentUser();
        ref = getIntent().getStringExtra("reference");

        referenceToGroup = FirebaseDatabase.getInstance().getReference("GROUPS");
        chatReference = referenceToGroup.child(Objects.requireNonNull(ref)).child("CHATS");
        cabmateReference = referenceToGroup.child(Objects.requireNonNull(ref)).child("CABMATES");

        cabmateReference.addValueEventListener(new ValueEventListener() {
            ArrayList<UserProfile> cabmates = new ArrayList<>();

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    cabmates.add(snapshot.getValue(UserProfile.class));
                }
                Log.d(TAG, "onDataChangeee: " + cabmates);

                recyclerAdapterForCabmates = new RecyclerAdapterForCabmates(getApplicationContext(), cabmates);
                recyclerAdapterForCabmates.notifyDataSetChanged();
                recyclerView2.setAdapter(recyclerAdapterForCabmates);

                cabmates2 = cabmates;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        messages = new ArrayList<>();

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


                    messages = messageArrayList;

                    for (int i = 0; i < messages.size(); i++) {
                        if (messages.get(i).getNumber().equals(user.getPhoneNumber())) {
                            conversation.add(FirebaseTextMessage
                                    .createForLocalUser(messages.get(i).getMessage(),
                                            System.currentTimeMillis()));

                        } else {

                            conversation.add(FirebaseTextMessage.createForRemoteUser(
                                    messages.get(i).getMessage(),
                                    System.currentTimeMillis(),
                                    messages.get(i).getNumber()));
                        }
                    }

                    try {
                        FirebaseSmartReply smartReply = FirebaseNaturalLanguage.getInstance().getSmartReply();
                        smartReply.suggestReplies(conversation)
                                .addOnSuccessListener(new OnSuccessListener<SmartReplySuggestionResult>() {
                                    @Override
                                    public void onSuccess(SmartReplySuggestionResult result) {
                                        recyclerView3 = findViewById(R.id.recyclerViewsmart);
                                        recyclerView3.setHasFixedSize(true);
                                        recyclerView3.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
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
                                            RecyclerAdapterForSmartReply recyclerAdapterForSmartReply = new RecyclerAdapterForSmartReply(getApplicationContext(), arrayList, chatReference, getIntent().getStringExtra("username"), user.getPhoneNumber(), messages);
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

                    messages.add(m);


                    chatReference.setValue(messages);
                    messageBox.setText("");
                    try {
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }


                }
            }
        });


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
            new AlertDialog.Builder(getApplicationContext())
                    .setTitle("Warning")
                    .setMessage("Are you sure you want to leave this group?")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Continue with delete operation
                            for (int i = 0; i < cabmates2.size(); i++) {
                                if (cabmates2.get(i).getPhone().equals(user.getPhoneNumber())) {
                                    cabmates2.remove(i);
                                    recyclerAdapterForCabmates.notifyDataSetChanged();
                                    if (ref.equals(user.getPhoneNumber())) {
                                        final DatabaseReference databaseReferenceAfterLeaving = referenceToGroup
                                                .child(cabmates2.get(0).getPhone());
                                        referenceToGroup.child(ref).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                databaseReferenceAfterLeaving.setValue(dataSnapshot.getValue());
                                                for (int i = 0; i < cabmates2.size(); i++) {
                                                    cabmates2.get(i).setBookedNumber(cabmates2.get(0).getPhone());
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                    }
                                    FirebaseDatabase
                                            .getInstance()
                                            .getReference("USER_DETAILS")
                                            .child(user.getPhoneNumber())
                                            .child("alreadyBooked")
                                            .setValue("false");
                                    FirebaseDatabase
                                            .getInstance()
                                            .getReference("USER_DETAILS")
                                            .child(user.getPhoneNumber())
                                            .child("bookedNumber")
                                            .setValue("");


                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();
                                }
                            }

                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        return super.onOptionsItemSelected(item);

    }

}

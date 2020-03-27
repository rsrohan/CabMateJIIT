package com.rsrohanverma.cabmatejiit.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.rsrohanverma.cabmatejiit.JavaClass.UserProfile;
import com.rsrohanverma.cabmatejiit.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileDialog extends Dialog implements DialogInterface.OnClickListener {

    EditText username;

    TextView message;
    ProgressBar progressBar;
    CircleImageView dp;
    Spinner gender;
    Button save;
    Context context;
    String name;
    DatabaseReference reference;
    FirebaseUser user;
    StorageReference imageReference;
    private int PICK_IMAGE = 100;

    Activity activity;
    public ProfileDialog(@NonNull Context context, String name, Activity activity) {
        super(context);
        this.name = name;
        this.context = context;
        this.activity=activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.userprofiledialog);
        username = findViewById(R.id.username);
        save = findViewById(R.id.save);
        message = findViewById(R.id.message);
        progressBar = findViewById(R.id.progressbar);
        dp = findViewById(R.id.displayPicture);
        gender = findViewById(R.id.gender);

        if (name != null) {
            username.setText(name);
        }
        user = FirebaseAuth.getInstance().getCurrentUser();


        dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK);
                pickIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                activity.startActivityForResult(chooserIntent, PICK_IMAGE);
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gender.getSelectedItemPosition()!=0)
                {
                    if (!username.getText().toString().isEmpty()) {
                        try {
                            save.setVisibility(View.GONE);
                            gender.setVisibility(View.GONE);
                            //dp.setVisibility(View.GONE);
                            message.setText(R.string.pleasewait);
                            username.setVisibility(View.GONE);
                            progressBar.setVisibility(View.VISIBLE);

                            final UserProfile userProfile = new UserProfile();
                            userProfile.setPhone(user.getPhoneNumber());
                            userProfile.setName(username.getText().toString().toUpperCase());
                            userProfile.setGender(gender.getSelectedItem().toString());
                            try{
                                imageReference.child(user.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        userProfile.setImageURL(uri.toString());
                                        setProfile(userProfile);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        userProfile.setImageURL("null");
                                        setProfile(userProfile);
                                    }
                                });
                            }catch (Exception e){
                                userProfile.setImageURL("null");
                                setProfile(userProfile);
                            }



                        } catch (Exception e) {
                            Toast.makeText(context, "" + e, Toast.LENGTH_SHORT).show();

                            Log.d("tag", "onClick: "+e);
                        }


                    }else{
                        Toast.makeText(context, "Enter Full Name", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(context, "Select Gender", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void setProfile( UserProfile userProfile) {
        reference=FirebaseDatabase.getInstance().getReference("USER_DETAILS").child(user.getPhoneNumber());
        reference.setValue(userProfile).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "SOMETHING WENT WRONG \n" + e, Toast.LENGTH_SHORT).show();
                save.setVisibility(View.VISIBLE);
                message.setText(R.string.entername);
                gender.setVisibility(View.VISIBLE);
                username.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dismiss();
            }
        });
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }



}


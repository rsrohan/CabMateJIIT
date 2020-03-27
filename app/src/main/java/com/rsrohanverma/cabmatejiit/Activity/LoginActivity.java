package com.rsrohanverma.cabmatejiit.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rsrohanverma.cabmatejiit.Dialog.OTPDialog;
import com.rsrohanverma.cabmatejiit.Dialog.ProfileDialog;
import com.rsrohanverma.cabmatejiit.R;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    EditText phone;
    Button sendOTP;
    String regEx = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$";
    Activity activity;
    String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        phone = findViewById(R.id.phoneNumber);
        sendOTP = findViewById(R.id.sendOTP);
        activity = this;


        sendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (Pattern.compile(regEx).matcher(phone.getText().toString()).matches())
                {
                    phoneNumber = "+91"+phone.getText().toString();
                    final OTPDialog otpDialog = new OTPDialog(LoginActivity.this, phone.getText().toString(), activity);
                    otpDialog.setCancelable(false);
                    otpDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (otpDialog.isNewUser())
                            {

                                ProfileDialog profileDialog = new ProfileDialog(activity, null, activity);
                                profileDialog.setCancelable(false);
                                profileDialog.show();
                                profileDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        finish();
                                    }
                                });
                            }else{
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }
                        }
                    });
                    otpDialog.show();

                }else{
                    Toast.makeText(LoginActivity.this, "ARE YOU SURE THIS NUMBER IS VALID ?", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public static final int PICK_IMAGE = 100;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE && resultCode==RESULT_OK) {

            if (phoneNumber!=null)
            {
                StorageReference imageStorageReference = FirebaseStorage.getInstance().getReference("DISPLAY_PICTURES");
                imageStorageReference=imageStorageReference.child(phoneNumber);

                Uri selectedImage = data.getData();

                if (selectedImage != null) {
                    imageStorageReference.putFile(selectedImage);
                }

            }


        }
    }
}

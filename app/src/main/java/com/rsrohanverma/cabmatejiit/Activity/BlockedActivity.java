package com.rsrohanverma.cabmatejiit.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.rsrohanverma.cabmatejiit.Dialog.BlockedDialog;
import com.rsrohanverma.cabmatejiit.R;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class BlockedActivity extends AppCompatActivity {

    private EditText message;
    ImageButton send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked);
        message = findViewById(R.id.messageBox);
        send = findViewById(R.id.sendBtn);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = message.getText().toString();
                if (messageText.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Write something...", Toast.LENGTH_SHORT).show();
                }else{
                    Intent i = new Intent(Intent.ACTION_SEND);
                    //i.setType("message/rfc822");
                    i.setData(Uri.parse("mailto:")); // only email apps should handle this

                    i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"rsrohanverma@gmail.com"});
                    i.putExtra(Intent.EXTRA_SUBJECT, "BLOCKED USER OF CabMateJIIT "+ FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                    i.putExtra(Intent.EXTRA_TEXT   , messageText);
                    try {
                        startActivity(Intent.createChooser(i, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getApplicationContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}

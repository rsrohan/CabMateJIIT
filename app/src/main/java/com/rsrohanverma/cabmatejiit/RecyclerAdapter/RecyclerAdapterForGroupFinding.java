package com.rsrohanverma.cabmatejiit.RecyclerAdapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rsrohanverma.cabmatejiit.Activity.ChatActivity;
import com.rsrohanverma.cabmatejiit.JavaClass.Cabmate;
import com.rsrohanverma.cabmatejiit.JavaClass.GroupDetails;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rsrohanverma.cabmatejiit.R;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class RecyclerAdapterForGroupFinding extends RecyclerView.Adapter<RecyclerAdapterForGroupFinding.MyHolder> {

    Context context;
    ArrayList<GroupDetails> groups;
    Cabmate cabbie;
    DatabaseReference referenceToBookCab, userDetailsReference;
    Activity activity;

    public RecyclerAdapterForGroupFinding(Context context, ArrayList<GroupDetails> groups, Cabmate cabmate, DatabaseReference reference, DatabaseReference reference2, Activity activity2) {
        this.context = context;
        this.groups = groups;
        this.cabbie=cabmate;
        this.referenceToBookCab=reference;
        this.userDetailsReference=reference2;
        this.activity=activity2;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(context).inflate(R.layout.groups_recyclerview, parent, false));    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {

        final GroupDetails GroupDetails = groups.get(position);

        holder.grpId.setText("Group Id:"+GroupDetails.getUniqueGroupName());
        holder.numberOfSeats.setText("Vacant Seats: "+String.valueOf(GroupDetails.getNumberOfVacantSeats()));
        holder.joinGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog dialog =
                        ProgressDialog.show(activity, "", "Please Wait...");
                dialog.setCancelable(false);
                try {
                    dialog.show();

                } catch (Exception Ignored) {
                }

                int numberOfSeatsAvalable =  groups.get(position).getNumberOfVacantSeats();
                if (numberOfSeatsAvalable>=Integer.parseInt(cabbie.getNumberofseats()))
                {
                    groups.get(position).getCabbies().add(cabbie);
                    groups.get(position).setNumberOfVacantSeats(numberOfSeatsAvalable-Integer.parseInt(cabbie.getNumberofseats()));

                    referenceToBookCab.child(groups.get(position).getUniqueGroupName()).setValue(groups.get(position));
                    FirebaseDatabase.getInstance().getReference("USER_DETAILS").child(cabbie.getPhone()).child("alreadyBooked").setValue(true);
                    userDetailsReference.setValue(referenceToBookCab.child(groups.get(position).getUniqueGroupName()).toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            try {
                                dialog.dismiss();

                            } catch (Exception Ignored) {
                            }
                            context.startActivity(new Intent(context, ChatActivity.class).addFlags(FLAG_ACTIVITY_NEW_TASK));
                            //((Activity)context).finish();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            try {
                                dialog.dismiss();

                            } catch (Exception Ignored) {
                            }
                            FirebaseDatabase.getInstance().getReference("USER_DETAILS").child(cabbie.getPhone()).child("alreadyBooked").setValue(false);
                            Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();


                        }
                    });

                }else{
                    Toast.makeText(context, "Seats are not available as per your requirement !", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        TextView numberOfSeats, grpId;
        Button joinGroup;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            numberOfSeats = itemView.findViewById(R.id.numberofseats);
            joinGroup = itemView.findViewById(R.id.joinGroup);
            grpId=itemView.findViewById(R.id.grpId);
        }
    }
}

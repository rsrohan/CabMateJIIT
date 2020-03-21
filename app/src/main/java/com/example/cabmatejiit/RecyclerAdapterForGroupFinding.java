package com.example.cabmatejiit;

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

import com.example.cabmatejiit.JavaClass.GroupDetails;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class RecyclerAdapterForGroupFinding extends RecyclerView.Adapter<RecyclerAdapterForGroupFinding.MyHolder> {

    Context context;
    ArrayList<GroupDetails> groups;
    Cabmate cabbie;
    DatabaseReference referenceToBookCab, userDetailsReference;

    public RecyclerAdapterForGroupFinding(Context context, ArrayList<GroupDetails> groups, Cabmate cabmate, DatabaseReference reference, DatabaseReference reference2) {
        this.context = context;
        this.groups = groups;
        this.cabbie=cabmate;
        this.referenceToBookCab=reference;
        this.userDetailsReference=reference2;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(context).inflate(R.layout.groups_recyclerview, parent, false));    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {

        final GroupDetails GroupDetails = groups.get(position);

        holder.numberOfSeats.setText(String.valueOf(GroupDetails.getNumberOfVacantSeats()));
        holder.joinGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int numberOfSeatsAvalable =  groups.get(position).getNumberOfVacantSeats();
                if (numberOfSeatsAvalable>=Integer.parseInt(cabbie.getNumberofseats()))
                {
                    groups.get(position).getCabbies().add(cabbie);
                    groups.get(position).setNumberOfVacantSeats(numberOfSeatsAvalable-Integer.parseInt(cabbie.getNumberofseats()));
                    referenceToBookCab.setValue(groups);
                    FirebaseDatabase.getInstance().getReference("USER_DETAILS").child(cabbie.getPhone()).child("alreadyBooked").setValue(true);
                    userDetailsReference.setValue(referenceToBookCab.child(groups.get(position).getUniqueGroupName()).toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            context.startActivity(new Intent(context, ChatActivity.class).addFlags(FLAG_ACTIVITY_NEW_TASK));

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
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

        TextView numberOfSeats;
        Button joinGroup;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            numberOfSeats = itemView.findViewById(R.id.numberofseats);
            joinGroup = itemView.findViewById(R.id.joinGroup);
        }
    }
}

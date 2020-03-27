package com.rsrohanverma.cabmatejiit.RecyclerAdapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.rsrohanverma.cabmatejiit.JavaClass.Cabmate;
import com.rsrohanverma.cabmatejiit.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class RecyclerAdapterForCabmates extends RecyclerView.Adapter<RecyclerAdapterForCabmates.MyHolder> {

    Context context;
    ArrayList<Cabmate> cabmates;
    private int REQUEST_PHONE_CALL=1;
    Activity activity;

    public RecyclerAdapterForCabmates(Context context, ArrayList<Cabmate> cabmates, Activity act) {
        this.context = context;
        this.cabmates = cabmates;
        this.activity = act;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(context).inflate(R.layout.cabmates_recyclerview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {

        final Cabmate Cabmate = cabmates.get(position);
//        try{
//            Glide.with(context).asBitmap().load(Cabmate.getImageURL()).addListener(new RequestListener<Bitmap>() {
//                @Override
//                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
//                    return false;
//                }
//
//                @Override
//                public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
//                    holder.circleImageView.setImageBitmap(resource);
//                    return false;
//                }
//            }).into(holder.circleImageView);
//        }catch (Exception e){
//
//        }

        holder.name.setText(Cabmate.getName() + "(" + Cabmate.getNumberofseats() + " Seats)");
        holder.seats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Cabmate.getPhone()));
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }

        });
        holder.report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                //i.setType("email/rfc822");
                i.setData(Uri.parse("mailto:")); // only email apps should handle this

                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"rsrohanverma@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "REPORTING USER OF CabMateJIIT");
                i.putExtra(Intent.EXTRA_TEXT   , "Username: "+Cabmate.getName()+"\nPhone: "+Cabmate.getPhone()+"\nWrite issue:");

                try {
                    //i.addFlags(FLAG_ACTIVITY_NEW_TASK);

                    context.startActivity(Intent.createChooser(i, "Choose Gmail...").addFlags(FLAG_ACTIVITY_NEW_TASK));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return cabmates.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView name, seats, report;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.displayPicture);
            name = itemView.findViewById(R.id.cabmatename);
            seats = itemView.findViewById(R.id.call);
            report=itemView.findViewById(R.id.report);
        }
    }
}

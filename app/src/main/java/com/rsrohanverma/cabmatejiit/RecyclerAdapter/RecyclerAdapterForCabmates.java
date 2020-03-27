package com.rsrohanverma.cabmatejiit.RecyclerAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rsrohanverma.cabmatejiit.JavaClass.Cabmate;
import com.rsrohanverma.cabmatejiit.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerAdapterForCabmates extends RecyclerView.Adapter<RecyclerAdapterForCabmates.MyHolder> {

    Context context;
    ArrayList<Cabmate> cabmates;

    public RecyclerAdapterForCabmates(Context context, ArrayList<Cabmate> cabmates) {
        this.context = context;
        this.cabmates = cabmates;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(context).inflate(R.layout.cabmates_recyclerview, parent, false));    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {

        Cabmate Cabmate = cabmates.get(position);
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

        holder.name.setText(Cabmate.getName());
        holder.seats.setText(Cabmate.getNumberofseats()+" Seats");


    }

    @Override
    public int getItemCount() {
        return cabmates.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView name, seats;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.displayPicture);
            name = itemView.findViewById(R.id.cabmatename);
            seats = itemView.findViewById(R.id.seats);
        }
    }
}

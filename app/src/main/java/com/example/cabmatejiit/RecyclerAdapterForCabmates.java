package com.example.cabmatejiit;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerAdapterForCabmates extends RecyclerView.Adapter<RecyclerAdapterForCabmates.MyHolder> {

    Context context;
    ArrayList<UserProfile> cabmates;

    public RecyclerAdapterForCabmates(Context context, ArrayList<UserProfile> cabmates) {
        this.context = context;
        this.cabmates = cabmates;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(context).inflate(R.layout.cabmates_recyclerview, parent, false));    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {

        UserProfile userProfile = cabmates.get(position);
//        try{
//            Glide.with(context).asBitmap().load(userProfile.getImageURL()).addListener(new RequestListener<Bitmap>() {
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

        holder.name.setText(userProfile.getName());


    }

    @Override
    public int getItemCount() {
        return cabmates.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView name;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.displayPicture);
            name = itemView.findViewById(R.id.cabmatename);
        }
    }
}

package com.rsrohanverma.cabmatejiit.RecyclerAdapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.rsrohanverma.cabmatejiit.JavaClass.Message;
import com.rsrohanverma.cabmatejiit.R;

import java.util.ArrayList;

public class RecyclerAdapterForSmartReply extends RecyclerView.Adapter<RecyclerAdapterForSmartReply.MyHolder>{

    Context context;
    ArrayList<String> reply;
    DatabaseReference reference;
    ArrayList<Message> messages;
    String username, number;

    public RecyclerAdapterForSmartReply(Context context, ArrayList<String> message, DatabaseReference reference, String username, String number, ArrayList<Message> messages) {
        this.context = context;
        this.reply = message;
        this.reference=reference;
        this.username=username;
        this.number=number;
        this.messages = messages;
    }

    @NonNull
    @Override
    public RecyclerAdapterForSmartReply.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new MyHolder(LayoutInflater.from(context).inflate(R.layout.smartreplyrecyclerview, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterForSmartReply.MyHolder holder, final int position) {


        holder.smartReply.setText(reply.get(position));
        holder.smartReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message m = new Message(reply.get(position), username ,number);

                messages.add(m);


                reference.setValue(messages);
            }
        });


    }

    @Override
    public int getItemCount() {
        return reply.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView smartReply;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            smartReply = itemView.findViewById(R.id.smartReply);


        }
    }
    public void setAlphaAnimation(View v) {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(v, "alpha",  1f, .1f);
        fadeOut.setDuration(0);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(v, "alpha", .1f, 1f);
        fadeIn.setDuration(500);

        final AnimatorSet mAnimationSet = new AnimatorSet();

        mAnimationSet.play(fadeIn).after(fadeOut);

        mAnimationSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

            }
        });
        mAnimationSet.start();
    }
}

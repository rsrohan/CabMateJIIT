package com.example.cabmatejiit;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapterForChat extends RecyclerView.Adapter<RecyclerAdapterForChat.MyHolder>{

    Context context;
    ArrayList<Message> messages;
    String userPhone;

    public RecyclerAdapterForChat(Context context, ArrayList<Message> message, String userPhone) {
        this.context = context;
        this.messages = message;
        this.userPhone = userPhone;
    }

    @NonNull
    @Override
    public RecyclerAdapterForChat.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new MyHolder(LayoutInflater.from(context).inflate(R.layout.chat_layout, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterForChat.MyHolder holder, int position) {

        Message message = messages.get(position);

        if (!message.getNumber().equals(userPhone))
        {
            holder.sender.setText(message.getName());
            holder.receivedTxt.setText(message.getMessage());
            setAlphaAnimation(holder.receivedTxt);
            setAlphaAnimation(holder.sender);

            holder.receivedTxt.setVisibility(View.VISIBLE);
            holder.sender.setVisibility(View.VISIBLE);
            holder.sentTxt.setVisibility(View.GONE);

        }else{
            holder.sentTxt.setText(message.getMessage());
            setAlphaAnimation(holder.sentTxt);
            holder.sentTxt.setVisibility(View.VISIBLE);
            holder.receivedTxt.setVisibility(View.GONE);
            holder.sender.setVisibility(View.GONE);

        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView sender, receivedTxt,sentTxt;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            sender = itemView.findViewById(R.id.senderName);
            receivedTxt = itemView.findViewById(R.id.received);
            sentTxt = itemView.findViewById(R.id.sent);

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

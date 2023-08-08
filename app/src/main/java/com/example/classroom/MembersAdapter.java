package com.example.classroom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MembersViewHolder> {

    private List<Users> membersList;

    public MembersAdapter(List<Users> membersList) {
        this.membersList = membersList;
    }

    @NonNull
    @Override
    public MembersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_item, parent, false);
        return new MembersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MembersViewHolder holder, int position) {
        Users member = membersList.get(position);
        holder.memberName.setText(member.getName());

        // Load profile picture using Glide or any other image loading library
        Glide.with(holder.itemView.getContext())
                .load(member.getProfile())
                .into(holder.memberProfile);
    }

    @Override
    public int getItemCount() {
        return membersList.size();
    }

    static class MembersViewHolder extends RecyclerView.ViewHolder {

        ImageView memberProfile;
        TextView memberName;

        public MembersViewHolder(@NonNull View itemView) {
            super(itemView);
            memberProfile = itemView.findViewById(R.id.memberProfile);
            memberName = itemView.findViewById(R.id.memberName);
        }
    }
}

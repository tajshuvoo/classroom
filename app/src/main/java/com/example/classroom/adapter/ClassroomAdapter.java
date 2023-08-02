package com.example.classroom.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.classroom.R;
import com.example.classroom.model.Classroom;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ClassroomAdapter extends RecyclerView.Adapter<ClassroomAdapter.ClassroomViewHolder> {

    private List<Classroom> classroomList;

    public ClassroomAdapter(List<Classroom> classroomList) {
        this.classroomList = classroomList;
    }

    @NonNull
    @Override
    public ClassroomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_classroom, parent, false);
        return new ClassroomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassroomViewHolder holder, int position) {
        Classroom classroom = classroomList.get(position);
        holder.textViewClassName.setText(classroom.getClassName());
        holder.textViewSection.setText(classroom.getSection());

        //teacher name
        String teacherUid = classroom.getTeacherUid();
        DatabaseReference usersRef = FirebaseDatabase.getInstance(" https://classroom-adefd-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        usersRef.child(teacherUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String teacherName = snapshot.child("name").getValue(String.class);
                    holder.textViewTeacherName.setText(teacherName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error if needed
            }
        });
    }

    @Override
    public int getItemCount() {
        return classroomList.size();
    }

    static class ClassroomViewHolder extends RecyclerView.ViewHolder {
        TextView textViewClassName;
        TextView textViewSection;
        TextView textViewTeacherName;

        public ClassroomViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewClassName = itemView.findViewById(R.id.textViewClassName);
            textViewSection = itemView.findViewById(R.id.textViewSection);
            textViewTeacherName = itemView.findViewById(R.id.textViewTeacherName);
        }
    }
}

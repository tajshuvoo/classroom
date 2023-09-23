package com.example.classroom;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ViewHolder> {
    private List<ChapterItem> chapterList;
    private OnItemClickListener onItemClickListener; // Listener for item clicks

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public ChapterAdapter(List<ChapterItem> chapterList, OnItemClickListener onItemClickListener) {
        this.chapterList = chapterList;
        this.onItemClickListener = onItemClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChapterItem chapter = chapterList.get(position);
        holder.chapterNumTextView.setText("Chapter " + chapter.getChapterNum());
        holder.chapterNameTextView.setText(chapter.getChapterName());

        // Set a click listener for this item
        final int itemPosition = position; // Add this line to make 'position' final
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(itemPosition); // Use 'itemPosition' here
            }
        });
    }


    @Override
    public int getItemCount() {
        return chapterList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView chapterNumTextView;
        public TextView chapterNameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            chapterNumTextView = itemView.findViewById(R.id.textViewChapterNum);
            chapterNameTextView = itemView.findViewById(R.id.textViewChapterName);
        }
    }
}


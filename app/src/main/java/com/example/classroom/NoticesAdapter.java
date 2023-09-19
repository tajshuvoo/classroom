package com.example.classroom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NoticesAdapter extends RecyclerView.Adapter<NoticesAdapter.NoticeViewHolder> {

    private List<Notice> notices;
    private OnItemClickListener listener; // Define the click listener interface field here

    public NoticesAdapter(List<Notice> notices) {
        this.notices = notices;
    }

    // Define the setOnItemClickListener method to set the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.your_notice_list_item_layout, parent, false);
        return new NoticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewHolder holder, int position) {
        Notice notice = notices.get(position);
        holder.bind(notice);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(notice);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notices.size();
    }

    // Define a click listener interface
    public interface OnItemClickListener {
        void onItemClick(Notice notice);
    }

    public static class NoticeViewHolder extends RecyclerView.ViewHolder {
        private TextView descriptionTextView;

        public NoticeViewHolder(@NonNull View itemView) {
            super(itemView);
            descriptionTextView = itemView.findViewById(R.id.textViewNoticeDescription); // Replace with your TextView ID
        }

        public void bind(Notice notice) {
            descriptionTextView.setText(notice.getDescription());
            // You can bind other data here if needed
        }
    }
}

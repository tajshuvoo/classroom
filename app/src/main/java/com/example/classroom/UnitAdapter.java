package com.example.classroom;
// UnitAdapter.java
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UnitAdapter extends RecyclerView.Adapter<UnitAdapter.UnitViewHolder> {
    private List<UnitItem> unitList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public UnitAdapter(List<UnitItem> unitList) {
        this.unitList = unitList;
    }

    @NonNull
    @Override
    public UnitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_unit, parent, false);
        return new UnitViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull UnitViewHolder holder, int position) {
        UnitItem unit = unitList.get(position);
        holder.textViewUnitNum.setText("Unit "+unit.getUnitNum());
        holder.textViewUnitName.setText(unit.getUnitName());
    }

    @Override
    public int getItemCount() {
        return unitList.size();
    }

    public static class UnitViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUnitNum;
        TextView textViewUnitName;

        public UnitViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            textViewUnitNum = itemView.findViewById(R.id.textViewUnitNum);
            textViewUnitName = itemView.findViewById(R.id.textViewUnitName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}

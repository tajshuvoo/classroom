package com.example.classroom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MaterialsAdapter extends RecyclerView.Adapter<MaterialsAdapter.MaterialsViewHolder> {

    private List<Materials> materialsList;
    private OnItemClickListener listener; // Define the click listener interface field here

    public MaterialsAdapter(List<Materials> materialsList) {
        this.materialsList = materialsList;
    }

    // Define the setOnItemClickListener method to set the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MaterialsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.material_list_item, parent, false);
        return new MaterialsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialsViewHolder holder, int position) {
        Materials materials = materialsList.get(position);
        holder.bind(materials);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(materials);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return materialsList.size();
    }

    // Define a click listener interface
    public interface OnItemClickListener {
        void onItemClick(Materials materials);
    }

    public static class MaterialsViewHolder extends RecyclerView.ViewHolder {
        private TextView descriptionTextView;

        public MaterialsViewHolder(@NonNull View itemView) {
            super(itemView);
            descriptionTextView = itemView.findViewById(R.id.textViewMaterialDescription); // Replace with your TextView ID
        }

        public void bind(Materials materials) {
            descriptionTextView.setText(materials.getDescription());
            // You can bind other data here if needed
        }
    }
}

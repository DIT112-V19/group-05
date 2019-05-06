package com.example.hajken;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ListAdapter extends RecyclerView.Adapter {


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ListViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return OurData.imageName.length;
    }

    private class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mItemText;
        private ImageView mItemImage;

        public ListViewHolder(View itemView) {

            super(itemView);
            mItemText = (TextView) itemView.findViewById(R.id.itemText);
            mItemImage = (ImageView) itemView.findViewById(R.id.itemImage);
            itemView.setOnClickListener(this);


        }

        public void bindView(int position){
            mItemText.setText(OurData.imageName[position]);
            mItemImage.setImageResource(OurData.picturePath[position]);
        }

        public void onClick(View view){

        }

    }
}

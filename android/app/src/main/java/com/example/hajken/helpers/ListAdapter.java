package com.example.hajken.helpers;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.hajken.R;
import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter {


    private ArrayList<CoordinatesListItem> mItems;

    public ListAdapter(ArrayList<CoordinatesListItem> mItems, onItemSelectedListener onItemSelectedListener) {
        this.mItems = mItems;
        this.onItemSelectedListener = onItemSelectedListener;
    }

    private onItemSelectedListener onItemSelectedListener;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ListViewHolder) holder).bindView(mItems.get(position));

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public interface onItemSelectedListener{

        void onItemSelected(CoordinatesListItem coordinatesListItem);

    }

    private class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mItemText;
        private ImageView mItemImage;

        public ListViewHolder(View itemView) {

            super(itemView);
            // mItemText = (TextView) itemView.findViewById(R.id.itemText);
            mItemImage = (ImageView) itemView.findViewById(R.id.itemImage);
            itemView.setOnClickListener(this);

        }

        public void bindView(CoordinatesListItem item){
            //mItemText.setText(OurData.imageName[position]);
            mItemImage.setImageBitmap(item.getmBitmap());

        }

        public void onClick(View view){

            if(getAdapterPosition() > -1){
                onItemSelectedListener.onItemSelected(mItems.get(getAdapterPosition()));
            }

        }

    }

}

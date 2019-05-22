package com.example.hajken.helpers;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.hajken.R;
import com.google.maps.model.TransitAgency;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

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
            Log.d(TAG,"loading bitmap");
            String path = "/data/user/0/com.example.hajken/app_imageDir/";
            String name = item.getmName();
            Log.d(TAG, "itemname"+item.getmName());
            mItemImage.setImageBitmap(loadImageFromStorage(path, name));
        }

        public void onClick(View view){

            if(getAdapterPosition() > -1){
                onItemSelectedListener.onItemSelected(mItems.get(getAdapterPosition()));
            }
        }
    }

    private Bitmap loadImageFromStorage(String path, String name)
    {
        Log.d(TAG, "Inside load image");
        try {
            File f=new File(path, name);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            Log.d(TAG, "Loading bitmap: "+b);
            return b;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            Log.d(TAG, "Exception");
        }

        return null;
    }

    public ListAdapter.onItemSelectedListener getOnItemSelectedListener() {
        return onItemSelectedListener;
    }

    public void setOnItemSelectedListener(ListAdapter.onItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }
}

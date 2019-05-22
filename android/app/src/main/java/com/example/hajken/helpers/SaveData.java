package com.example.hajken.helpers;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;


public class SaveData {

    private static SaveData mInstance = null;
    private Context context;
    private File filePath;
    private ArrayList<CoordinatesListItem> mItemList;



    private SaveData(Context context){
        context = context;
    }

    public static SaveData getInstance(Context context){
        if (mInstance == null){
            mInstance = new SaveData(context);
        }
        return mInstance;
    }

    public String savePNG(Bitmap bitmap){

        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/savedMaps
        File directory = cw.getDir("savedMaps", MODE_PRIVATE);
        // Create savedMaps

        File mypath = new File(directory, createName());
        Log.d(TAG, "before saving data. My path is: "+mypath);

        FileOutputStream toPhoneStream = null;
        try{
            toPhoneStream = new FileOutputStream(mypath);
            //Use to compress method on the BitMap object to write image to the outputstream
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, toPhoneStream);
            Log.d(TAG, "Saving data");
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            Log.d(TAG, "Finally data");
            try {
                toPhoneStream.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        return mypath.getAbsolutePath();
    }

    public void saveData(Object object){
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mItemList);
        editor.putString("task list", json);
        editor.apply();


    }

    public void loadData(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list", null);
        Type type = new TypeToken<ArrayList<CoordinatesListItem>>() {}.getType();
        mItemList = gson.fromJson(json, type);

        if (mItemList == null) {
            mItemList = new ArrayList<>();
        }

    }


    public String createName() {
        String name = Integer.toString(mItemList.size());
        return name;

    }

    public ArrayList<CoordinatesListItem> getList(){
        return this.mItemList;
    }


}

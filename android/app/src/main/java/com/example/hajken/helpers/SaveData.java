package com.example.hajken.helpers;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;

import com.example.hajken.MainActivity;
import com.example.hajken.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
    public static ArrayList<CoordinatesListItem> mItemList = new ArrayList<>();
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private File mypath;


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

        ContextWrapper cw = new ContextWrapper(MainActivity.getThis());
        // path to /data/data/yourapp/app_data/savedMaps

        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create savedMaps

        mypath = new File(directory, createName());

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
        Log.d(TAG, "myPath: "+mypath);
        return mypath.getAbsolutePath();
    }

    public void saveData(Object object){
        Gson gson = new Gson();
        mPreferences = MainActivity.getThis().getSharedPreferences("shared preferences", MODE_PRIVATE);
        mEditor = mPreferences.edit();
        String json = gson.toJson(mItemList);
        mEditor.putString("task list", json);
        mEditor.apply();
        Log.d(TAG, "Inside Save Data: "+ json);
        Log.d(TAG, "Save listitem: "+ mItemList);

    }

    public void loadData(){
        mPreferences = MainActivity.getThis().getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPreferences.getString("task list", null);
        Type type = new TypeToken<ArrayList<CoordinatesListItem>>() {}.getType();
        mItemList = gson.fromJson(json, type);

        if (mItemList == null) {
            mItemList = new ArrayList<>();
        }
        Log.d(TAG, "Inside Load Data: "+ json);
    }


    public String createName(){

            String name = Integer.toString(mItemList.size()-1)+".png";
            return name;

    }

    /*public Bitmap loadBitmap(String name){
        FileInputStream in = new FileInputStream(mypath+mItemList);


        return bitmap;
    }*/

    public ArrayList<CoordinatesListItem> getList(){
        return this.mItemList;
    }

    public void setList(){
        mItemList = new ArrayList<CoordinatesListItem>();
    }





}

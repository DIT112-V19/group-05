package com.example.hajken;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.ProgressBar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;

public class BluetoothConnection {



    public static final String TAG = "BluetoothConnection ";
    public final String APPNAME = "HAJKEN";

    private final static UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public final BluetoothAdapter myBluetoothAdapter;
    Context myContext;


    private AcceptThread myInsecureAcceptThread;

    private ConnectThread myConnectThread;
    private BluetoothDevice mBluetoothDevice;
    private UUID myDeviceUUID;

    private ConnectedThread myConnectedThread;

    private static BluetoothConnection mInstance = null;

    private  BluetoothConnection(Context context) {
        myContext = context;
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        start();
    }

    // CLASSIC singleton implementation

    public static BluetoothConnection getInstance(Context context){
        if (mInstance == null){
            mInstance = new BluetoothConnection(context);
        }
        return mInstance;
    }

    private class AcceptThread extends Thread{

        private final BluetoothServerSocket myServerSocket;

        public AcceptThread(){
            BluetoothServerSocket tempSocket = null;

            try{
                tempSocket = myBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(APPNAME,MY_UUID_INSECURE);
                Log.d(TAG, "Setting up using acceptThread " + MY_UUID_INSECURE);
            } catch(IOException e){
                Log.e(TAG," AcceptThread IO error " + e.getMessage());
            }
            myServerSocket = tempSocket;
        }

        public void run(){
            Log.d(TAG, " Accept thread running.. ");

            BluetoothSocket mSocket = null;

            try{
                Log.d(TAG, "RFCOM server starts.. ");
                mSocket = myServerSocket.accept();
                Log.d(TAG, " server socket accepted connection! ");

            } catch (IOException e){
                Log.e(TAG, " IO error in acceptthread - run" + e.getMessage());

            }

            if (mSocket != null){
                connected(mSocket , mBluetoothDevice);
            }
            Log.d(TAG, " end of acceptThread - run") ;
        }

        public void cancel(){

            Log.d(TAG, " cancelling acceptThread ");
            try{
                myServerSocket.close();
            } catch (IOException e){
                Log.e(TAG, " cancel of acceptThread failed " + e.getMessage());
            }
        }

    }

    private class ConnectThread extends Thread{

        private  BluetoothSocket mBluetoothSocket;

        public ConnectThread(BluetoothDevice device, UUID uuid){

            Log.d(TAG, " ConnectThread started ");
            mBluetoothDevice = device;
            myDeviceUUID = uuid;
        }

        public void run(){

            BluetoothSocket temp = null;
            Log.d(TAG, " ConnectThread Run initiated");

            try{
                Log.d(TAG, "Trying to connect unsecured RF socket using UUID" + MY_UUID_INSECURE);

                temp = mBluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID_INSECURE);

            } catch (IOException e){
                Log.e(TAG, " Could not connect using insecure RF socket" + e.getMessage());

            }

            mBluetoothSocket = temp;

            myBluetoothAdapter.cancelDiscovery();

            try{
                mBluetoothSocket.connect();

                Log.d(TAG, " connected in connectThread");


            } catch (IOException e){

                try{
                    mBluetoothSocket.close();
                    Log.e(TAG,  " socket closed in connectThread - run " + e.getMessage());


                } catch (IOException e1){

                    Log.e(TAG, " unable to close socket in connectThread - run " + e.getMessage());


                }
                Log.e(TAG, "could not connect to UUID " + MY_UUID_INSECURE);

            }
            connected(mBluetoothSocket, mBluetoothDevice);
        }
        public void cancel() {
            try {
                Log.d(TAG, " Closing other device socket ");
                mBluetoothSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Failed to close socket in connectThread cancel " + e.getMessage());
            }
        }
    }

    public synchronized void start(){
        Log.d(TAG, " start is called.. ");
        if (myConnectThread != null){
            myConnectThread.cancel();
            myConnectThread = null;
        }
        if (myInsecureAcceptThread == null){
            myInsecureAcceptThread = new AcceptThread();
            myInsecureAcceptThread.start();
        }
    }

    public void startClient(BluetoothDevice device, UUID uuid){

        Log.d(TAG, " StartClient initiated ");
        myConnectThread = new ConnectThread(device, uuid);
        myConnectThread.start();
    }

    private class ConnectedThread extends Thread{
        private final BluetoothSocket mSocket;
        private final InputStream mInputStream;
        private OutputStream mOutputStream;

        public ConnectedThread(BluetoothSocket socket){
            Log.d(TAG, " connectedThread starting ");
            mSocket = socket;
            InputStream tempInputStream = null;
            OutputStream tempOutputStream = null;
            Log.d(TAG, " Connected to : " + mBluetoothDevice.getName());

            try{
                tempInputStream = mSocket.getInputStream();
                tempOutputStream = mSocket.getOutputStream();

            } catch (IOException e){
                Log.e(TAG, " failed either stream in ConnectedThread " + e.getMessage());
            }

            mInputStream = tempInputStream;
            mOutputStream = tempOutputStream;
        }

        public void run(){
            //stores what is read from stream
            byte[] byteForStream = new byte[1024];

            int bytes;

            while (true){
                try{
                    bytes = mInputStream.read(byteForStream);
                    String message = new String ( byteForStream, 0, bytes);
                    Log.d(TAG, " Read from inputstream " + message);

                } catch (IOException e){
                    Log.e(TAG, " error reading from inputstream " + e.getMessage());
                    break;
                }
            }
        }

        //will send data to remote device

        public void writeToDevice(String input){
            Log.d(TAG, " What will be converted to bytes: " + input);

            byte[] inputInBytes = input.getBytes();

            Log.d(TAG, " what will be sent to outputstream: " + Arrays.toString(inputInBytes));

            try{

                mOutputStream = mSocket.getOutputStream();
                mOutputStream.write(inputInBytes);

                Log.d(TAG, " successfully written to outputstream in write()");

            } catch (IOException e){
                Log.e(TAG," error writing to outputstream.. " + e.getMessage());
            }
        }

        public void cancel() {
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e(TAG, " failed to close socket" + e.getMessage());
            }
        }




        /*public void write(String input){

            Log.d(TAG, " Write() is called ");

            byte [] convertedString = input.getBytes();

            myConnectedThread.write(convertedString);


        }*/
    }
    private void connected(BluetoothSocket socket, BluetoothDevice device){

        Log.d(TAG, " started connected()" );

        myConnectedThread = new ConnectedThread(socket);
        myConnectedThread.start();
    }


}

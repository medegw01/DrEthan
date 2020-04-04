package com.dreslab.www.drethans;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity_server extends Activity {
    //constants declaration
    Button b1,b2,b3,b4;
    ListView lv;
    EditText InputText;
    RelativeLayout inputPane;
    TextView textStatus;

    private UUID MY_UUID;
    private String myName;
    private BluetoothAdapter BA;
    private ConnectedThread myThreadConnected;
    private Set<BluetoothDevice>pairedDevices;
    private static final String TAG = "DR_ETHAN_DEBUG_TAG";
    private static final int DISCOVER_DURATION = 300;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_bluetooth);

        b1 = (Button) findViewById(R.id.button);
        b2 = (Button) findViewById(R.id.button2);
        b3 = (Button) findViewById(R.id.button3);
        b4 = (Button) findViewById(R.id.button4);

        inputPane =  (RelativeLayout)  findViewById(R.id.layout);
        InputText =  (EditText) findViewById(R.id.inputText);
        textStatus = (TextView) findViewById(R.id.textStatus);
        lv =         (ListView) findViewById(R.id.listView);

        BA = BluetoothAdapter.getDefaultAdapter();


        //generate UUID on web: http://www.famkruithof.net/uuid/uuidgen
        MY_UUID = UUID.fromString("1efb0fa0-d424-11e6-9598-0800200c9a66");
        myName = MY_UUID.toString();
    }

    public void on(View v) {
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Turned on", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_LONG).show();
        }

    }

    //Turns bluetooth off
    public void off(View v) {
        //myThreadConnected.cancel();
        BA.disable();
        Toast.makeText(getApplicationContext(), "Turned off", Toast.LENGTH_LONG).show();
    }

    public void visible(View v) {
        if (BA == null) {//check if bluetooth is supported
            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_LONG).show();
        } else {//make it visible
            enableBluetooth(v);
        }
    }

    public void enableBluetooth(View v) {
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        getVisible.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVER_DURATION);
        startActivityForResult(getVisible, 1);
    }

    public void connect(View v){
        BluetoothServer bluetoothServer = new BluetoothServer();
        bluetoothServer.start();
    }

    public void list(View v) {
        pairedDevices = BA.getBondedDevices();

        ArrayList list = new ArrayList();

        for (BluetoothDevice bt : pairedDevices) list.add(bt.getName());
        Toast.makeText(getApplicationContext(), "Showing Paired Devices", Toast.LENGTH_SHORT).show();

        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);

        lv.setAdapter(adapter);
    }

    public void send(View v){
        if(myThreadConnected!=null){
            byte[] bytesToSend = InputText.getText().toString().getBytes();
            myThreadConnected.write(bytesToSend);
        }
    }

    private class BluetoothServer extends Thread {

        private BluetoothServerSocket mmServerSocket = null;

        public BluetoothServer() {
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                mmServerSocket  = BA.listenUsingRfcommWithServiceRecord(myName, MY_UUID);
                textStatus.setText("Waiting\n"
                        + "bluetoothServerSocket :\n"
                        + mmServerSocket);
            } catch (IOException e) {
                Log.e(TAG, "Socket's listen() method failed", e);
            }
            //mmServerSocket = tmp;
        }

        @Override
        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.
            if  (mmServerSocket != null) {
                try {
                    socket = mmServerSocket.accept();
                    BluetoothDevice remoteDevice = socket.getRemoteDevice();

                    final String strConnected = "Connected:\n" +
                            remoteDevice.getName() + "\n" +
                            remoteDevice.getAddress();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textStatus.setText(strConnected);
                            inputPane.setVisibility(View.VISIBLE);
                        }
                    });
                    manageMyConnectedSocket(socket);
                } catch (IOException e) {
                    Log.e(TAG, "Socket's accept() method failed", e);
                    final String eMessage = e.getMessage();
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            textStatus.setText("something wrong: \n" + eMessage);
                        }
                    });

                    //break;
                }
            }
                else{
                    runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            textStatus.setText("bluetoothServerSocket == null");
                        }});
                }
                /*if (socket != null) {
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.
                    manageMyConnectedSocket(socket);
                    //mmServerSocket.close();
                    break;
                }*/
        }
    }

    private void manageMyConnectedSocket(BluetoothSocket socket) {

        myThreadConnected = new ConnectedThread(socket);
        myThreadConnected.start();
    }

    //connected
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private final BluetoothSocket mmSocket;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            mmSocket = socket;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();

            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating I/O stream", e);
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        @Override
        public void run() {
            mmBuffer = new byte[1024000000];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    String strReceived = new String(mmBuffer, 0, numBytes);
                    final String msgReceived = String.valueOf(numBytes) +
                            " bytes received:\n"
                            + strReceived;

                    runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            textStatus.setText(msgReceived);
                        }});

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    Log.d(TAG, "Input stream was disconnected", e);
                    final String msgConnectionLost = "Connection lost:\n"
                            + e.getMessage();
                    runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            textStatus.setText(msgConnectionLost);
                        }});
                }
            }
        }
        // Call this from the main activity to send data to the remote device.
        //sending means
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
                mmOutStream.flush();

                /* Share the sent message with the UI activity.
                Message writtenMsg = mHandler.obtainMessage(
                        MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
                writtenMsg.sendToTarget();*/
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                /*Message writeErrorMsg =
                        mHandler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                mHandler.sendMessage(writeErrorMsg);*/
            }
        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }
}

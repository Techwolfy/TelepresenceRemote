package net.g33kworld.telepresenceremote;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

public class NetworkTask extends AsyncTask<String, Boolean, String> {

    private Context parentContext;
    private Button connectButton;

    private int commandFrame;
    private int pingFrame;
    private JSONObject command;
    private JSONObject ping;
    private InetAddress server;
    private DatagramSocket socket;

    public NetworkTask(Context parentContext, Button connectButton) {
        this.parentContext = parentContext;
        this.connectButton = connectButton;
        command = new JSONObject();
        ping = new JSONObject();
        try {
            ping.put("isClient", true);
            ping.put("isRobot", false);
            ping.put("ping", true);
        } catch(JSONException e) {
            //These values are hard-coded and should never cause exceptions
        }
    }

    protected String doInBackground(String... hostname) {
        //Get server address
        try {
            server = InetAddress.getByName(hostname[0]);
        } catch(UnknownHostException e) {
            return "Could not connect to robot!";
        }

        //Open UDP socket
        try {
            socket = new DatagramSocket();
            socket.connect(server, 8353);
        } catch(SocketException e) {
            return "Failed to open UDP socket!";
        }

        //Main loop
        publishProgress(true);
        int counter = 0;
        while(!isCancelled() && socket.isConnected()) {
            try {
                //Create and send UDP packet
                if(counter < 20) {
                    try {
                        command.put("frameNum", commandFrame++);
                        command.put("time", System.currentTimeMillis());
                    } catch(JSONException e) {return "JSON error";}
                    socket.send(new DatagramPacket(command.toString().getBytes(), command.toString().getBytes().length, server, 8353));
                } else {
                    //Ping every 20th packet
                    try {
                        ping.put("frameNum", pingFrame++);
                        ping.put("time", System.currentTimeMillis());
                    } catch(JSONException e) {return "JSON error";}
                    socket.send(new DatagramPacket(ping.toString().getBytes(), ping.toString().getBytes().length, server, 8353));
                    counter = 0;
                }
            } catch(IOException e) {
                return "Communication with robot failed!";
            }

            //Sleep for 20 milliseconds
            try {
                Thread.sleep(20);
                counter++;
            } catch(InterruptedException e) {
                //Ignore; different sleep times have no effect
            }
        }

        socket.close();
        publishProgress(false);
        return null;
    }

    protected void onProgressUpdate(Boolean... connected) {
        if(connected[0]) {
            connectButton.setText(R.string.connected);
        } else {
            connectButton.setText(R.string.disconnected);
        }
    }

    protected void onPostExecute(String result) {
        if(result != null) {
            Toast.makeText(parentContext, result, Toast.LENGTH_SHORT).show();
        }
    }

    protected void onCancelled(String result) {
        if(result != null) {
            Toast.makeText(parentContext, result, Toast.LENGTH_SHORT).show();
        }
    }

    public synchronized void setCommand(JSONObject command) {
        this.command = command;
    }
}
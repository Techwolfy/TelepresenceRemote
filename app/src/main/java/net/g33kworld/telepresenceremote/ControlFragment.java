package net.g33kworld.telepresenceremote;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ControlFragment extends Fragment {

    private NetworkTask client;

    private EditText ipText;
    private Button connectButton;
    private ToggleButton[] buttons;
    private Button setAllButton;
    private Button clearAllButton;
    private Button stopButton;
    private JoystickView joystick;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Retain through config changes
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.control_fragment, container, false);

        //Retrieve and initialize EditText, Button, and Joystick objects
        ipText = (EditText)v.findViewById(R.id.ipText);
        connectButton = (Button)v.findViewById(R.id.connectButton);
        buttons = new ToggleButton[10];
        buttons[0] = (ToggleButton)v.findViewById(R.id.button0);
        buttons[1] = (ToggleButton)v.findViewById(R.id.button1);
        buttons[2] = (ToggleButton)v.findViewById(R.id.button2);
        buttons[3] = (ToggleButton)v.findViewById(R.id.button3);
        buttons[4] = (ToggleButton)v.findViewById(R.id.button4);
        buttons[5] = (ToggleButton)v.findViewById(R.id.button5);
        buttons[6] = (ToggleButton)v.findViewById(R.id.button6);
        buttons[7] = (ToggleButton)v.findViewById(R.id.button7);
        buttons[8] = (ToggleButton)v.findViewById(R.id.button8);
        buttons[9] = (ToggleButton)v.findViewById(R.id.button9);
        setAllButton = (Button)v.findViewById(R.id.setAllButton);
        clearAllButton = (Button)v.findViewById(R.id.clearAllButton);
        stopButton = (Button)v.findViewById(R.id.stopButton);
        joystick = (JoystickView)v.findViewById(R.id.joystickView);

        //Update connect button text if necessary (e.g. config change)
        if(client == null) {
            connectButton.setText(R.string.disconnected);
        } else {
            connectButton.setText(R.string.connected);
        }

        //Retrieve last used server
        ipText.setText(getActivity().getPreferences(getActivity().MODE_PRIVATE).getString("server", ""));

        //Set up connect button listener
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(client == null) {
                    client = new NetworkTask(connectButton.getContext(), connectButton);
                    sendCommand();
                    client.execute(ipText.getText().toString());
                    getActivity().getPreferences(getActivity().MODE_PRIVATE).edit().putString("server", ipText.getText().toString()).commit();
                } else {
                    client.cancel(true);
                    client = null;
                    connectButton.setText(R.string.disconnected);
                }
            }
        });

        //Set up control button listeners
        for(ToggleButton b : buttons) {
            b.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    sendCommand();
                }
            });
        }
        setAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(ToggleButton button : buttons) {
                    button.setChecked(true);
                }
            }
        });
        clearAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(ToggleButton button : buttons) {
                    button.setChecked(false);
                }
            }
        });

        //Set up stop button listener
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joystick.reset();
                sendCommand();
            }
        });

        //Set up joystick listener
        //Note: Since this is called before the JoystickView's onTouchEvent, touch values are always one frame behind
        joystick.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                sendCommand();

                //Don't consume touch event
                return false;
            }
        });

        //Send initial command
        sendCommand();
        return v;
    }

    //Prepare and send JSON command packet
    public void sendCommand() {
        //Make sure a NetworkTask object exists to send the command to, and that all values are available
        if(client == null) {
            return;
        }

        //Build and send the command packet
        JSONObject command = new JSONObject();
        try {
            command.put("isClient", true);
            command.put("isRobot", false);
            command.put("ping", false);

            command.put("axes", new JSONArray(joystick.getScaledAxes()));

            boolean[] buttonValues = new boolean[buttons.length];
            for(int i = 0; i < buttons.length; i++) {
                buttonValues[i] = buttons[i].isChecked();
            }
            command.put("buttons", new JSONArray(buttonValues));

            //Send JSON packet bytes to networking AsyncTask
            client.setCommand(command);
        } catch(JSONException e) {
            Toast.makeText(this.getActivity(), "Failed to create command packet!", Toast.LENGTH_SHORT).show();
        }
    }
}

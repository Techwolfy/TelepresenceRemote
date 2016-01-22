package net.g33kworld.telepresenceremote;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TelepresenceRemote extends AppCompatActivity {

    private ControlFragment fragment;

    //Shell activity; does nothing but load a worker fragment
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //Get a reference to the message fragment
        fragment = (ControlFragment)getFragmentManager().findFragmentById(R.id.controlFragment);
    }
}

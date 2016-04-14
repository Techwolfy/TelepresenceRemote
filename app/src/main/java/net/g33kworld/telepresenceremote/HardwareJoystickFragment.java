package net.g33kworld.telepresenceremote;

import android.app.Fragment;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class HardwareJoystickFragment extends Fragment {

    //Process hardware joystick events
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        //Check that the motion event came from a game controller
        if((event.getSource() & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK && event.getAction() == MotionEvent.ACTION_MOVE) {
            x = getHardwareJoystickAxis(event, MotionEvent.AXIS_X);
            y = getHardwareJoystickAxis(event, MotionEvent.AXIS_Y);
            rx = getHardwareJoystickAxis(event, MotionEvent.AXIS_Z);
            ry = getHardwareJoystickAxis(event, MotionEvent.AXIS_RZ);
            return true;
        }
        return super.onGenericMotionEvent(event);
    }

    //Return value of requested axis from hardware joystick, accounting for deadzone
    public float getHardwareJoystickAxis(MotionEvent event, int axis) {
        InputDevice.MotionRange range = event.getDevice().getMotionRange(axis, event.getSource());
        if(range != null) {
            if(Math.abs(event.getAxisValue(axis)) > range.getFlat()) {
                return event.getAxisValue(axis);
            } else {
                return 0.0f;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean handled = false;
        if((event.getSource() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) {
            if(event.getRepeatCount() == 0) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_BUTTON_1:
                        break;
                    case KeyEvent.KEYCODE_BUTTON_2:
                        break;
                    case KeyEvent.KEYCODE_BUTTON_3:
                        break;
                    case KeyEvent.KEYCODE_BUTTON_4:
                        break;
                    case KeyEvent.KEYCODE_BUTTON_5:
                        break;
                    case KeyEvent.KEYCODE_BUTTON_6:
                        break;
                    case KeyEvent.KEYCODE_BUTTON_7:
                        break;
                    case KeyEvent.KEYCODE_BUTTON_8:
                        break;
                    case KeyEvent.KEYCODE_BUTTON_9:
                        break;
                    case KeyEvent.KEYCODE_BUTTON_10:
                        break;
                    case KeyEvent.KEYCODE_BUTTON_11:
                        break;
                    case KeyEvent.KEYCODE_BUTTON_12:
                        break;
                    case KeyEvent.KEYCODE_BUTTON_13:
                        break;
                    case KeyEvent.KEYCODE_BUTTON_14:
                        break;
                    case KeyEvent.KEYCODE_BUTTON_15:
                        break;
                    case KeyEvent.KEYCODE_BUTTON_16:
                        break;
                }
            }
            if(handled) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
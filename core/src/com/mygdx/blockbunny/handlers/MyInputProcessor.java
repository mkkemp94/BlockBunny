package com.mygdx.blockbunny.handlers;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

/**
 * Created by mkemp on 11/8/17.
 */

public class MyInputProcessor extends InputAdapter {
    public boolean keyDown(int k) {
        if (k == Input.Keys.Z) {
            MyInput.setKey(MyInput.BUTTON1, true);
        }
        if (k == Input.Keys.X) {
            MyInput.setKey(MyInput.BUTTON2, true);
        }
        return true;
    }

    public boolean keyUp(int k) {
        if (k == Input.Keys.Z) {
            MyInput.setKey(MyInput.BUTTON1, false);
        }
        if (k == Input.Keys.X) {
            MyInput.setKey(MyInput.BUTTON2, false);
        }
        return true;
    }
}

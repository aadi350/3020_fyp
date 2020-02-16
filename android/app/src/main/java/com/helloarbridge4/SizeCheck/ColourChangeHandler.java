package com.helloarbridge4.SizeCheck;

import com.helloarbridge4.Object.ObjectCodes;
import com.helloarbridge4.Object.ObjectHandler;

public class ColourChangeHandler {
    private static ColourChangeHandler object;
    private ObjectHandler objectHandler;
    private ColourChangeHandler() {}

    public ColourChangeHandler getHandler() {
        if (object == null) {
            object = new ColourChangeHandler();
        }
        return object;
    }

    public void setObject(FitCodes fitCode, ObjectCodes objectCode) {

    }





}

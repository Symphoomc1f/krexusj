package com.java110.things.sip;


import com.java110.things.sip.remux.Observer;

public interface Observable {

    public void subscribe(Observer observer);
}

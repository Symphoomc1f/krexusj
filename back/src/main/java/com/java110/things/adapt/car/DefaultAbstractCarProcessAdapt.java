package com.java110.things.adapt.car;

import com.java110.things.entity.machine.MachineDto;

public abstract class DefaultAbstractCarProcessAdapt implements ICarProcess {

    @Override
    public void initCar() {

    }

    @Override
    public void initCar(MachineDto machineDto) {

    }


    @Override
    public int getCarNum() {
        return 0;
    }
}

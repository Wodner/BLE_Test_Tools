package com.example.wwd.bletools;

import java.util.UUID;

public class Constant {

    public static final UUID SERVICE_UUID               = UUID.fromString("6e40fc00-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID CHARACTERISTIC_WRITE_UUID  = UUID.fromString("6e40fc20-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID CHARACTERISTIC_READ_UUID   = UUID.fromString("6e40fc21-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID CHARACTERISTIC_NOTIFY_UUID = UUID.fromString("6e40fc21-b5a3-f393-e0a9-e50e24dcca9e");

    public static final byte DEVICE_STEP  = (byte) 0xB2;
    public static final byte DEVICE_HEART = (byte) 0xE1;

}

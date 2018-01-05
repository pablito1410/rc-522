package com.pablo.rc522;

enum PICC {

    REQIDL    ((byte)0x26),
    REQALL    ((byte)0x52),
    ANTICOLL  ((byte)0x93),
    SElECTTAG ((byte)0x93),
    AUTHENT1A ((byte)0x60),
    AUTHENT1B ((byte)0x61),
    READ      ((byte)0x30),
    WRITE     ((byte)0xA0),
    DECREMENT ((byte)0xC0),
    INCREMENT ((byte)0xC1),
    RESTORE   ((byte)0xC2),
    TRANSFER  ((byte)0xB0),
    HALT      ((byte)0x50),

    ;

    private final byte value;

    PICC(final byte value) {
        this.value = value;
    }

    byte getValue() {
        return value;
    }
}

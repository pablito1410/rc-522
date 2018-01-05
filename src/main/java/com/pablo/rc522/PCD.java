package com.pablo.rc522;

enum PCD {

    IDLE       ((byte)0x00),
    AUTHENT    ((byte)0x0E),
    RECEIVE    ((byte)0x08),
    TRANSMIT   ((byte)0x04),
    TRANSCEIVE ((byte)0x0C),
    RESETPHASE ((byte)0x0F),
    CALCCRC    ((byte)0x03),

    ;

    private final byte value;

    PCD(final byte value) {
        this.value = value;
    }

    byte getValue() {
        return value;
    }
}

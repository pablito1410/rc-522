package com.pablo.rc522;

class RegisterAddress {

    static final byte Reserved00     = 0x00;
    static final byte CommandReg     = 0x01;
    static final byte CommIEnReg     = 0x02;
    static final byte DivlEnReg      = 0x03;
    static final byte CommIrqReg     = 0x04;
    static final byte DivIrqReg      = 0x05;
    static final byte ErrorReg       = 0x06;
    static final byte Status1Reg     = 0x07;
    static final byte Status2Reg     = 0x08;
    static final byte FIFODataReg    = 0x09;
    static final byte FIFOLevelReg   = 0x0A;
    static final byte WaterLevelReg  = 0x0B;
    static final byte ControlReg     = 0x0C;
    static final byte BitFramingReg  = 0x0D;
    static final byte CollReg        = 0x0E;
    static final byte Reserved01     = 0x0F;

    static final byte Reserved10     = 0x10;
    static final byte ModeReg        = 0x11;
    static final byte TxModeReg      = 0x12;
    static final byte RxModeReg      = 0x13;
    static final byte TxControlReg   = 0x14;
    static final byte TxAutoReg      = 0x15;
    static final byte TxSelReg       = 0x16;
    static final byte RxSelReg       = 0x17;
    static final byte RxThresholdReg = 0x18;
    static final byte DemodReg       = 0x19;
    static final byte Reserved11     = 0x1A;
    static final byte Reserved12     = 0x1B;
    static final byte MifareReg      = 0x1C;
    static final byte Reserved13     = 0x1D;
    static final byte Reserved14     = 0x1E;
    static final byte SerialSpeedReg = 0x1F;

    static final byte Reserved20        = 0x20;
    static final byte CRCResultRegM     = 0x21;
    static final byte CRCResultRegL     = 0x22;
    static final byte Reserved21        = 0x23;
    static final byte ModWidthReg       = 0x24;
    static final byte Reserved22        = 0x25;
    static final byte RFCfgReg          = 0x26;
    static final byte GsNReg            = 0x27;
    static final byte CWGsPReg          = 0x28;
    static final byte ModGsPReg         = 0x29;
    static final byte TModeReg          = 0x2A;
    static final byte TPrescalerReg     = 0x2B;
    static final byte TReloadRegH       = 0x2C;
    static final byte TReloadRegL       = 0x2D;
    static final byte TCounterValueRegH = 0x2E;
    static final byte TCounterValueRegL = 0x2F;

    static final byte Reserved30      = 0x30;
    static final byte TestSel1Reg     = 0x31;
    static final byte TestSel2Reg     = 0x32;
    static final byte TestPinEnReg    = 0x33;
    static final byte TestPinValueReg = 0x34;
    static final byte TestBusReg      = 0x35;
    static final byte AutoTestReg     = 0x36;
    static final byte VersionReg      = 0x37;
    static final byte AnalogTestReg   = 0x38;
    static final byte TestDAC1Reg     = 0x39;
    static final byte TestDAC2Reg     = 0x3A;
    static final byte TestADCReg      = 0x3B;
    static final byte Reserved31      = 0x3C;
    static final byte Reserved32      = 0x3D;
    static final byte Reserved33      = 0x3E;
    static final byte Reserved34      = 0x3F;
}

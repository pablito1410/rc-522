package com.pablo.rc522;

import com.pablo.rc522.exception.CommunicationException;
import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.Spi;

import static com.pablo.rc522.CommunicationStatus.MI_ERR;
import static com.pablo.rc522.CommunicationStatus.MI_OK;
import static com.pablo.rc522.RegisterAddress.BitFramingReg;
import static com.pablo.rc522.RegisterAddress.TxControlReg;

public class RC522 {

    private final int resetPinNumber;
    private final int speed;
    private static final int SPI_CHANNEL = 0;

    private final int MAX_LEN = 16;

    public RC522(int Speed, int PinReset) {
        resetPinNumber = PinReset;
        if (Speed < 500000 || Speed > 32000000) {
            throw new IllegalArgumentException("Speed out of range");
        } else this.speed = Speed;
        init();
    }

    public RC522() {
        this.speed = 500000;
        this.resetPinNumber = 22;
        init();
    }

    private void init() {
        Gpio.wiringPiSetup();
        int fd = Spi.wiringPiSPISetup(SPI_CHANNEL, speed);
        if (fd <= -1) {
            throw new RuntimeException(" --> Failed to set up  SPI communication");
        }

        Gpio.pinMode(resetPinNumber, Gpio.OUTPUT);
        Gpio.digitalWrite(resetPinNumber, Gpio.HIGH);
        Reset();
        writeRC522(RegisterAddress.TModeReg, (byte) 0x8D);
        writeRC522(RegisterAddress.TPrescalerReg, (byte) 0x3E);
        writeRC522(RegisterAddress.TReloadRegL, (byte) 30);
        writeRC522(RegisterAddress.TReloadRegH, (byte) 0);
        writeRC522(RegisterAddress.TxAutoReg, (byte) 0x40);
        writeRC522(RegisterAddress.ModeReg, (byte) 0x3D);
        antennaOn();
    }

    private void Reset() {
        writeRC522(RegisterAddress.CommandReg, PCD.RESETPHASE.getValue());
    }

    private void writeRC522(byte address, byte value) {
        byte data[] = new byte[2];
        data[0] = (byte) ((address << 1) & 0x7E);
        data[1] = value;
        int result = Spi.wiringPiSPIDataRW(SPI_CHANNEL, data);
        if (result == -1)
            System.out.println("Device write  error,address=" + address + ",value=" + value);
    }

    private byte readRC522(byte address) {
        byte data[] = new byte[2];
        data[0] = (byte) (((address << 1) & 0x7E) | 0x80);
        data[1] = 0;
        int result = Spi.wiringPiSPIDataRW(SPI_CHANNEL, data);
        if (result == -1)
            throw new CommunicationException("Device read error, address=" + address);
        return data[1];
    }

    private void setBitMask(byte address, byte mask) {
        byte value = readRC522(address);
        writeRC522(address, (byte) (value | mask));
    }

    private void clearBitMask(byte address, byte mask) {
        byte value = readRC522(address);
        writeRC522(address, (byte) (value & (~mask)));
    }

    private void antennaOn() {
        setBitMask(TxControlReg, (byte) 0x03);
    }

    private void antennaOff() {
        clearBitMask(TxControlReg, (byte) 0x03);
    }

    private CommunicationStatus writeCard(byte command, byte[] data, int dataLen,
                                          byte[] backData, int[] backBits, int[] backLen) {
        CommunicationStatus status = CommunicationStatus.MI_ERR;
        byte irq = 0, irqWait = 0, lastBits = 0;
        int n, i;

        backLen[0] = 0;
        if (command == PCD.AUTHENT.getValue()) {
            irq = 0x12;
            irqWait = 0x10;
        } else if (command == PCD.TRANSCEIVE.getValue()) {
            irq = 0x77;
            irqWait = 0x30;
        }

        writeRC522(RegisterAddress.CommIEnReg, (byte) (irq | 0x80));
        clearBitMask(RegisterAddress.CommIrqReg, (byte) 0x80);
        setBitMask(RegisterAddress.FIFOLevelReg, (byte) 0x80);

        writeRC522(RegisterAddress.CommandReg, PCD.IDLE.getValue());

        for (i = 0; i < dataLen; i++) {
            writeRC522(RegisterAddress.FIFODataReg, data[i]);
        }

        writeRC522(RegisterAddress.CommandReg, command);
        if (command == PCD.TRANSCEIVE.getValue()) {
            setBitMask(BitFramingReg, (byte) 0x80);
        }

        i = 2000;
        while (true) {
            n = readRC522(RegisterAddress.CommIrqReg);
            i--;
            if ((i == 0) || (n & 0x01) > 0 || (n & irqWait) > 0) {
                break;
            }
        }
        clearBitMask(BitFramingReg, (byte) 0x80);

        if (i != 0) {
            if ((readRC522(RegisterAddress.ErrorReg) & 0x1B) == 0x00) {
                status = CommunicationStatus.MI_OK;
                if ((n & irq & 0x01) > 0)
                    status = CommunicationStatus.MI_NOTAGERR;
                if (command == PCD.TRANSCEIVE.getValue()) {
                    n = readRC522(RegisterAddress.FIFOLevelReg);
                    lastBits = (byte) (readRC522(RegisterAddress.ControlReg) & 0x07);
                    if (lastBits != 0)
                        backBits[0] = (n - 1) * 8 + lastBits;
                    else
                        backBits[0] = n * 8;

                    if (n == 0) n = 1;
                    if (n > this.MAX_LEN) n = this.MAX_LEN;
                    backLen[0] = n;
                    for (i = 0; i < n; i++)
                        backData[i] = readRC522(RegisterAddress.FIFODataReg);
                }
            } else
                status = MI_ERR;
        }
        return status;
    }

    private CommunicationStatus sendRequest(byte reqMode, int[] backBits)
    {
        byte tagType[] = new byte[1];
        byte dataBack[] = new byte[16];
        int backLen[] = new int[1];

        writeRC522(BitFramingReg, (byte) 0x07);

        tagType[0] = reqMode;
        backBits[0] = 0;
        CommunicationStatus status = writeCard(PCD.TRANSCEIVE.getValue(), tagType, 1, dataBack, backBits, backLen);
        if (status != CommunicationStatus.MI_OK || backBits[0] != 0x10) {
            status = CommunicationStatus.MI_ERR;
        }

        return status;
    }

    private CommunicationStatus antiColl(byte[] backData) {
        CommunicationStatus status;
        byte[] serialNumber = new byte[2];
        int serialNumberCheck = 0;
        int backLen[] = new int[1];
        int backBits[] = new int[1];
        int i;

        writeRC522(BitFramingReg, (byte) 0x00);
        serialNumber[0] = PICC.ANTICOLL.getValue();
        serialNumber[1] = 0x20;
        status = writeCard(PCD.TRANSCEIVE.getValue(), serialNumber, 2, backData, backBits, backLen);
        if (status == CommunicationStatus.MI_OK) {
            if (backLen[0] == 5) {
                for (i = 0; i < 4; i++)
                    serialNumberCheck ^= backData[i];
                if (serialNumberCheck != backData[4]) {
                    status = MI_ERR;
                }
            } else {
                status = MI_OK;
            }
        }
        return status;
    }

    private void calculateCRC(byte[] data) {
        int i, n;
        clearBitMask(RegisterAddress.DivIrqReg, (byte) 0x04);
        setBitMask(RegisterAddress.FIFOLevelReg, (byte) 0x80);

        for (i = 0; i < data.length - 2; i++)
            writeRC522(RegisterAddress.FIFODataReg, data[i]);
        writeRC522(RegisterAddress.CommandReg, PCD.CALCCRC.getValue());
        i = 255;
        while (true) {
            n = readRC522(RegisterAddress.DivIrqReg);
            i--;
            if ((i == 0) || ((n & 0x04) > 0))
                break;
        }
        data[data.length - 2] = readRC522(RegisterAddress.CRCResultRegL);
        data[data.length - 1] = readRC522(RegisterAddress.CRCResultRegM);
    }

    private int selectTag(byte[] uuid) {
        CommunicationStatus status;
        byte data[] = new byte[9];
        byte backData[] = new byte[this.MAX_LEN];
        int backBits[] = new int[1];
        int backLen[] = new int[1];
        int i, j;

        data[0] = PICC.SElECTTAG.getValue();
        data[1] = 0x70;
        for (i = 0, j = 2; i < 5; i++, j++)
            data[j] = uuid[i];
        calculateCRC(data);

        status = writeCard(PCD.TRANSCEIVE.getValue(), data, 9, backData, backBits, backLen);
        if (status == MI_OK && backBits[0] == 0x18) return backData[0];
        else return 0;
    }

    public String readUUID() {
        int backBits[] = new int[1];
        byte tagId[] = new byte[5];

        if (sendRequest(PICC.REQIDL.getValue(), backBits) == CommunicationStatus.MI_OK
                && antiColl(tagId) == CommunicationStatus.MI_OK) {
            selectTag(tagId);
            byte[] uuid = new byte[5];
            System.arraycopy(tagId, 0, uuid, 0, 5);
            return Converter.bytesToHex(uuid);
        } else {
            return null;
        }
    }

}


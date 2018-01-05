package com.pablo.rc522;


import com.pablo.rc522.exception.CommunicationException;

import java.util.Optional;

/**
 * Created by Liang on 2016/3/7.
 */

public class Main {
    public static void main(String[] args) throws InterruptedException {
        while(true) {
            try {
                RC522 rc522 = new RC522();
                final Optional<String> strUID = Optional.ofNullable(rc522.readUUID());
                if (strUID.isPresent()) {
                    System.out.println("UUID=" + strUID.get().substring(0, 8));
                } else {
                    System.out.println("Waiting...");
                }
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
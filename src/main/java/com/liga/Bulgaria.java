package com.liga;

import com.liga.bulgaria.BulgariaParser;
import com.liga.orsr.ORSRParser;

import java.io.IOException;

public class Bulgaria {

    public static void main(String[] args) {
        try {
            BulgariaParser.parseFiveK();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

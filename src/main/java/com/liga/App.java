package com.liga;

import com.liga.orsr.ORSRParser;

import java.io.IOException;

public class App {

    public static void main(String[] args) {
        try {
            ORSRParser.parseFiveK();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

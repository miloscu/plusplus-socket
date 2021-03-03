/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.plusplussocket.packet;

/**
 *
 * @author Milos Cupara
 */
public abstract class Packet {
    
    

    public Packet(byte[] input) {
        this.data = input.clone();
        this.type = input[0];
        this.length = input[4];
        this.b1 = input[8];
        this.b2 = input[9];
        this.b3 = input[10];
        this.b4 = input[11];
    }
    
    byte[] data;
    byte type;
    byte length;
    byte b1;
    byte b2;
    byte b3;
    byte b4;
    
    
    abstract void send();

    public byte getType() {
        return type;
    }

    public byte getLength() {
        return length;
    }

    public byte getB1() {
        return b1;
    }

    public byte getB2() {
        return b2;
    }

    public byte getB3() {
        return b3;
    }

    public byte getB4() {
        return b4;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public void setLength(byte length) {
        this.length = length;
    }

    public void setB1(byte b1) {
        this.b1 = b1;
    }

    public void setB2(byte b2) {
        this.b2 = b2;
    }

    public void setB3(byte b3) {
        this.b3 = b3;
    }

    public void setB4(byte b4) {
        this.b4 = b4;
    }

}

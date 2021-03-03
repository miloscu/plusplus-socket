/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.plusplussocket.packet;

import com.mycompany.plusplussocket.socket.ThreadSafeSocketSingleton;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Milos Cupara
 */
public class DummyPacket extends Packet {

    private Socket socket;
    private long expiry;
    private byte timeout;
    private boolean done;
    private OutputStream outputStream;
    private DataOutputStream dataOutputStream;

    public long getExpiry() {
        return expiry;
    }

    public void setExpiry(long expiry) {
        this.expiry = expiry;
    }

    public byte getTimeout() {
        return timeout;
    }

    public void setTimeout(byte timeout) {
        this.timeout = timeout;
    }

    public DummyPacket(byte[] input) throws IOException {
        super(input);
        this.socket = ThreadSafeSocketSingleton.getInstance();
        this.outputStream = socket.getOutputStream();
        this.dataOutputStream = new DataOutputStream(outputStream);
        this.timeout = input[12];
        this.expiry = System.currentTimeMillis() + input[12] * 1000;
        this.done = false;

    }

    public DummyPacket(byte[] input, long expiry) throws IOException {
        super(input);
        this.socket = ThreadSafeSocketSingleton.getInstance();
        this.outputStream = socket.getOutputStream();
        this.dataOutputStream = new DataOutputStream(outputStream);
        this.timeout = input[12];
        this.expiry = expiry;
        this.done = false;

    }

    public boolean isDone() {
        return done;
    }

    public boolean isNotDone() {
        return !done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    @Override
    public void send() {
        if (expiry < System.currentTimeMillis()) {
            Date date = new Date(expiry);
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String dateFormatted = formatter.format(date);
            System.out.println(String.format("Packet %02X%02X%02X%02X expired on %s", b1, b2, b3, b4, dateFormatted));
            this.done = true;
            return;
        }
        try {
            this.done = false;
            Thread.sleep(expiry - System.currentTimeMillis());
            System.out.println(String.format("Emitted: %02X%02X%02X%02X after %d seconds", b1, b2, b3, b4, timeout));
            dataOutputStream.write(this.data);
            dataOutputStream.flush();
            this.done = true;
        } catch (InterruptedException | IOException ex) {
            Logger.getLogger(DummyPacket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

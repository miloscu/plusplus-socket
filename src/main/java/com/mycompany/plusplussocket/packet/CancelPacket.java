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

/**
 *
 * @author Milos Cupara
 */
public class CancelPacket extends Packet {

    private Socket socket;
    private OutputStream outputStream;
    private DataOutputStream dataOutputStream;
    
    public CancelPacket(byte[] input) throws IOException {
        super(input);
        this.socket = ThreadSafeSocketSingleton.getInstance();
        this.outputStream = socket.getOutputStream();
        this.dataOutputStream = new DataOutputStream(outputStream);
    }

    @Override
    public void send() {
            System.out.println(String.format(" Cancel: %02X%02X%02X%02X", b1, b2, b3, b4));
    }   
}

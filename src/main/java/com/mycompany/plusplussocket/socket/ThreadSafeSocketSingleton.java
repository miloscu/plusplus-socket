/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.plusplussocket.socket;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 *
 * @author Milos Cupara
 */
public class ThreadSafeSocketSingleton extends Socket{
    
    private static volatile Socket instance;// = new Socket("hermes.plusplus.rs", 4000);
    private static Object mutex = new Object();

    private ThreadSafeSocketSingleton() throws IOException {
        this.instance = new Socket("hermes.plusplus.rs", 4000);
    }

    public static Socket getInstance() throws IOException {
        Socket result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null) {
                    instance = result = new Socket("hermes.plusplus.rs", 4000);
                }
            }
        }
        return result;
    }

}

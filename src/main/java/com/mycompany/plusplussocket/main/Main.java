/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.plusplussocket.main;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import com.mycompany.plusplussocket.packet.DummyPacket;
import com.mycompany.plusplussocket.packet.CancelPacket;
import com.mycompany.plusplussocket.packet.DummyPacketDao;
import com.mycompany.plusplussocket.socket.ThreadSafeSocketSingleton;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * "hermes.plusplus.rs", 4000
 *
 * @author Milos Cupara
 */
public class Main implements Runnable {

    public static void main(String[] args) {
        Main obj = new Main();
        obj.run();
    }

    @Override
    public void run() {

        try {
            Scanner inputReader = new Scanner(System.in);

            Thread inputThread = new Thread() {
                @Override
                public void run() {
                    while (true) {
                        String input = inputReader.nextLine();
                        if (true) {
                            System.exit(0);
                        }
                    }
                }
            };
            inputThread.start();

            String databasePath = "jdbc:h2:./packet-database";
            DummyPacketDao database = new DummyPacketDao(databasePath);

            Socket socket = ThreadSafeSocketSingleton.getInstance();
            InputStream stream = socket.getInputStream();

            byte[] data = new byte[16];
            int count;
            int counter = 0;

            CopyOnWriteArrayList< DummyPacket> packetList = database.list()
                    .stream()
                    .collect(Collectors.toCollection(CopyOnWriteArrayList::new));

            System.out.println("\n*********************************\nStarting program. Press ENTER to exit.");

            if (packetList.size() > 0) {
                System.out.print("*********************************\nRetrieving unsent packets from database and intercepting");
            } else {
                System.out.print("*********************************\nIntercepting");
            }

            System.out.println(" packets from server\n"+ "*********************************");

            packetList.forEach(packet -> {
                Thread beginningThread = new Thread() {
                    @Override
                    public void run() {
//                        System.out.println(String.format("Hey I exist and I am %02X", packet.getB1()));
                        packet.send();
                    }
                };
                beginningThread.start();
            });

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    System.out.println("\n*********************************\nAdding unsent packages to database");
                    database.dropTable();
                    packetList.stream().filter(packet -> !packet.isDone()).forEach(packet -> {
                        database.add(packet);
                    });
                    try {
                        System.out.println("*********************************\nExiting");
                        socket.close();
                    } catch (IOException ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            });

            while ((count = stream.read(data)) != -1) {
                counter++;
                switch (data[0]) {
                    case 1:
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    System.out.println(String.format("Receive: %02X%02X%02X%02X with timeout %dseconds", data[8], data[9], data[10], data[11], data[12]));
                                    DummyPacket dummyPacket = new DummyPacket(data);
                                    packetList.add(dummyPacket);
                                    dummyPacket.send();
                                    return;
                                    //                                database.add(dummyPacket);
                                } catch (IOException ex) {
                                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        };
                        thread.start();

                        break;
                    case 2:
                        System.out.println("Cancel received");
                        byte[] cancelData = new byte[]{
                            data[0],
                            data[1],
                            data[2],
                            data[3],
                            data[4],
                            data[5],
                            data[6],
                            data[7],
                            data[8],
                            data[9],
                            data[10],
                            data[11],};
                        CancelPacket cp = new CancelPacket(cancelData);
                        cp.send();
                        break;
                    default:
                        System.out.println("Losing packet");
                }

            }
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("IO Error in Main");
        }
    }
}

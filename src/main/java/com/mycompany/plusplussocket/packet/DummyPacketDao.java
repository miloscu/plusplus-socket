package com.mycompany.plusplussocket.packet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DummyPacketDao {

    private String databasePath;

    public DummyPacketDao(String databasePath) {
        this.databasePath = databasePath;
    }

    public void add(DummyPacket packet) {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO DummyPacket (type,length,b1,b2,b3,b4,timeout,expiry) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            stmt.setByte(1, packet.getType());
            stmt.setByte(2, packet.getLength());
            stmt.setByte(3, packet.getB1());
            stmt.setByte(4, packet.getB2());
            stmt.setByte(5, packet.getB3());
            stmt.setByte(6, packet.getB4());
            stmt.setByte(7, packet.getTimeout());
            stmt.setLong(8, packet.getExpiry());
            stmt.executeUpdate();
            System.out.println(String.format("Adding %02X%02X%02X%02X", packet.getB1(), packet.getB1(), packet.getB2(), packet.getB3(), packet.getB4()));
        } catch (SQLException ex) {
            System.out.println("Error adding packet in DAO");
            Logger.getLogger(DummyPacketDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void dropTable() {
        try (Connection connection = createConnectionAndEnsureDatabase()) {
            PreparedStatement stmt = connection.prepareStatement("DROP TABLE IF EXISTS DummyPacket");
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Error dropping table in DAO");
        }
    }

    public List<DummyPacket> list() throws IOException {
        List<DummyPacket> packets = new ArrayList<>();
        try (Connection connection = createConnectionAndEnsureDatabase();
                ResultSet results = connection.prepareStatement("SELECT * FROM DummyPacket").executeQuery()) {
            while (results.next()) {
                packets.add(new DummyPacket(new byte[]{results.getByte("type"), 0, 0, 0,
                    results.getByte("length"), 0, 0, 0,
                    results.getByte("b1"),
                    results.getByte("b2"),
                    results.getByte("b3"),
                    results.getByte("b4"),
                    results.getByte("timeout"), 0, 0, 0}, results.getLong("expiry")));
            }
        } catch (SQLException ex) {
            System.out.println("Error in retrieving packets in DAO");
            Logger.getLogger(DummyPacketDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return packets;
    }

    private Connection createConnectionAndEnsureDatabase() throws SQLException {
        Connection conn = DriverManager.getConnection(this.databasePath, "sa", "");
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS DummyPacket (type BINARY(1), length BINARY(1), b1 BINARY(1), b2 BINARY(1), b3 BINARY(1), b4 BINARY(1),timeout BINARY(1),expiry long,id int auto_increment primary key)").execute();
        return conn;
    }
}

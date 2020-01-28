package io.github.williamtriinh;

import java.net.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class ScoreboardServer {
    public static Server server;

    public void start(String host, int port) {
        server = new Server(this, new InetSocketAddress(host, port));
        server.run();
    }

    public static void stop() {
        try
        {
            System.out.println("Stopping scoreboard server");
            server.stop();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public class Server extends WebSocketServer
    {
        private ScoreboardServer parent;

        public Server(ScoreboardServer parent, InetSocketAddress address)
        {
            super(address);
            this.parent = parent;
        }

        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake)
        {
            System.out.println("Scoreboard connected");
            conn.send("{\"msg\": \"on_connected\", \"ip\": \""+parent.server.getAddress().getHostName()+"\"}");
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote)
        {
            System.out.println("Scoreboard disconnected, reason: " + reason);
        }

        @Override
        public void onMessage(WebSocket conn, String message)
        {
            JsonObject jsonMessage = JsonParser.parseString(message).getAsJsonObject();
            // JsonObject jsonData = (!jsonMessage.get("data").isJsonNull() ? jsonMessage.get("data").getAsJsonObject() : new JsonObject());
            switch (jsonMessage.get("msg").getAsString())
            {
                case "start_game":
                    GameServer.startGame();
                    break;
                case "stop_game":
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onError(WebSocket conn, Exception ex)
        {
            ex.printStackTrace();
        }

        @Override
        public void onStart()
        {
            System.out.println("Scoreboard server listening on port " + this.getPort());
        }
        
    }

}
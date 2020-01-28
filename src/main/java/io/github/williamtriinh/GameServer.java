package io.github.williamtriinh;

import java.net.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import io.github.williamtriinh.PlayerManager;

public class GameServer
{
    public static Server ws;
    public static Game game;
    private boolean isGameRunning = true;

    // Begins the websocket server and the game (thread) loop
    public void start(String host, int port)
    {
        ws = new Server(this, new InetSocketAddress(host, port));
        ws.start();

        game = new Game();
        game.start();
    }

    public static void startGame()
    {
        ws.broadcast("{\"msg\": \"start_game\"}");
    }

    public void stop()
    {
        try {
            System.out.println("Stopping game server");
            ws.stop();
            isGameRunning = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class Game extends Thread
    {

        @Override
        public void run()
        {
            try
            {
                while (isGameRunning)
                {
                    ws.broadcast(
                        "{\"msg\": \"update_players\", \"data\": "+PlayerManager.getInstance().players.toString()+"}"
                    );
                    Thread.sleep(1000 / 30);
                }
                System.out.println("Stopping game server thread");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    
    public class Server extends WebSocketServer
    {
        GameServer parent;

        public Server(GameServer parent, InetSocketAddress address)
        {
            super(address);
            this.parent = parent;
        }

        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake)
        {
            System.out.println("Player connected");

            String id = String.valueOf(System.nanoTime());
            String address = conn.getLocalSocketAddress().getHostString() + ":" + conn.getLocalSocketAddress().getPort();
            JsonObject jsonObj = JsonParser.parseString("{\"id\": \""+id+"\", \"x\": 90, \"dist\": 0, \"adrs\": \""+address+"\"}").getAsJsonObject();
            
            // Add player to the player manager
            PlayerManager.getInstance().playerIds.put(address, id);

            PlayerManager.getInstance().players.add(id, jsonObj);

            // Broadcast to other players that someone joined
            broadcast(
                "{\"msg\": \"new_player\", \"data\": "+jsonObj.toString()+"}"
            );
            
            PlayerManager.getInstance().printPlayers();

        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote)
        {
            System.out.println("Player disconnected, reason: " + reason);
            String address = conn.getLocalSocketAddress().getHostString() + ":" + conn.getLocalSocketAddress().getPort();
            String playerId = PlayerManager.getInstance().playerIds.get(address);

            // Notify the scoreboard
            ScoreboardServer.server.broadcast("{\"msg\": \"player_left\", \"data\": { \"id\": "+playerId+" }}");

            // Remove player from the player manager
            PlayerManager.getInstance().players.remove(playerId);
            PlayerManager.getInstance().players.remove(address);
        }

        @Override
        public void onMessage(WebSocket conn, String message)
        {
            try
            {
                JsonObject jsonMessage = JsonParser.parseString(message).getAsJsonObject();
                JsonObject jsonData = jsonMessage.get("data").getAsJsonObject();
                switch (jsonMessage.get("msg").getAsString())
                {
                    // When the user has picked their driver's name
                    case "player_picked_name":
                        ScoreboardServer.server.broadcast("{\"msg\": \"player_joined\", \"data\":{ \"id\":"+jsonData.get("id")+", \"name\": "+jsonData.get("name")+" }}");
                        break;
                    case "update_player":
                        PlayerManager.getInstance().players.add(jsonData.get("id").getAsString(), jsonData);
                        break;
                    default:
                        break;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(WebSocket conn, Exception ex)
        {
            ex.printStackTrace();
        }

        @Override
        public void onStart() {
            System.out.println("Game server listening on port " + this.getPort());
        }

    }
}
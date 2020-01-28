package io.github.williamtriinh;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.github.williamtriinh.Player;

public class PlayerManager
{
    // Eager initialization singleton
    private static final PlayerManager instance = new PlayerManager();
    public HashMap<String, String> playerIds = new HashMap();
    public JsonObject players = JsonParser.parseString("{}").getAsJsonObject();

    private PlayerManager() {}

    public static PlayerManager getInstance()
    {
        return instance;
    }

    public void printPlayers()
    {
        System.out.println(players.toString());
    }
}
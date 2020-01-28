package io.github.williamtriinh;

import java.util.Scanner;

import io.github.williamtriinh.ScoreboardServer;
import io.github.williamtriinh.GameServer;

public class App 
{
    private Thread scoreboardThread;
    private Thread gameServerThread;

    public static ScoreboardServer scoreboardServer;
    public GameServer gameServer;

    private final String ip = "localhost";
    private final int scoreboardPort = 2039;
    private final int gameServerPort = 2038;

    public static void main( String[] args )
    {

        App app = new App();
        app.start();

    }

    public void start()
    {
        System.out.println("Starting server on \"" + ip + "\"");

        scoreboardThread = new Thread()
        {
            @Override
            public void run()
            {
                scoreboardServer = new ScoreboardServer();
                scoreboardServer.start(ip, scoreboardPort);
            }
        };

        gameServerThread = new Thread()
        {
            @Override
            public void run()
            {
                gameServer = new GameServer();
                gameServer.start(ip, gameServerPort);
            }
        };

        scoreboardThread.start();
        gameServerThread.start();

        new Thread()
        {
            @Override
            public void run()
            {
                Scanner scan = new Scanner(System.in);
                while(true)
                {
                    String input = scan.next();
                    if (input.equals("stop"))
                    {
                        ScoreboardServer.stop();
                        gameServer.stop();
                        break;
                    }
                }
            }
        }.start();

    }

}

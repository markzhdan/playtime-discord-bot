package Main;

import Listeners.Commands;
import Utility.APIConnection;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;
import java.util.Timer;
import java.util.TimerTask;

public class Main
{
    public static JDA jda;
    private static MongoDatabase database;
    private static final Dotenv config = Dotenv.configure().load();

    public static void main(String[] args) throws LoginException
    {
        jda = JDABuilder.createDefault(config.get("TOKEN")).setActivity(Activity.playing("0 Online")).build();
        jda.getPresence().setStatus(OnlineStatus.ONLINE);

        MongoClient mongoClient = MongoClients.create(config.get("DBLINK"));
        database = mongoClient.getDatabase("IvoryPlayerData");

        jda.addEventListener(new Commands());

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                // Task here ...
                APIConnection api = new APIConnection();
                jda.getPresence().setActivity(Activity.playing(api.Connect() + " Online"));
            }
        }, 0, 60000);
    }

    public static MongoDatabase getDatabase() { return database; }
}

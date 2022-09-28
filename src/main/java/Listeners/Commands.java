package Listeners;

import Main.Main;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bson.Document;

import java.awt.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Commands extends ListenerAdapter
{
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split(" ");
        DecimalFormat df = new DecimalFormat("#.#");

        if (args[0].equalsIgnoreCase("!time")) {
            if (args.length <= 2) {
                event.getChannel().sendMessage("Command Format: !time [ign] dd-mm-yyyy").queue();
            }
            else if(args[2].equalsIgnoreCase("week"))
            {
                String[] date;
                int totalWeekTimeSeconds = 0;
                EmbedBuilder eb = new EmbedBuilder();

                for(int i = 0; i < 7; i++)
                {
                    date = LocalDateTime.now().minusDays(i).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")).split("-");
                    int time = getTime(args[1], date[0] + "-" + date[1] + "-" + date[2]);
                    eb.addField(date[0] + "-" + date[1] + "-" + date[2] + " : " + df.format((double)time/3600) + " Hours", "", false);
                    totalWeekTimeSeconds += time;

                    date[0] = String.valueOf(Integer.parseInt(date[0]) - 1);
                }

                eb.setTitle(args[1] + "'s Playtime", null);
                eb.setColor(Color.red);
                eb.setFooter("by Cuft", event.getMember().getUser().getAvatarUrl());
                eb.setDescription("Total Weekly Playtime: " + df.format((double)totalWeekTimeSeconds/3600));
                event.getChannel().sendMessage(eb.build()).queue();
            }
            else
            {
                event.getChannel().sendMessage(getTime(args[1], args[2]) + " Seconds played").queue();
                //event.getChannel().sendMessage(getTime(args[0] + args[2] + "time" + "args[0")).queue();
            }
        }
    }

    public int getTime(String username, String date)
    {
        for(String collectionName : Main.getDatabase().listCollectionNames())
        {
            // sets collection
            MongoCollection<Document> collection = Main.getDatabase().getCollection(collectionName);

            // if collection has username of person, go to next step
            Document usernameDoc = new Document("username", username);
            Document found = collection.find(usernameDoc).first();

            // next step
            if(found != null)
            {
                // get document for supplied date
                Document dateDoc = new Document("date", date);
                Document fnd = collection.find(dateDoc).first();
                if(fnd != null)
                {
                    int totalTimeSeconds = 0;
                    for(int i = 0; i < 24; i++)
                    {
                        try { totalTimeSeconds += fnd.getInteger(String.format("%02d", i)); }
                        catch (Exception ignored){}
                    }
                    return totalTimeSeconds;
                }
                else
                {
                    //player has not played on given date
                    return 0;
                }
            }
        }
        //user does not exist
        return 0;
    }
}

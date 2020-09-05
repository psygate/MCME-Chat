package me.gilan.mcmetesting.utils;

import com.mcmiddleearth.pluginutil.message.MessageType;
import com.mcmiddleearth.pluginutil.message.MessageUtil;
import com.mcmiddleearth.pluginutil.NumericUtil;
import me.gilan.mcmetesting.MCMEtesting;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import com.mcmiddleearth.pluginutil.message.FancyMessage;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class NameHistoryUtil{

     public static List<FancyMessage> getFancyList(String name) {
        //Get UUID
        URL url = null;
        try {
            url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Scanner sc = null;
        try {
            sc = new Scanner(url.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        while (sc.hasNext()) {
            sb.append(sc.next());

        }

        String result = sb.toString();
        int start = result.lastIndexOf(":") + 2;
        int end = start + 32;
        String playerUUID = result.substring(start, end);

        //Get all names from UUID
        URL url1 = null;
        try {
            url1 = new URL("https://api.mojang.com/user/profiles/" + playerUUID + "/names");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Scanner sc1 = null;
        try {
            sc1 = new Scanner(url1.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder sb1 = new StringBuilder();
        while (sc1.hasNext()) {
            sb1.append(sc1.next());

        }

        String info = sb1.toString();


        //NAMES
        List<String> names = new ArrayList<>();

        int length = info.length();
        int stringPos = 0;
        for (int i = 0; i < length; i++) {
            char current = info.charAt(stringPos);
            //Checks if at beginning of section
            if (current == 123) {
                int start1 = stringPos + 9;
                int count = 9;
                while (info.charAt(stringPos + count) != 125) {
                    if (info.charAt(stringPos + count) == 58) {
                        int end1 = stringPos + count;
                        String name1 = info.substring(start1, end1);
                        name1 = name1.replace("\"", "");
                        name1 = name1.replace(",", "");
                        name1 = name1.replaceAll("changedToAt", "");
                        names.add(name1);

                    }
                    count++;
                }
            }
            stringPos++;
        }


        //DATES
        List<String> dates = new ArrayList<>();

        int dateLength = info.length();
        int datePos = 0;
        for (int i = 0; i < dateLength; i++) {
            char current = info.charAt(datePos);
            if (current == 58) {
                int num = datePos + 1;
                if (info.charAt(num) > 47 && info.charAt(num) < 58) {
                    int start1 = num;
                    int end1 = num + 13;
                    String date = info.substring(start1, end1);
                    long milliSec = Long.parseLong(date);
                    DateFormat simple = new SimpleDateFormat("dd MMM yyyy");
                    Date newDate = new Date(milliSec);
                    dates.add(simple.format(newDate));

                }
            }
            datePos++;
        }

        int firstIndex = info.indexOf("},");
        if(firstIndex==-1){
            firstIndex = info.indexOf("}]");
        }
        int start1 = 10;
        int end1 = firstIndex - 1;
        String name1 = info.substring(start1, end1);
        names.add(0, name1);
        dates.add(0, "Account Created");


        int size = names.size();


        List<FancyMessage> fancyList = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            FancyMessage element = new FancyMessage(MessageType.INFO, MCMEtesting.getMessageUtil())
                    .addSimple(names.get(i) + ", " + dates.get(i));
            fancyList.add(element);
        }


        return fancyList;
    }
}




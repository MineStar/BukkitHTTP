package com.bukkit.gemo.BukkitHTTP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Page {
    public String line = "";

    public Page(String template) {
        try {
            File thisFile = new File(template);
            if (!thisFile.exists())
                return;

            BufferedReader in = new BufferedReader(new FileReader(template));
            String zeile = "";
            while ((zeile = in.readLine()) != null) {
                // lines.add(zeile);
                line += zeile + "\r\n";
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void replaceText(String placeHolder, String newText) {
        line = line.replace(placeHolder, newText);

    }
}

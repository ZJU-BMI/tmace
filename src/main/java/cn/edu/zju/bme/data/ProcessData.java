package cn.edu.zju.bme.data;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;

public class ProcessData {

    public static void processAll(String rootPath) {

    }

    public static void processOne(String filePath) {
        try {
            Document doc = Jsoup.parse(new File(filePath), "utf-8");
            System.out.println(doc.text());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        processOne("resources/origin/3556_9_2.html");
    }

}

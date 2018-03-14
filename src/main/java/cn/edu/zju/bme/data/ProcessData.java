package cn.edu.zju.bme.data;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ProcessData {

    private static void processAll() {
        File root = new File("resources/origin");
        File[] files = root.listFiles();
        assert files != null;

        for (File file : files) {
            processOne(file.getPath());
        }
    }

    private static void processOne(String filePath) {
        try {
            Document doc = Jsoup.parse(new File(filePath), "utf-8");
            if (!doc.hasText()) return;

            System.out.println(filePath);
            Element element;
            if (doc.select("div").isEmpty()) {
                if (doc.select("p").size() < 2) return;
                element = doc.select("p").get(1);
            } else {
                if (doc.select("div").size() < 2) return;
                element = doc.select("div").get(1);
            }
            String content = element.text(); // 取查体结果
            // 分句，根据句号换行
            content = content.replaceAll("。", "。\n").trim();

            String savePath = filePath.replace("origin", "sentences").replace("html", "txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(new File(savePath)));
            out.write(content);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
//        processAll();
        processOne("resources/origin/448_1_11.html");
    }

}

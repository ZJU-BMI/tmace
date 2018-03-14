package cn.edu.zju.bme.util;

import cn.edu.zju.bme.data.Document;
import cn.edu.zju.bme.data.Documents;
import cn.edu.zju.bme.data.Sentence;

import java.io.*;
import java.util.*;
import java.util.stream.IntStream;

public class Util {
    public static List<String> indexToWord;
    public static Map<String, Integer> bagOfWord;

    public static void writeObject(String path, Object o) {
        try{
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(path));
            os.writeObject(o);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object readObject(String path) {
        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(path));
            return is.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Documents loadData(String path) throws IOException {
        File root = new File(path);
        assert root.isDirectory();
        File[] files = root.listFiles();
        assert files != null;

        Documents result = new Documents();

        Map<String, Integer> bagOfWord = new HashMap<>();
        List<String> indexToWord = new ArrayList<>();

        bagOfWord.put("digital", 0); // 数字一律归到digital中
        indexToWord.add("digital");

        for (File file : files) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            Document document = new Document();

            while ((line = reader.readLine()) != null) {
                String[] words = line.trim().split(" ");
                Sentence sentence = new Sentence();

                for (String word : words) {
                    try {
                        Double.parseDouble(word);
                        word = "digital";
                    } catch (Exception ignored) {

                    }
                    if (!bagOfWord.containsKey(word)) {
                        bagOfWord.put(word, bagOfWord.size());
                        indexToWord.add(word);
                    }
                    sentence.add(bagOfWord.get(word));
                }

                document.add(sentence);
            }
            document.setName(file.getName());

            result.add(document);
        }

        Util.indexToWord = indexToWord;
        Util.bagOfWord = bagOfWord;

        return result;
    }

    public static String sentenceToString(Sentence sentence) {
        List<Integer> words = sentence.getWords();
        return words.stream().map(x -> indexToWord.get(x)).reduce("", String::concat);
    }

    public static String[] argsort(double[][] array) {
        int row = array.length;
        List<String> result = new ArrayList<>();
        List<Double> value = new ArrayList<>();

        for (int i=0; i<row; i++) {
            for (int j=0; j<array[i].length; j++) {
                result.add(i + "," + j);
                value.add(array[i][j]);
            }
        }

        int length = result.size();

        return IntStream.range(0, length)
                .boxed()
                .map(x -> new AbstractMap.SimpleEntry<>(result.get(x), value.get(x)))
                .sorted((x, y) -> y.getValue().compareTo(x.getValue()))
                .map(AbstractMap.SimpleEntry::getKey)
                .toArray(String[]::new);
    }

    public static void main(String[] args) {
        String a = "0.9%";
        try {
            double b = Double.parseDouble(a);
        } catch (Exception e) {
            System.out.println("...");
        }
    }

}

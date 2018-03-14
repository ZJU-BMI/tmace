package cn.edu.zju.bme.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Document implements Iterable<Sentence>{
    private List<Sentence> sentences;
    private String name; // 文件名

    public Document() {
        sentences = new ArrayList<>();
        name = "";
    }

    public Document(List<Sentence> sentences, String name) {
        this.sentences = sentences;
        this.name = name;
    }

    public void add(Sentence sentence) {
        this.sentences.add(sentence);
    }

    public Sentence get(int index) {
        return sentences.get(index);
    }

    public int size() {
        return sentences.size();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public Iterator<Sentence> iterator() {
        return sentences.iterator();
    }
}

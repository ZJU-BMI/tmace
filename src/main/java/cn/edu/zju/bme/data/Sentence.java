package cn.edu.zju.bme.data;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Getter
public class Sentence implements Iterable<Integer>{
    private List<Integer> words;

    public Sentence() {
        words = new ArrayList<>();
    }

    public Sentence(List<Integer> words) {
        this.words = words;
    }

    public void add(Integer word) {
        words.add(word);
    }

    public Integer get(int index) {
        return words.get(index);
    }

    @Override
    public Iterator<Integer> iterator() {
        return words.iterator();
    }

}

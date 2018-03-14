package cn.edu.zju.bme.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Documents implements Iterable<Document>{
    private List<Document> documents;

    public Documents() {
        this.documents = new ArrayList<>();
    }

    public Documents(List<Document> documents) {
        this.documents = documents;
    }

    public void add(Document document) {
        this.documents.add(document);
    }

    public Document get(int index) {
        return documents.get(index);
    }

    public int size() {
        return documents.size();
    }

    @Override
    public Iterator<Document> iterator() {
        return documents.iterator();
    }
}

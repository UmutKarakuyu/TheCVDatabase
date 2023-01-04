package com.example.se302_project;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;

public class Index {
    private String indexDir;
    private int hitsPerPage;

    private StandardAnalyzer analyzer;
    private Directory index;
    private IndexWriter indexWriter;

    public Index(String indexDir, int hitsPerPage) throws IOException {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        Directory index = FSDirectory.open(Paths.get(indexDir));
        IndexWriter indexWriter = new IndexWriter(index, config);

        this.indexDir = indexDir;
        this.hitsPerPage = hitsPerPage;
        this.index = index;
        this.analyzer = analyzer;
        this.indexWriter = indexWriter;
    }

    private byte[] serialize(ArrayList<String> tags) throws IOException {
        ArrayList<Object> tagsObj = new ArrayList<Object>(
                tags.stream().map(tag -> (Object) tag).collect(Collectors.toList()));
        ByteArrayOutputStream bos = new ByteArrayOutputStream(200000);
        ObjectOutputStream os = new ObjectOutputStream(bos);
        os.writeObject(tagsObj);
        os.close();
        return bos.toByteArray();
    }

    private List<String> unserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(bytes));
        ArrayList<Object> result = (ArrayList<Object>) is.readObject();
        is.close();
        List<String> docTags = result.stream().map(object -> Objects.toString(object, null))
                .collect(Collectors.toList());
        return docTags;
    }

    public void addDoc(String name, String date, String text, ArrayList<String> tags, String template_title) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("name", name, Field.Store.YES));
        doc.add(new TextField("date", date, Field.Store.YES));
        doc.add(new TextField("resume_text", text, Field.Store.YES));
        doc.add(new TextField("template_title", template_title, Field.Store.YES));
        doc.add(new StoredField("tags", this.serialize(tags)));
        String tags_text = "";
        for (String tag : tags) {
            tags_text += tag + " ";
        }
        doc.add(new TextField("tags_text", tags_text, Field.Store.YES));

        this.indexWriter.addDocument(doc);
        this.indexWriter.commit();
    }

    public void deleteDoc(String name) throws IOException {
        this.indexWriter.deleteDocuments(new Term("name", name.trim()));
        this.indexWriter.commit();
    }

    public HashMap<String, String> query(String query_text, ArrayList<String> tagFilters, String templateFilter, String NO_TEMPLATE_FILTER_TEXT)
            throws IOException, ParseException, ClassNotFoundException {

        ArrayList<Query> queries = new ArrayList<>();
        queries.add(new WildcardQuery(new Term("name", "*" + query_text + "*")));
        queries.add(new WildcardQuery(new Term("date", "*" + query_text + "*")));
        queries.add(new WildcardQuery(new Term("resume_text", "*" + query_text + "*")));
        queries.add(new WildcardQuery(new Term("tags_text", "*" + query_text + "*")));

        IndexReader reader = DirectoryReader.open(this.index);
        IndexSearcher searcher = new IndexSearcher(reader);

        HashMap<String, String> query_results = new HashMap<>();
        for (Query q : queries) {
            TopDocs docs = searcher.search(q, this.hitsPerPage);
            ScoreDoc[] hits = docs.scoreDocs;

            for (int i = 0; i < hits.length; ++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                List<String> docTags = this.unserialize(d.getBinaryValue("tags").bytes);

                boolean add2results = false;
                boolean tags_condition = false;
                boolean template_condition = false;
                if (tagFilters.size() != 0) {
                    for (String docTag : docTags) {
                        for (String tagFilter : tagFilters) {
                            if (docTag.equals(tagFilter)) {
                                tags_condition = true;
                                break;
                            }
                        }
                    }
                } else {
                    tags_condition = true;
                }

                if(templateFilter != null){
                    if(!templateFilter.equals(NO_TEMPLATE_FILTER_TEXT)){
                        if(!templateFilter.equals("")){
                            if(d.get("template_title").equals(templateFilter)){
                                template_condition = true;
                            }
                        } else{
                            template_condition = true;
                        }
                    } else{
                        template_condition = true;
                    }
                }else{
                    template_condition = true;
                }

                add2results = tags_condition && template_condition;
                if(add2results){
                    query_results.put(d.get("name"), d.get("date"));
                }
            }

        }

        return query_results;
    }

    public void close() throws IOException {
        this.indexWriter.close();
    }

}
package ResumeNLP;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.Unmarshaller.Listener;

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
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;

public class Index {
    private int hitsPerPage;
    
    private StandardAnalyzer analyzer;
    private Directory index;
    private IndexWriter indexWriter;
    
    public Index(int hitsPerPage) throws IOException{
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        Directory index = new ByteBuffersDirectory();
        IndexWriter indexWriter = new IndexWriter(index, config);
        
        this.hitsPerPage = hitsPerPage;
        this.index = index;
        this.analyzer = analyzer;
        this.indexWriter = indexWriter;
    }

    private byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(200000);
        ObjectOutputStream os = new ObjectOutputStream(bos);
        os.writeObject(obj);
        os.close();
        return bos.toByteArray();
    }

    private Object unserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Object result = is.readObject();
        is.close();
        return result;
    }

    public void addDoc(String name, String text, ArrayList<String> tags) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("name", name, Field.Store.YES));
        doc.add(new TextField("text", text, Field.Store.YES));

        ArrayList<Object> tagsObj = new ArrayList<Object>(tags.stream().map(tag -> (Object) tag).toList());
        doc.add(new StoredField("tags", this.serialize(tagsObj)));

        this.indexWriter.addDocument(doc);
        this.indexWriter.commit();
    }

    public void deleteDoc(String name) throws IOException{
        this.indexWriter.deleteDocuments(new Term("name", name));
    }

    public void query(String query_text, ArrayList<String> tagFilters) throws IOException, ParseException, ClassNotFoundException{
        Query q = new QueryParser("text", analyzer).parse(query_text);

        IndexReader reader = DirectoryReader.open(this.index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(q, this.hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;

        System.out.printf("Query: %s\n", query_text);
        for(int i=0;i<hits.length;++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            ArrayList<Object> docTagsObj = (ArrayList<Object>) this.unserialize(d.getBinaryValue("tags").bytes);
            List<String> docTags = docTagsObj.stream().map(object -> Objects.toString(object, null)).toList();

            if(tagFilters.size() != 0){
                for(String docTag: docTags){
                    for(String tagFilter: tagFilters){
                        if(docTag.equals(tagFilter)){
                            System.out.println((i + 1) + ". " + d.get("name") + ". " + docTags);
                            break;
                        }
                    }
                }
            } else{
                System.out.println((i + 1) + ". " + d.get("name") + ". " + docTags);
            }
        }
        System.out.println();
        
        reader.close();
    }

    public void close() throws IOException{
        this.indexWriter.close();
    }

}

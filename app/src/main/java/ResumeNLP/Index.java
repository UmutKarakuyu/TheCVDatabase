package ResumeNLP;

import java.io.IOException;
import java.util.ArrayList;

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

    public void addDoc(String name, String text, ArrayList<String> tags) throws IOException {
        Document doc = new Document();

        doc.add(new TextField("name", name, Field.Store.YES));
        doc.add(new TextField("text", text, Field.Store.YES));
        for(String tag: tags){
            doc.add(new TextField("tag", tag, Field.Store.YES));
        }

        this.indexWriter.addDocument(doc);
        this.indexWriter.commit();
    }

    public void deleteDoc(String name) throws IOException{
        this.indexWriter.deleteDocuments(new Term("name", name));
    }

    public void query(String query_text, String field) throws IOException, ParseException{
        switch(field){
            case "name": break;
            case "text": break;
            case "tag": break;
            default:
                throw new IllegalArgumentException("Field value only can be 'name', 'text' or 'tag'.");
        }

        Query q = new QueryParser(field, analyzer).parse(query_text);

        IndexReader reader = DirectoryReader.open(this.index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(q, this.hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;

        System.out.println("Found " + hits.length + " hits.");
        for(int i=0;i<hits.length;++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            System.out.println((i + 1) + ". " + d.get("name"));
        }
        
        reader.close();
    }

    public void close() throws IOException{
        this.indexWriter.close();
    }

}

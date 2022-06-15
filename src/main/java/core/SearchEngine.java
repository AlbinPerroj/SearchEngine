package core;

import help.Status;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.CollectorManager;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

public class SearchEngine {

    private final String indexDirectory;

    public SearchEngine(String indexDirectory)
    {
        this.indexDirectory = indexDirectory;
    }

    public void createIndexDirectory() throws IndexOutOfBoundsException, NullPointerException, IOException
    {
        File f = new File(this.indexDirectory);
        f.mkdir();
    }

    public Status addDocToIndex(String docId, ArrayList<String> tokens) throws IOException {

        FSDirectory dir =FSDirectory.open(Path.of(this.indexDirectory));

        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new StandardAnalyzer());

        IndexWriter indexWriter = new IndexWriter(dir, indexWriterConfig);

        Document doc = new Document();

        doc.add(new StringField("id", docId, Field.Store.YES));

        for(var token:tokens)
        {
            doc.add(new StringField("token", token, Field.Store.YES));
        }

        indexWriter.updateDocument ( new Term("id", docId), doc);
        indexWriter.commit();
        indexWriter.close();

        return new Status(true, "index ok " + docId);
    }

    public Status search(String query) throws IOException, ParseException, NullPointerException, IndexOutOfBoundsException {

        FSDirectory dir =FSDirectory.open(Path.of(this.indexDirectory));

        IndexReader indexReader = DirectoryReader.open(dir);

        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        QueryParser qp = new QueryParser("token", new StandardAnalyzer());

        Query q =qp.parse(query);

        var scoreDocs = indexSearcher.search(q, 1000).scoreDocs;

        String docIds = "";

        for(int j=0; j < scoreDocs.length; j++)
        {
            Document d = indexSearcher.doc(scoreDocs[j].doc);
            docIds = docIds + d.get("id") + " ";
        }

        if(docIds.isBlank())
            docIds = "none";

        return new Status(true, "query results    " + docIds );
    }
}

package com.xuxinhui.Test;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;


import java.io.File;
import java.io.IOException;
import java.util.List;

public class LuceneTest {
    public static void main(String[] args) throws IOException {

        //标准分词器
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_44);

        //指定Lucene版本 分词器
        IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_44,analyzer);

        FSDirectory d = FSDirectory.open(new File("i:/idnex"));

        //第一步.索引输出目录,一个是索引相关配置
        IndexWriter indexWriter = new IndexWriter(d, conf);

        //第二步.Document叫文档对象,Field域对象
         //doc对象就代表一个文件,Field就代表一个属性如 名字 作者 文章 等等。。
           //在doc里面外面会创建很多个域对象
        Document doc = new Document();
                 //以单子查询方式创建
        doc.add(new TextField("title","玫玫",Field.Store.YES));

        doc.add(new TextField("author","鑫辉",Field.Store.YES));
                //以全字的查询方式创建
        doc.add(new StringField("content","我对你的思念就像大海无边无际一眼望不到边",Field.Store.YES));


        //第三步.建立索引
        indexWriter.addDocument(doc);
        //开始创建
        indexWriter.commit();
        //关闭流
        indexWriter.close();

    }
    @Test
    public   void testIndex() throws IOException {
        //创建 索引检索对象

        //指定索引所在目录
        IndexReader r = DirectoryReader.open(FSDirectory.open(new File("i:/idnex")));

        IndexSearcher indexSearcher = new IndexSearcher(r);

        //创建 索引 查询方式对象   //只能以单子查询                 //查询对象
        TermQuery termQuery = new TermQuery(new Term("content","我对你的思念就像大海无边无际一眼望不到边"));

        //开始查询
        //查询方式,查询返回的数量
        TopDocs topDocs = indexSearcher.search(termQuery, 10);

        //返回的topDocs对象封装了 查询结果
        //查询文档根据得分排名先后结果,加了个相关度 分数高 相关度就越高
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;

        //查询出文档符合结果的数量
        int totalHits = topDocs.totalHits;

        System.out.println("一共多个个文档"+totalHits);

        //变量文档数组,获取文档信息
        for (ScoreDoc scoreDoc : scoreDocs) {
            //代表对应文档的id
            int doc = scoreDoc.doc;
            //代表该文档的相关度得分
            float score = scoreDoc.score;
            System.out.println("这篇文档的得分"+score);


            Document doc1 = indexSearcher.doc(doc);
           /* List<IndexableField> fieldlist = fields.getFields();
            for (IndexableField field : fieldlist) {
                System.out.println(field.name()+fields.getValues());
            }*/
            IndexableField title = doc1.getField("title");
            String s = title.stringValue();
            System.out.println("第一句"+s);

            IndexableField author = doc1.getField("author");
            String s1 = author.stringValue();
            System.out.println("第二句"+s1);

            IndexableField content = doc1.getField("content");
            String s2 = content.stringValue();
            System.out.println("第二句"+s2);

        }
    }
}

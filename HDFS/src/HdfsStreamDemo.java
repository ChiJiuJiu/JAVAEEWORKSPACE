import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Before;
import org.junit.Test;

public class HdfsStreamDemo {
    FileSystem fs = null;
    Configuration conf = null;

    @Before
    public void init() throws Exception {
        conf = new Configuration();
        // fs = FileSystem.get(conf);
        // 拿到Hdfs的客户端，它其实就相当于（hadoop fs 命令）
        fs = FileSystem.get(new URI("hdfs://node01:9000"), conf, "root");
    }

    @Test
    // 用流的方式上传
    public void testUpload() throws Exception {
        FileInputStream in = new FileInputStream("f:/a.txt");
        FSDataOutputStream out = fs.create(new Path("/a.txt"));
        IOUtils.copy(in, out);
    }

    // 下载
    @Test
    public void testDown() throws Exception {
        FSDataInputStream in = fs.open(new Path("/aa.txt"));
        FileOutputStream out = new FileOutputStream("f:/aa.txt");
        IOUtils.copy(in, out);
    }

    @Test
    public void testRandom() throws Exception {
        FSDataInputStream in = fs.open(new Path("/a.txt"));
        in.seek(50);
        FileOutputStream out = new FileOutputStream("f:/a.txt");
        IOUtils.copy(in, out);
    }

    @Test
    public void testCat() throws Exception {
        FSDataInputStream in = fs.open(new Path("/a.txt"));
        IOUtils.copy(in, System.out, 100);
    }
}
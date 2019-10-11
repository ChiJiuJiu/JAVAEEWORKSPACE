import java.net.URI;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.fs.permission.FsPermission;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HdfsDemo {
    FileSystem fs = null;
    Configuration conf = null;

    @Before
    public void init() throws Exception {
        conf = new Configuration();
        // conf.set("fs.defaultFS", "hdfs://node01:9000");
        // 拿到一个文件系统操作的客户端实例对象
        // fs = FileSystem.get(conf);
        fs = FileSystem.get(new URI("hdfs://node01:9000"), conf, "root");

    }

    // 上传
    @Test
    public void testUpload() throws Exception {
        fs.copyFromLocalFile(new Path("c:/abc.log"), new Path("/abc.log.copypp"));
        fs.close();
    }

    // 下载
    @Test
    public void testDownload() throws Exception {
        fs.copyToLocalFile(new Path("/aa.txt"), new Path("f:/"));
    }

    // 创建目录
    @Test
    public void mkDir() throws Exception {
        fs.mkdirs(new Path("/cc/bb/aa"));
    }

    // 删除
    @Test
    public void delDir() throws Exception {
        fs.delete(new Path("/a.txt"), true);
    }

    // LocatedFileStatus方法只能列出文件
    @Test
    public void testls() throws Exception {
        RemoteIterator<LocatedFileStatus> listFile = fs.listLocatedStatus(new Path("/"));
        while (listFile.hasNext()) {
            LocatedFileStatus status = listFile.next();
            String name = status.getPath().getName();
            System.out.println("文件名称：" + name);
            System.out.println(status.toString());
            long accessTime = status.getAccessTime();
            System.out.println("访问时间：" + new Date(accessTime));
            long blockSize = status.getBlockSize();
            System.out.println("数据块的大小：" + blockSize);
            long len = status.getLen();
            System.out.println("实际大小：" + len);
            BlockLocation[] blockLocations = status.getBlockLocations();
            for (BlockLocation b1 : blockLocations) {
                String[] hosts = b1.getHosts();
                for (String host : hosts) {
                    System.out.println(host);
                }
            }
            String group = status.getGroup();
            System.out.println("所属组：" + group);
            String owner = status.getOwner();
            System.out.println("所属用户：" + owner);
            long modificationTime = status.getModificationTime();
            System.out.println("修改时间:" + modificationTime);
            FsPermission permission = status.getPermission();
            System.out.println("权限:" + permission.toString());
            short replication = status.getReplication();
            System.out.println("副本数量：" + replication);
            System.out.println("--------------------------------------");
        }
    }

    // 查询文件信息
    // listfiles方法，返回LocatedFileStatus的迭代器，自带递归
    @Test
    public void testls2() throws Exception {
        RemoteIterator<LocatedFileStatus> listFiles = fs.listFiles(new Path("/"), true);
        while (listFiles.hasNext()) {
            LocatedFileStatus next = listFiles.next();
            System.out.println(next.toString());
        }
    }

    // 查询文件信息
    @Test
    public void testls3() throws Exception {
        FileStatus[] listStatus = fs.listStatus(new Path("/"));
        for (FileStatus fs : listStatus) {
            System.out.println(fs.toString());
        }
    }

    @Test
    public void testconf() {
        Iterator<Entry<String, String>> it = conf.iterator();
        while (it.hasNext()) {
            Entry<String, String> entry = it.next();
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
    }

    @After
    public void release() throws Exception {
        fs.close();
    }
}
package com.hadoop.main;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.fs.permission.FsPermission;

public class Main {

    public static void main(String[] args) throws FileNotFoundException, IllegalArgumentException, IOException, InterruptedException, URISyntaxException {
        FileSystem fs = null;
        Configuration conf = null;
        conf = new Configuration();
        fs = FileSystem.get(new URI("hdfs://node1:9000"), conf, "root");
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

}

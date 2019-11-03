import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/*
 * 相当于一个yarn集群的客户端
 * 需要在此封装我们的mr程序的相关运行参数，指定jar包
 * 最后提交给yarn
 */
public class WordCountDriver {
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);
        // 指定本程序的jar包所在的本地路径
        job.setJarByClass(WordCountDriver.class);
        // 指定本业务job要使用的业务类 mapper/reducer
        job.setMapperClass(WordCountMapper.class);
        job.setReducerClass(WordCountReducer.class);
        // 指定mapper输出数据的k-v类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        // 指定最终输出的数据的k-v类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // 判断output文件夹是否存在，如果存在则删除
        Path path = new Path(args[1]);// 取第1个表示输出目录参数（第0个参数是输入目录）
        FileSystem fileSystem = path.getFileSystem(conf);// 根据path找到这个文件
        if (fileSystem.exists(path)) {
            fileSystem.delete(path, true);// true的意思是，就算output有东西，也一带删除
        }
        // 指定job的输入原始文件所在目录
        FileInputFormat.setInputPaths(job, new Path(args[0]));
        // 指定job的输出结果所在目录
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        // 将job中配置的相关参数，以及job所用的java类所在的jar包，提交给yarn运行。
        // job.submit();
        boolean res = job.waitForCompletion(true);
        System.exit(res ? 0 : 1);
    }
}
import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/*
 * 泛型：输入和输出的数据类型
 * KEYIN：默认情况下，是mr框架所读到的一行文本的起始偏移量，类型是long。
 * 但是在hadoop中有自己更精简的序列化接口，所以不直接用，而是用longwritable
 * VALUEIN：默认情况下，是mr框架所读到的一行文本的内容，类型是String
 * KEYOUT：是用户自定义逻辑处理完成之后输出数据中的key，类型由我们来决定，比如
 * 此处是单词，类型是String，用上，用Text
 * VALUEOUT：是用户自定义逻辑处理完成之后输出数据中的value，
 * 在此处是单词次数，类型是integer
 * 用IntWritable
 */
public class WordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
    /*
     * map阶段的业务逻辑就写在在定义的map（）中
     * maptask会对每一行输入的数据调用一次，我们自定义的map（）方法
     */
    @Override
    protected void map(LongWritable key, Text value, Context context)
        throws IOException, InterruptedException {
        // 把maptask传给我们的文本内容先转换成String
        String line = value.toString();
        // 根据空格将这一行切分成单词
        String[] words = line.split(" ");
        // 把单词输出为<单词 1>
        for (String word : words) {
            // 将单词作为key，将次数1作为value，以便于后续的数据分发，
            // 可以根据单词分发，以便于相同的单词回到相同的reduce task
            context.write(new Text(word), new IntWritable(1));
        }

    }

}
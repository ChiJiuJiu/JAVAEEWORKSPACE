import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/*
 * KEYIN,VALUEIN对应mapper输出的KEYOUT,VALUEOUT类型对应
 * KEYOUT,VALUEOUT是自定义reduce逻辑处理结果的输出数据类型
 * KEYOUT 是单词
 * VALUEOUT 是总次数
 */
public class WordCountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
    /*入参key 是一组相同单词的key
     * <apache,1><apache,1><apache,1><apache,1><apache,1><apache,1>
     * <hadoop,1><hadoop,1><hadoop,1><hadoop,1><hadoop,1>
     * <java,1><java,1><java,1><java,1><java,1><java,1>
     */
    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context)
        throws IOException, InterruptedException {
        int count = 0;
        /*Iterator<IntWritable> iterator = values.iterator();
        while (iterator.hasNext()) {
            count += iterator.next().get();}*/
        for (IntWritable value : values) {
            count += value.get();
        }
        context.write(key, new IntWritable(count));
    }
}
package edu.yctc.zyj;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

import edu.yctc.zyj.bloomfilter.BloomFilter;
import edu.yctc.zyj.bloomfilter.BloomFilterStrategies;
import edu.yctc.zyj.bloomfilter.Funnels;

public class Intersection {

    
    /** input file A */
    private static String        FILE_A                          = "D:/bloomFilter/bigfile1.txt";
    /** input file B */
    private static String        FILE_B                          = "D:/bloomFilter/bigfile2.txt";
    /** output file */
    private static String        FILE_OUTPUT                     = "D:/bloomFilter/output.txt";
    /** temp dir */
    private static String        TEMP_DIR                        = "D:/bloomFilter/temp/";

    private static final Charset CHARSET                         = Charset.forName("ISO-8859-1");
    
    /** A∩B */
    private static String        FILE_X                          = TEMP_DIR + "A∩B.txt";
    
    /** 分拆时读BUFFER大小 */
    private static final int     SPLIT_READ_BUFFER_SIZE          = 256 * 1024;
    /** 分拆时总BUFFER大小 */
    private static final int     SPLIT_OUTPUT_BUFFER_SIZE        = 2 * 1024 * 1024;
    /** 分拆的数量 */
    private static final int     SPLIT_SUM                       = 1000;
    /** 拆分时hash的loop次数，数值越大，拆分越均匀，但是用时越长，此值不可小于4 */
    private static final int     HASH_LOOP_TIME                  = 4;
    /** 分拆时每个输出的句柄BUFFER大小 */
    private static final int     SPLIT_OUTPUT_BUFFER_REGION_SIZE = SPLIT_OUTPUT_BUFFER_SIZE / (SPLIT_SUM * 2);
    
    /** 行分隔符字符 */
    private static final int     N                               = 10;
    
    /** BloomFilter Config */
    private static final int     EXPECTED_INSERTION              = 12000000;
    private static final double  FPP                             = 0.06;
    
    public static void main(String[] args) {
        if (args.length == 4) {
            FILE_A = args[0];
            FILE_B = args[1];
            TEMP_DIR = args[2];
            FILE_OUTPUT = args[3];
        }
        long startTime = System.currentTimeMillis();
        
        FILE_X = TEMP_DIR + "A∩B.txt";
        
        threadBloom();
        
        System.out.println("total spent=" + (System.currentTimeMillis() - startTime));
    }
    
    private static void threadBloom() {
        long startTime = System.currentTimeMillis();

        try {
            final CountDownLatch latch = new CountDownLatch(1);

            final BloomFilter<String> bloomFilterA = BloomFilter.create(Funnels.stringFunnel(CHARSET),
                                                                        EXPECTED_INSERTION, FPP,
                                                                        BloomFilterStrategies.MURMUR128_MITZ_64);

            Thread splitAThread = buildNewBloomThreadWithG(FILE_A, FILE_B, FILE_X, bloomFilterA, latch);
            
            splitAThread.start();

            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("threadBloom spent=" + (System.currentTimeMillis() - startTime));
    }
    
    private static Thread buildNewBloomThreadWithG(final String inputPathA, final String inputPathB,
                                                   final String outputPath, final BloomFilter<String> bloomFilter,
                                                   final CountDownLatch latch) {
        return new Thread(() -> {
            long startTime = System.currentTimeMillis();
            try {
                BufferedReader splitBufferReaderA = new BufferedReader(new FileReader(inputPathA),
                                                                       SPLIT_READ_BUFFER_SIZE);
                String s;
                while ((s = splitBufferReaderA.readLine()) != null) {
                    bloomFilter.put(s);
                }
                splitBufferReaderA.close();

                System.out.println("bloom spent=" + (System.currentTimeMillis() - startTime));

                BufferedReader splitBufferReaderB = new BufferedReader(new FileReader(inputPathB),
                                                                       SPLIT_READ_BUFFER_SIZE);
                BufferedWriter splitBufferWriterB = Files.newBufferedWriter(Paths.get(outputPath));
                while ((s = splitBufferReaderB.readLine()) != null) {
                    if (bloomFilter.mightContain(s)) {
                        splitBufferWriterB.write(s);
                        splitBufferWriterB.write(N);
                    }
                }
                splitBufferReaderB.close();
                splitBufferWriterB.close();

                System.out.println("thead spent=" + (System.currentTimeMillis() - startTime));
            } catch (Exception e) {
                e.printStackTrace();
            }
            latch.countDown();
        });
    }
}

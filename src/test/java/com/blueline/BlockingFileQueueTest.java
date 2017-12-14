package com.blueline;

import com.blueline.util.concurrent.BlockingFileQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;


/**
 * BlockingFileQueueTest
 */
public class BlockingFileQueueTest {

    private static  final Logger log = LoggerFactory.getLogger(BlockingFileQueueTest.class);

    public static void main(String[] args) throws Exception {

        final BlockingQueue<byte[]> queue= new BlockingFileQueue("db",1024*1024*64);;
        final byte [] data= "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890".getBytes();

        final int producerCount=10;
        final int eachProducerGeneratesDataCount=1000000; //每个生产者生产数据量
        final int consumerCount=10;

        final AtomicLong count=new AtomicLong();

        for (int i = 0; i < consumerCount; i++) {
            final int ix =i;
            Thread consumer =new Thread(new Runnable() {

                @Override
                public void run() {

                    byte[] data=null;
                    long temp=0;
                    try {
                        while((data = queue.take())!=null) {
                            temp = count.incrementAndGet();
                            if (temp % 10000 == 0 || temp > producerCount * eachProducerGeneratesDataCount) {
                                log.info("Take count:"+ temp);
                            }
                        }
                    } catch (InterruptedException e) {
                        log.warn("",e);
                    }

                }
            });
            consumer.start();
        }


        for (int i = 0; i < producerCount; i++) {
            Thread producer =new Thread(new Runnable() {

                @Override
                public void run() {
                    for (int i1 = 0; i1 < eachProducerGeneratesDataCount; i1++) {
                        try {
                            queue.put(data);
                        } catch (InterruptedException e) {
                            log.warn("",e);
                        }
                    }
                }
            });
            producer.start();
        }


        Thread periodicProducer =new Thread(new Runnable() {

            @Override
            public void run() {
                for (int i1 = 0; i1 < 100; i1++) {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        log.warn("",e);
                    }
                    try {
                        queue.put(data);
                        log.info(" put date");
                    } catch (InterruptedException e) {
                        log.warn("",e);
                    }
                }
            }
        });
        periodicProducer.start();






    }
}

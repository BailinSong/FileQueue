# FileQueue/BlockingFileQueue
**基于：sunli1223/fqueue[https://github.com/sunli1223/fqueue]
修改精简**
## 介绍
此Mq实现**JDK**中**BlockingQueue**接口，去掉了外部协议支持，仅支持本地嵌入式使用，数据文件采用时间戳作为标记。
## 特性 
* 基于磁盘持久化存储。
* 高性能，能达到数十万qps。
* 低内存消耗
* 高效率IO读写算法，IO效率高。
* 纯JAVA代码。支持进程内JVM级别的直接调用。
## 精简特性
* 在不需要强顺序的场景下，支持多机负载均衡。
* 支持memcached协议。
* 支持多队列，密码验证功能。

## 调用实例

```java
        final BlockingQueue<byte[]> queue= new BlockingFileQueue("db",1024*1024*64);
        final byte [] data= "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890".getBytes();
        //消费数据
        Thread consumer =new Thread(new Runnable() {
            @Override
            public void run() {
        
                byte[] data=null;
                long temp=0;
                try {
                    while((data = queue.take())!=null) {
                        temp = count.incrementAndGet();
                        System.out.println(temp);
                        
                    }
                } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
        
            }
        });
        consumer.start();
        
        //生产数据
        Thread producer =new Thread(new Runnable() {
             @Override
            public void run() {
                for (int i1 = 0; i1 < 1000000; i1++) {
                    try {
                        queue.put(data);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
             }
        });
        producer.start();
        
        
```
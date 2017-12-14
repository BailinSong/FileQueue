package com.blueline.util.concurrent;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public class BlockingFileQueue extends FileQueue implements BlockingDeque<byte[]> {


    protected Lock rlock=super.locker.readLock();

    public BlockingFileQueue(String path) throws Exception {
        this(path,1024*1024*16);
    }
    public BlockingFileQueue(String path, int logsize) throws Exception {
        super(path,logsize);
    }

    @Override
    public void addFirst(byte[] bytes) {
        throw new UnsupportedOperationException("addFirst Unsupported now");
    }

    @Override
    public void addLast(byte[] bytes) {
        if(!offerLast(bytes)){
            throw new IllegalStateException("Queue full");
        }

    }

    @Override
    public boolean offerFirst(byte[] bytes) {
        throw new UnsupportedOperationException("offerFirst Unsupported now");
    }

    @Override
    public boolean offerLast(byte[] bytes) {
       return offer(bytes);

    }

    @Override
    public byte[] removeFirst() {
        return poll();
    }

    @Override
    public byte[] removeLast() {
        throw new UnsupportedOperationException("removeLast Unsupported now");
    }

    @Override
    public byte[] pollFirst() {
        return poll();
    }

    @Override
    public byte[] pollLast() {
        throw new UnsupportedOperationException("pollLast Unsupported now");
    }

    @Override
    public byte[] getFirst() {
        throw new UnsupportedOperationException("getFirst Unsupported now");

    }

    @Override
    public byte[] getLast() {
        throw new UnsupportedOperationException("getLast Unsupported now");
    }

    @Override
    public byte[] peekFirst() {
        throw new UnsupportedOperationException("peekFirst Unsupported now");
    }

    @Override
    public byte[] peekLast() {
        throw new UnsupportedOperationException("peekLast Unsupported now");
    }

    @Override
    public void putFirst(byte[] bytes) throws InterruptedException {
        throw new UnsupportedOperationException("putFirst Unsupported now");
    }

    @Override
    public void putLast(byte[] bytes) throws InterruptedException {
        while(!offerLast(bytes)){
            synchronized (wlock){
                wlock.wait();
            }
        }

    }

    @Override
    public boolean offerFirst(byte[] bytes, long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException("offerFirst Unsupported now");
    }

    @Override
    public boolean offerLast(byte[] bytes, long timeout, TimeUnit unit) throws InterruptedException {
        return offer(bytes,timeout,unit);
    }

    @Override
    public byte[] takeFirst() throws InterruptedException {
        return take();
    }

    @Override
    public byte[] takeLast() throws InterruptedException {
        throw new UnsupportedOperationException("takeLast Unsupported now");
    }

    @Override
    public byte[] pollFirst(long timeout, TimeUnit unit) throws InterruptedException {
        return poll(timeout,unit);
    }

    @Override
    public byte[] pollLast(long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException("takeLast Unsupported now");
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        throw new UnsupportedOperationException("removeFirstOccurrence Unsupported now");
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        throw new UnsupportedOperationException("removeLastOccurrence Unsupported now");
    }

    @Override
    public void put(byte[] bytes) throws InterruptedException {
        while(!offerLast(bytes)){
            synchronized (wlock){
                wlock.wait();
            }
        }
    }

    @Override
    public boolean offer(byte[] bytes, long timeout, TimeUnit unit) throws InterruptedException {
        long fix=0;
        long timeOut=unit.convert(timeout,TimeUnit.MILLISECONDS);
        while (!offerLast(bytes));{

            if(timeOut-fix<=0){
                return offerLast(bytes);
            }else{
                long begin = System.currentTimeMillis();
                synchronized (wlock){
                    wlock.wait(timeOut-fix);
                }
                fix+=System.currentTimeMillis()-begin;
            }

        }
        return true;
    }

    @Override
    public byte[] take() throws InterruptedException {
        byte[] object;
        while ((object = this.poll())== null) {
            synchronized (rlock) {
                rlock.wait();
            }
        }
        return object;
    }

    @Override
    public byte[] poll(long timeout, TimeUnit unit) throws InterruptedException {
        byte[] object;
        int fix=0;
        long timeOut=unit.convert(timeout,TimeUnit.MILLISECONDS);
        while ((object = this.poll())== null);{

            if(timeOut-fix<=0){
                object = this.poll();
                if(object != null){

                }
            }else{
                long begin = System.currentTimeMillis();
                synchronized (rlock){
                    rlock.wait(timeOut-fix);
                }
                fix+=System.currentTimeMillis()-begin;
            }

        }

        return object;
    }

    @Override
    public int remainingCapacity() {
        throw new UnsupportedOperationException("remainingCapacity Unsupported now");
    }

    @Override
    public int drainTo(Collection<? super byte[]> c) {

        return drainTo(c,Integer.MAX_VALUE);
    }

    @Override
    public int drainTo(Collection<? super byte[]> c, int maxElements) {
        byte[] data;
        while(c.size()>=maxElements&&(data=super.poll())!=null){
            c.add(data);
        }
        return c.size();
    }

    @Override
    public void push(byte[] bytes) {
        throw new UnsupportedOperationException("push Unsupported now");
    }

    @Override
    public byte[] pop() {
        throw new UnsupportedOperationException("pop Unsupported now");
    }

    @Override
    public Iterator<byte[]> descendingIterator() {
        throw new UnsupportedOperationException("descendingIterator Unsupported now");
    }
    @Override
    public byte[] poll() {
        byte[] temp =super.poll();
        synchronized (wlock) {
            wlock.notify();
        }
        return temp;
    }

    @Override
    public boolean offer(byte[] bytes){
        boolean temp=super.offer(bytes);
        if(temp){
            synchronized (rlock) {
                rlock.notify();
            }
        }
        return temp;
    }
}

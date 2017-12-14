/*
 *  Copyright 2011 sunli [sunli1223@gmail.com][weibo.com@sunli1223]
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.blueline.util.concurrent;

import java.io.File;
import java.io.IOException;
import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.locks.*;

import com.blueline.util.concurrent.filequeue.FSQueue;
import com.blueline.util.concurrent.filequeue.exception.FileFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于文件系统的持久化队列
 *
 * @author sunli
 * @date 2010-8-13
 * @version $Id$
 */
public class FileQueue extends AbstractQueue<byte[]> implements Queue<byte[]>,
		java.io.Serializable {
	private static final long serialVersionUID = -5960741434564940154L;
	private FSQueue fsQueue = null;
	final Logger log = LoggerFactory.getLogger(FileQueue.class);
	protected ReentrantReadWriteLock locker=new ReentrantReadWriteLock();
	protected Lock wlock =locker.writeLock();

//	private Condition blocker = rlock.newCondition();
	private String rootPath=null;

	public FileQueue(String path) throws Exception {
		this(path, 1024 * 1024 * 300);
	}

	public void destroy(){
		this.close();
		File file=new File(rootPath);

		File [] dbFiles= file.listFiles();
		for (File f : dbFiles) {
			if (f.isFile()&&(f.getName().endsWith(".db")||f.getName().endsWith(".idb"))) {
				System.out.println(f.getName());
				;
				System.out.println("delete = " + f.delete());
			}
		}

	}






	public FileQueue(String path, int logsize) throws Exception {
		rootPath=path;
		fsQueue = new FSQueue(path, logsize);
	}

	@Override
	public Iterator<byte[]> iterator() {
		throw new UnsupportedOperationException("iterator Unsupported now");
	}

	@Override
	public int size() {
		return fsQueue.getQueuSize();
	}

	@Override
	public boolean offer(byte[] e) {
		try {
			wlock.lock();
			if(fsQueue.getQueuSize()==Integer.MAX_VALUE){
				return false;
			}else {
				fsQueue.add(e);
				return true;
			}

		} catch (Exception ex){
			throw new RuntimeException(ex);
		} finally {
			wlock.unlock();
		}
	}

	@Override
	public byte[] peek() {
		throw new UnsupportedOperationException("peek Unsupported now");
	}

	@Override
	public byte[] poll() {
		try {
			wlock.lock();
			return fsQueue.readNextAndRemove();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return null;
		} catch (FileFormatException e) {
			log.error(e.getMessage(), e);
			return null;
		} finally {
			wlock.unlock();
		}
	}

	public void close() {
		if (fsQueue != null) {
			fsQueue.close();
		}
	}
}

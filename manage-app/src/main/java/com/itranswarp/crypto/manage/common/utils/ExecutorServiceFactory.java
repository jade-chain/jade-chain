package com.itranswarp.crypto.manage.common.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池工厂<br>
 * 可创建4种线程池实例<br>
 * 1.延迟、定时周期任务线程池<br>
 * 2.单线程线程池<br>
 * 3.可缓存线程池，无上限，空闲线程优先执行<br>
 * 4.可控制线程最大并发数线程池<br>
 * 
 * @author AlanMa<br>
 * @date：2016年9月5日
 */
public class ExecutorServiceFactory {
	private static ExecutorServiceFactory executorFactory = new ExecutorServiceFactory();
	/**
	 * 定时任务线程池
	 */
	private ExecutorService executors;

	private ExecutorServiceFactory() {
	}

	/**
	 * 获取工厂实例
	 * 
	 * @return
	 */
	public static ExecutorServiceFactory getInstance() {
		return executorFactory;
	}

	/**
	 * 延迟、定时周期任务线程池
	 * 
	 * @return
	 */
	public ExecutorService createScheduledThreadPool() {
		int availableProcessors = Runtime.getRuntime().availableProcessors();
		executors = Executors.newScheduledThreadPool(availableProcessors * 10, getThreadFactory());
		return executors;
	}

	/**
	 * 单线程线程池
	 * 
	 * @return
	 */
	public ExecutorService createSingleThreadExecutor() {
		executors = Executors.newSingleThreadExecutor(getThreadFactory());
		return executors;
	}

	/**
	 * 可缓存线程池，无上限，空闲线程优先执行
	 * 
	 * @return
	 */
	public ExecutorService createCachedThreadPool() {
		executors = Executors.newCachedThreadPool(getThreadFactory());
		return executors;
	}

	/**
	 * 可控制线程最大并发数线程池
	 * 
	 * @return
	 */
	public ExecutorService createFixedThreadPool(int count) {
		executors = Executors.newFixedThreadPool(count, getThreadFactory());
		return executors;
	}

	/**
	 * 获取线程池工厂
	 * 
	 * @return
	 */
	private ThreadFactory getThreadFactory() {
		return new ThreadFactory() {
			AtomicInteger sn = new AtomicInteger();

			public Thread newThread(Runnable r) {
				SecurityManager s = System.getSecurityManager();
				ThreadGroup group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
				Thread t = new Thread(group, r);
				t.setName("Thread Mission - " + sn.incrementAndGet());
				return t;
			}
		};
	}
}
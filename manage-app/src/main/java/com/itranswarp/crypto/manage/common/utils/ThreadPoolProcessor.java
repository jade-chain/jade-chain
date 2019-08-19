package com.itranswarp.crypto.manage.common.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 线程处理类
 * 
 * @author AlanMa <br>
 * @date：2016年9月5日
 */
public class ThreadPoolProcessor {

	/**
	 * 线程池实例
	 */
	private ExecutorService executor;

	/**
	 * 延迟、定时周期任务线程池
	 */
	private volatile static ThreadPoolProcessor poolScheduled;

	/**
	 * 单线程线程池
	 */
	private volatile static ThreadPoolProcessor poolSingle;

	/**
	 * 可缓存线程池，无上限，空闲线程优先执行
	 */
	private volatile static ThreadPoolProcessor poolCached;

	/**
	 * 可控制线程最大并发数线程池
	 */
	private volatile static ThreadPoolProcessor poolFixed;

	private ThreadPoolProcessor() {
	}

	private ThreadPoolProcessor(ExecutorService executor) {
		super();
		this.executor = executor;
	}

	public static ThreadPoolProcessor getInstanceScheduled() {
		if (poolScheduled == null) {
			synchronized (ThreadPoolProcessor.class) {
				if (poolScheduled == null) {
					poolScheduled = new ThreadPoolProcessor(
							ExecutorServiceFactory.getInstance().createScheduledThreadPool());
				}
			}
		}
		return poolScheduled;
	}

	public static ThreadPoolProcessor getInstanceSingle(ExecutorService executor) {
		if (poolSingle == null) {
			synchronized (ThreadPoolProcessor.class) {
				if (poolSingle == null) {
					poolSingle = new ThreadPoolProcessor(
							ExecutorServiceFactory.getInstance().createSingleThreadExecutor());
				}
			}
		}
		return poolSingle;
	}

	public static ThreadPoolProcessor getInstanceCached() {
		if (poolCached == null) {
			synchronized (ThreadPoolProcessor.class) {
				if (poolCached == null) {
					poolCached = new ThreadPoolProcessor(ExecutorServiceFactory.getInstance().createCachedThreadPool());
				}
			}
		}
		return poolCached;
	}

	public static ThreadPoolProcessor getInstanceFixed(int threadPoolMax) {
		if (poolFixed == null) {
			synchronized (ThreadPoolProcessor.class) {
				if (poolFixed == null) {
					poolFixed = new ThreadPoolProcessor(
							ExecutorServiceFactory.getInstance().createFixedThreadPool(threadPoolMax));
				}
			}
		}
		return poolFixed;
	}

	/**
	 * 关闭线程池<br>
	 * 调用关闭线程池方法后，线程池会执行完队列中的所有任务才退出
	 */
	public void shutdown() {
		executor.shutdown();
	}

	/**
	 * 提交任务到线程池，可以接收线程返回值
	 */
	public Future<?> submit(Runnable task) {
		return executor.submit(task);
	}

	/**
	 * 提交任务到线程池，可以接收线程返回值
	 */
	public Future<?> submit(Callable<?> task) {
		return executor.submit(task);
	}

	/**
	 * 直接提交任务到线程池，无返回值
	 */
	public void execute(Runnable task) {
		executor.execute(task);
	}

	/**
	 * 定时任务(多次)
	 * 
	 * @param task
	 * @param initialDelay
	 *            延迟n秒后执行第一次
	 * @param period
	 *            第一次执行后每个n秒执行一次
	 */
	public void scheduleAtFixedRate(Runnable task, long initialDelay, long period) {
		((ScheduledExecutorService) executor).scheduleAtFixedRate(task, initialDelay, period, TimeUnit.SECONDS);
	}

	/**
	 * 定时任务(单次)
	 * 
	 * @param task
	 * @param initialDelay
	 *            延迟n秒后执行第一次
	 */
	public void schedule(Runnable command, long delay) {
		((ScheduledExecutorService) executor).schedule(command, delay, TimeUnit.SECONDS);
	}

}
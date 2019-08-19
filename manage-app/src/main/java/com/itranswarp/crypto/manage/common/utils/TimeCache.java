package com.itranswarp.crypto.manage.common.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class TimeCache<K, V> {
	private static final int DEFAULT_NUM_BUCKETS = 3;

	public static interface ExpiredCallback<K, V> {
		public void expire(K key, V val);
	}

	private ConcurrentLinkedDeque<ConcurrentHashMap<K, V>> _buckets;

	private ExpiredCallback _callback;

	private Thread _cleaner;

	public TimeCache(int expirationSecs, int numBuckets, ExpiredCallback<K, V> callback) {
		if (numBuckets < 2) {
			throw new IllegalArgumentException("numBuckets must be >= 2");
		}
		_buckets = new ConcurrentLinkedDeque<ConcurrentHashMap<K, V>>();
		for (int i = 0; i < numBuckets; i++) {
			_buckets.add(new ConcurrentHashMap<K, V>());
		}
		_callback = callback;
		final long expirationMillis = expirationSecs * 1000L;
		final long sleepTime = expirationMillis / (numBuckets - 1);
		_cleaner = new Thread(new Runnable() {
			public void run() {
				try {
					while (true) {
						Thread.sleep(sleepTime);
						rotate();
					}
				} catch (InterruptedException ex) {
				}
			}
		});
		_cleaner.setDaemon(true);
		_cleaner.start();
	}

	public TimeCache(int expirationSecs, ExpiredCallback<K, V> callback) {
		this(expirationSecs, DEFAULT_NUM_BUCKETS, callback);
	}

	public TimeCache(int expirationSecs) {
		this(expirationSecs, DEFAULT_NUM_BUCKETS);
	}

	public TimeCache(int expirationSecs, int numBuckets) {
		this(expirationSecs, numBuckets, null);
	}

	public Map<K, V> rotate() {
		Map<K, V> dead = null;
		dead = _buckets.removeLast();
		_buckets.addFirst(new ConcurrentHashMap<K, V>());
		if (_callback != null) {
			for (Entry<K, V> entry : dead.entrySet()) {
				_callback.expire(entry.getKey(), entry.getValue());
			}
		}
		return dead;
	}

	public boolean containsKey(K key) {
		for (ConcurrentHashMap<K, V> bucket : _buckets) {
			if (bucket.containsKey(key)) {
				return true;
			}
		}
		return false;
	}

	public V get(K key) {

		for (ConcurrentHashMap<K, V> bucket : _buckets) {
			if (bucket.containsKey(key)) {
				return bucket.get(key);
			}
		}
		return null;
	}

	public void put(K key, V value) {
		Iterator<ConcurrentHashMap<K, V>> it = _buckets.iterator();
		ConcurrentHashMap<K, V> bucket = it.next();
		bucket.put(key, value);
		while (it.hasNext()) {
			bucket = it.next();
			bucket.remove(key);
		}
	}

	public Object remove(K key) {
		for (ConcurrentHashMap<K, V> bucket : _buckets) {
			if (bucket.containsKey(key)) {
				return bucket.remove(key);
			}
		}
		return null;
	}

	public int size() {
		int size = 0;
		for (ConcurrentHashMap<K, V> bucket : _buckets) {
			size += bucket.size();
		}
		return size;
	}
}

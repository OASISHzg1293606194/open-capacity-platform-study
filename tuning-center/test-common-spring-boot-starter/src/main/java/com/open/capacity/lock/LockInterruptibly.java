package com.open.capacity.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockInterruptibly implements Runnable{

	private Lock lock = new ReentrantLock();
	
	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName() + " 尝试获取锁");
		try{
			lock.lockInterruptibly();
			
			try{
				System.out.println(Thread.currentThread().getName() + " 获取到锁");
				TimeUnit.MILLISECONDS.sleep(5000);
			}catch (InterruptedException e) {
				System.out.println(Thread.currentThread().getName() + " 休眠期中断了");
			}finally{
				lock.unlock();
				System.out.println(Thread.currentThread().getName() + " 释放了锁");
			}
		}catch (Exception e) {
			System.out.println(Thread.currentThread().getName() + " 获取锁期中断了");
		}
		
		
	}
	public static void main(String[] args) {
		LockInterruptibly t1 = new LockInterruptibly();
		Thread a1 = new Thread(t1) ;
		Thread a2 = new Thread(t1) ;
		
		a1.start();
		a2.start();
		
		try {
			TimeUnit.MILLISECONDS.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		a2.interrupt();
		
	}
	
}

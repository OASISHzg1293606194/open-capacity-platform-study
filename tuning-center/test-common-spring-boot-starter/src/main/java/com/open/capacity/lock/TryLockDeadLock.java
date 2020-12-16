package com.open.capacity.lock;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * tryLock避免死锁
 */
public class TryLockDeadLock implements Runnable {

	private int flag = 1;
	private static Lock LOCK1 = new ReentrantLock();
	private static Lock LOCK2 = new ReentrantLock();

	@Override
	public void run() {
		for (int i = 0; i <= 10; i++) {
			if (flag == 1) {
				try {

					if (LOCK1.tryLock()) {
						try {
							System.out.println("1:"+ Thread.currentThread().getId() + "   LOCK1 HOLD LOCK !");
							TimeUnit.MILLISECONDS.sleep(new Random().nextInt(1000));
							
							
							try {

								if (LOCK2.tryLock()) {
									try {
										System.out.println("1:"+ Thread.currentThread().getId() + "   LOCK2 HOLD LOCK !");
										TimeUnit.MILLISECONDS.sleep(new Random().nextInt(1000));
										
										
										
										
									} catch (Exception e) {

									} finally {
										LOCK2.unlock();
										TimeUnit.MILLISECONDS.sleep(new Random().nextInt(1000));
									}
								} else {
									System.out.println("1:"+ Thread.currentThread().getId() + "   LOCK2 HOLD LOCK FAILED ! retry it");
								}

							} catch (Exception e) {

							}
							
							
						} catch (Exception e) {

						} finally {
							LOCK1.unlock();
							TimeUnit.MILLISECONDS.sleep(new Random().nextInt(1000));
						}
					} else {
						System.out.println("1:"+ Thread.currentThread().getId() + "   LOCK1 HOLD LOCK FAILED ! retry it");
					}

				} catch (Exception e) {

				}

			}
			
			if (flag == 0) {
				try {

					if (LOCK2.tryLock()) {
						try {
							System.out.println("2:"+ Thread.currentThread().getId() + "   LOCK2 HOLD LOCK !");
							TimeUnit.MILLISECONDS.sleep(new Random().nextInt(1000));
							
							
							try {

								if (LOCK1.tryLock()) {
									try {
										System.out.println("2:"+Thread.currentThread().getId() + "   LOCK1 HOLD LOCK !");
										TimeUnit.MILLISECONDS.sleep(new Random().nextInt(1000));
										
										
										
										
									} catch (Exception e) {

									} finally {
										LOCK1.unlock();
										TimeUnit.MILLISECONDS.sleep(new Random().nextInt(1000));
									}
								} else {
									System.out.println("2:"+Thread.currentThread().getId() + "   LOCK1 HOLD LOCK FAILED ! retry it");
								}

							} catch (Exception e) {

							}
							
							
						} catch (Exception e) {

						} finally {
							LOCK2.unlock();
							TimeUnit.MILLISECONDS.sleep(new Random().nextInt(1000));
						}
					} else {
						System.out.println("2:"+Thread.currentThread().getId() + "   LOCK2 HOLD LOCK FAILED ! retry it");
					}

				} catch (Exception e) {

				}

			}
			
		}
	}
	public static void main(String[] args) {
		TryLockDeadLock t1 = new TryLockDeadLock();
		TryLockDeadLock t2 = new TryLockDeadLock();
		t1.flag=1 ;
		t1.flag=0;
		new Thread(t1).start();
		new Thread(t2).start();
	}
}

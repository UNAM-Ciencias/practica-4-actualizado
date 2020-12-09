package unam.ciencias.computoconcurrente;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.jupiter.api.Assertions.*;

public class BufferTest {
  final int LOOPS = 320;
  final int MIN_SLEEP_TIME = 10;
  final int MAX_SLEEP_TIME = 100;
  final int MAX_TEST_EXECUTION_TIME = 25000;

  List<Thread> threads;
  Buffer<Integer> buffer;
  ConcurrentLinkedQueue<Integer> result;
  ConcurrentLinkedQueue<Integer> expectedResult;
  Random random;
  Integer consumers;
  Integer producers;

  @BeforeEach
  void setUp() {
    this.result = new ConcurrentLinkedQueue<>();
    this.expectedResult = new ConcurrentLinkedQueue<>();
    this.threads = new ArrayList<>();
    this.random = new Random();
  }

  @Test
  void testSingleThreadFIFO() throws Exception {
    System.out.printf("MAIN THREAD: Testing sequential behavior is correct.\n");
    this.buffer = new BufferImplementation<>(LOOPS);
    this.producers = 1;
    this.consumers = 1;
    putInBuffer();
    takeFromBuffer();
    assertEquals(result.size(), LOOPS);
    assertEquals(expectedResult.size(), LOOPS);
    assertEquals(toList(expectedResult), toList(result));
  }

  @Test
  void testSingleProducerSingleConsumer() throws Exception {
    this.producers = 1;
    this.consumers = 1;
    initThreads(1, 1);
    assertEquals(result.size(), LOOPS);
    assertEquals(expectedResult.size(), LOOPS);
    assertEquals(toList(expectedResult), toList(result));
  }

  @Test
  void testSingleProducerMultipleConsumers() throws InterruptedException {
    this.producers = 1;
    this.consumers = 4;
    initThreads(1, 4);
    assertEquals(result.size(), LOOPS);
    assertEquals(sort(toList(expectedResult)), sort(toList(result)));
  }

  @Test
  void testMultipleProducersSingleConsumer() throws InterruptedException {
    this.producers = 4;
    this.consumers = 1;
    initThreads(4, 1);
    assertEquals(result.size(), LOOPS);
    assertEquals(expectedResult.size(), LOOPS);
    assertEquals(sort(toList(expectedResult)), sort(toList(result)));
  }

  @Test
  void testMultipleProducersMultipleConsumersCaseOne() throws InterruptedException {
    this.producers = 2;
    this.consumers = 4;
    initThreads(2, 4);
    assertEquals(result.size(), LOOPS);
    assertEquals(expectedResult.size(), LOOPS);
    assertEquals(sort(toList(expectedResult)), sort(toList(result)));
  }

  @Test
  void testMultipleProducersMultipleConsumersCaseTwo() throws InterruptedException {
    this.producers = 4;
    this.consumers = 2;
    initThreads(4, 2);
    assertEquals(result.size(), LOOPS);
    assertEquals(expectedResult.size(), LOOPS);
    assertEquals(sort(toList(expectedResult)), sort(toList(result)));
  }

  @Test
  void testMultipleProducersMultipleConsumersCaseThree() throws InterruptedException {
    this.producers = 40;
    this.consumers = 40;
    initThreads(40, 40);
    assertEquals(result.size(), LOOPS);
    assertEquals(expectedResult.size(), LOOPS);
    assertEquals(sort(toList(expectedResult)), sort(toList(result)));
  }

  void initThreads(int producers, int consumers) throws InterruptedException {
    System.out.printf("MAIN THREAD: Starting test case for %d producer(s), %d consumer(s).\n", producers, consumers);
    this.buffer = new BufferImplementation<>();
    for (int i = 0; i < producers; i++) {
      threads.add(new Thread(this::putInBuffer));
    }
    for (int i = 0; i < consumers; i++) {
      threads.add(new Thread(this::takeFromBuffer));
    }
    for (Thread t : threads) {
      t.start();
    }
    long currentTime = System.currentTimeMillis();
    while (result.size() < LOOPS && isThereActiveThreads() && System.currentTimeMillis() - currentTime <= MAX_TEST_EXECUTION_TIME) {
      assertTrue(buffer.elements() <= buffer.size());
      sleepRandomTime();
    }
    if (result.size() < LOOPS) {
      System.out.println("MAIN THREAD interrupting other threads, possible they didn't finish and you might have a deadlock");
      for (Thread t : threads) {
        t.interrupt();
      }
    }
    for (Thread t : threads) {
      t.join();
    }
  }

  void putInBuffer() {
    System.out.printf("Producer %s starting execution\n", Thread.currentThread().getName());
    try {
      for(int i = 0; i < LOOPS/producers; i++) {
        Integer randomInt = random.nextInt();
        buffer.put(randomInt);
        expectedResult.add(randomInt);
        int rate = consumers / producers;
        int speed = rate == 0 ? 1 : rate;
        if (i % speed == 0) {
          sleepRandomTime();
        }
      }
    } catch (InterruptedException ignored) {
      System.out.printf("Producer %s didn't finish the work\n", Thread.currentThread().getName());
    } finally {
      System.out.printf("Producer %s finishing execution\n", Thread.currentThread().getName());
    }
  }

  void takeFromBuffer() {
    System.out.printf("Consumer %s starting execution\n", Thread.currentThread().getName());
    try {
      for(int i = 0; i < LOOPS/consumers; i++) {
        result.add(buffer.take());
        int rate = producers / consumers;
        int speed = rate == 0 ? 1 : rate;
        if (i % speed == 0) {
          sleepRandomTime();
        }
      }
    } catch (InterruptedException ignored) {
      System.out.printf("Consumer %s didn't finish the work\n", Thread.currentThread().getName());
    } finally {
      System.out.printf("Consumer %s finishing execution\n", Thread.currentThread().getName());
    }
  }

  void sleepRandomTime() throws InterruptedException {
    Thread.sleep(Math.abs((MIN_SLEEP_TIME + random.nextInt()) % MAX_SLEEP_TIME));
  }

  boolean isThereActiveThreads() {
    boolean isThereActiveThreads = false;
    for (Thread t : threads) {
      isThereActiveThreads = isThereActiveThreads || t.isAlive();
    }
    return isThereActiveThreads;
  }

  List<Integer> toList(ConcurrentLinkedQueue<Integer> concurrentLinkedQueue) {
    return new ArrayList<>(concurrentLinkedQueue);
  }

  List<Integer> sort(List<Integer> list) {
    List<Integer> copy = new ArrayList<>(list);
    copy.sort(Comparator.comparingInt(a -> a));
    return copy;
  }
}

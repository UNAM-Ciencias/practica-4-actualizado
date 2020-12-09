package unam.ciencias.computoconcurrente;

public class BufferImplementation<T> implements Buffer<T> {
  public static final int DEFAULT_SIZE = 20;

  private final int size;
  private final T[] buffer;
  private int elements;

  public BufferImplementation() {
    this(DEFAULT_SIZE);
  }

  public BufferImplementation(int size) {
    this.size = size;
    this.elements = 0;
    this.buffer = (T[]) new Object[size];
  }

  public int size() {
    return this.size;
  }

  public void put(T item) throws InterruptedException {
    throw new UnsupportedOperationException();
  }

  public T take() throws InterruptedException {
    throw new UnsupportedOperationException();
  }

  public int elements() {
    return elements;
  }
}

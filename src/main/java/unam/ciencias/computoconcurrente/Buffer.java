package unam.ciencias.computoconcurrente;

public interface Buffer<T> {
  /**
   * Agrega un elemento al buffer;
   * se bloquea si esta lleno
   * @param item
   */
  void put(T item);
  /**
   * Elimina un elemento del buffer;
   * se bloquea si esta vacío.
   * @return  siguienteElemento
   */
  T take();
  /**
   * Obtiene el numero de elementos
   * del buffer.
   * @return contedoDeObjetos
   */
  int count();
}
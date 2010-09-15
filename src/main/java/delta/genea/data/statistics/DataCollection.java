package delta.genea.data.statistics;

/**
 * Interface of a data collection.
 * @author DAM
 * @param <T> Type of samples.
 */
public interface DataCollection<T>
{
  /**
   * Add a sample in this data collection.
   * @param value Value to add.
   */
  public void addSample(T value);
}

package delta.genea.data.sources;

import delta.common.framework.objects.data.ObjectsSource;

/**
 * Interface of genea objects source factories.
 * @author DAM
 */
public interface GeneaObjectsSourceFactory
{
  /**
   * Build an objects source.
   * @param id Source identifier.
   * @return A source or <code>null</code>.
   */
  ObjectsSource build(String id);
  /**
   * Indicates if this factory can handle the given identifier.
   * @param id Identifier to use.
   * @return <code>true</code> if it does, <code>false</code> otherwise.
   */
  boolean handles(String id);
}

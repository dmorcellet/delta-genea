package delta.genea.data.selections;

import java.util.Set;

import delta.common.framework.objects.data.DataObject;

/**
 * Interface of an objects selection.
 * @param <T> Type of objects in this selection.
 * @author DAM
 */
public interface Selection<T extends DataObject<T>>
{
  /**
   * Get the name of this selection.
   * @return the name of this selection.
   */
  String getName();

  /**
   * Get the objects in this selection.
   * @return A set of objects.
   */
  Set<T> getSelectedObjects();

  /**
   * Indicates if this selection contains an object that has
   * the specified primary key.
   * @param key Primary key to use.
   * @return <code>true</code> if the targeted object is in this selection,
   * <code>false</code> otherwise.
   */
  boolean hasObject(Long key);

  /**
   * Get the size of this selection.
   * @return the size of this selection.
   */
  public int getSize();
}

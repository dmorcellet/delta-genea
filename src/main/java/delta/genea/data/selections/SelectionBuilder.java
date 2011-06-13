package delta.genea.data.selections;

import delta.common.framework.objects.data.DataObject;

/**
 * Interface of a selection builder.
 * @author DAM
 * @param <T> Type of objects in this selection.
 */
public interface SelectionBuilder<T extends DataObject<T>>
{
  /**
   * Build a selection.
   * @return Newly built selection.
   */
  Selection<T> build();
}

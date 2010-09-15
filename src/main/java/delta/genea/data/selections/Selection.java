package delta.genea.data.selections;

import java.util.HashSet;
import java.util.Set;

import delta.common.framework.objects.data.DataObject;

/**
 * Base class for selections.
 * @author DAM
 * @param <T> Type of objects in this selection.
 */
public abstract class Selection<T extends DataObject>
{
  private String _name;
  private Set<T> _selectedObjects;
  private HashSet<Long> _keys;

  /**
   * Constructor.
   */
  public Selection()
  {
    _name="";
    _selectedObjects=new HashSet<T>();
    _keys=new HashSet<Long>();
  }
  
  /**
   * Get the name of this selection.
   * @return the name of this selection.
   */
  public String getName()
  {
    return _name;
  }

  /**
   * Set the name of this selection.
   * @param name Name to set.
   */
  protected void setName(String name)
  {
    _name=name;
  }

  /**
   * Get the selected objects.
   * @return the selected objects.
   */
  public Set<T> getSelectedObjects()
  {
    return _selectedObjects;
  }

  /**
   * Indicates if this selection contains an object that has
   * the specified primary key.
   * @param key Primary key to use.
   * @return <code>true</code> if the targeted object is in this selection,
   * <code>false</code> otherwise.
   */
  public boolean hasObject(long key)
  {
    return _keys.contains(Long.valueOf(key));
  }

  /**
   * Build this selection.
   */
  public abstract void build();

  /**
   * Add an object to this selection.
   * @param object Object to add.
   */
  protected void addObject(T object)
  {
    if (object!=null)
    {
      long key=object.getPrimaryKey();
      if (!hasObject(key))
      {
        _selectedObjects.add(object);
        _keys.add(Long.valueOf(key));
      }
    }
  }
}

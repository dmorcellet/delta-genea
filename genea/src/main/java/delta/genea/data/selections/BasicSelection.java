package delta.genea.data.selections;

import java.util.HashSet;
import java.util.Set;

import delta.common.framework.objects.data.DataObject;

/**
 * Base class for selections.
 * @param <T> Type of objects in this selection.
 * @author DAM
 */
public class BasicSelection<T extends DataObject<T>> implements Selection<T>
{
  private String _name;
  private Set<T> _selectedObjects;
  private HashSet<Long> _keys;

  /**
   * Constructor.
   * @param name Name of this selection.
   */
  public BasicSelection(String name)
  {
    _name=name;
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
   * Get the objects in this selection.
   * @return A set of objects.
   * @see delta.genea.data.selections.Selection#getSelectedObjects()
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
  public boolean hasObject(Long key)
  {
    return _keys.contains(key);
  }

  /**
   * Get the size of this selection.
   * @return the size of this selection.
   */
  public int getSize()
  {
    return _selectedObjects.size();
  }

  /**
   * Add an object to this selection.
   * @param object Object to add.
   */
  public void addObject(T object)
  {
    if (object!=null)
    {
      Long key=object.getPrimaryKey();
      if (!hasObject(key))
      {
        _selectedObjects.add(object);
        _keys.add(key);
      }
    }
  }
  
  /**
   * Remove an object from this selection.
   * @param object Object to remove.
   */
  public void removeObject(T object)
  {
    if (object!=null)
    {
      _selectedObjects.remove(object);
      Long key=object.getPrimaryKey();
      _keys.remove(key);
    }
  }
}

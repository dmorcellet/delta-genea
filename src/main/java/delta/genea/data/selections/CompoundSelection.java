package delta.genea.data.selections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import delta.common.framework.objects.data.DataObject;

/**
 * Selection made of the composition of other selections.
 * @param <T> Type of objects in this selection.
 * @author DAM
 */
public class CompoundSelection<T extends DataObject> implements Selection<T>
{
  /**
   * Composition operator.
   * @author DAM
   */
  public enum Operator
  {
    /**
     * AND operator. An object belongs to this selection if it belongs
     * to all the managed selections.
     */
    AND,
    /**
     * AND operator. An object belongs to this selection if it belongs
     * to at least one of the managed selections.
     */
    OR
  }

  private Operator _operator;
  private List<Selection<T>> _selections;
  private BasicSelection<T> _result;

  /**
   * Constructor.
   * @param name 
   * @param operator Composition operator.
   * @param selections Selections to use.
   */
  public CompoundSelection(String name, Operator operator, List<Selection<T>> selections)
  {
    _result=new BasicSelection<T>(name);
    _operator=operator;
    _selections=new ArrayList<Selection<T>>(selections);
    computeSelection();
  }

  /**
   * Get the name of this selection.
   * @return the name of this selection.
   */
  public String getName()
  {
    return _result.getName();
  }

  /**
   * Get the objects in this selection.
   * @return A set of objects.
   * @see delta.genea.data.selections.Selection#getSelectedObjects()
   */
  public Set<T> getSelectedObjects()
  {
    return _result.getSelectedObjects();
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
    return _result.hasObject(key);
  }

  private void computeSelection()
  {
    if (_operator==Operator.OR)
    {
      for(Selection<T> selection : _selections)
      {
        for(T object : selection.getSelectedObjects())
        {
          _result.addObject(object);
        }
      }
    }
    else if (_operator==Operator.AND)
    {
      int nbSelections=_selections.size();
      if (nbSelections>0)
      {
        SelectionSizeComparator<T> c=new SelectionSizeComparator<T>();
        Collections.sort(_selections,c);
        Selection<T> currentSelection=_selections.get(0);
        System.out.println("Current selection : "+currentSelection.getSize());
        for(int i=1;i<nbSelections;i++)
        {
          Selection<T> otherSelection=_selections.get(i);
          System.out.println("Other selection : "+otherSelection.getSize());
          currentSelection=and(currentSelection,otherSelection);
          System.out.println("New current selection : "+currentSelection.getSize());
        }
        for(T object : currentSelection.getSelectedObjects())
        {
          _result.addObject(object);
        }
      }
    }
  }

  private Selection<T> and(Selection<T> s1, Selection<T> s2)
  {
    BasicSelection<T> ret=new BasicSelection<T>("");
    Set<T> objects1=s1.getSelectedObjects();
    for(T object : objects1)
    {
      if (s2.hasObject(object.getPrimaryKey()))
      {
        ret.addObject(object);
      }
    }
    return ret;
  }

  /**
   * Get the size of this selection.
   * @return the size of this selection.
   */
  public int getSize()
  {
    return _result.getSize();
  }

  /**
   * Selection size comparator.
   * @param <T> Type of objects in the managed selections.
   * @author DAM
   */
  public static class SelectionSizeComparator<T extends DataObject> implements Comparator<Selection<T>>
  {
    /**
     * Comparison method.
     * Compares the size of two selections.
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Selection<T> o1, Selection<T> o2)
    {
      return o1.getSize()-o2.getSize();
    }
  }
}

package delta.genea.data.trees;

import java.util.LinkedList;

import delta.genea.data.Person;

/**
 * An ordered list of person that make up a filiation.
 * @author DAM
 */
public class Filiation
{
  private LinkedList<Person> _list;

  /**
   * Default constructor.
   */
  public Filiation()
  {
    _list=new LinkedList<Person>();
  }

  /**
   * Add a person as the top ancestor of the filiation.
   * @param p Person to add.
   */
  public void addAncestor(Person p)
  {
    _list.addLast(p);
  }

  /**
   * Add a person as the descendant of all others in the filiation.
   * @param p Person to add.
   */
  public void addDescendant(Person p)
  {
    _list.addFirst(p);
  }

  /**
   * Get the number of generations in this filiation.
   * @return the number of generations in this filiation.
   */
  public int getNumberOfGenerations()
  {
    return _list.size();
  }

  /**
   * Get the contents of this object as a readable string.
   * @return A human-readable string.
   */
  @Override
  public String toString()
  {
    Person first=_list.getFirst();
    Person last=_list.getLast();
    return(first+" -> "+last);
  }
}

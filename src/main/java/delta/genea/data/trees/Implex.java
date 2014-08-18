package delta.genea.data.trees;

import java.util.ArrayList;

import delta.genea.data.Person;

/**
 * Represents an implex.
 * @author DAM
 */
public class Implex
{
  private Person _rootPerson;
  private Person _maleAncestor;
  private Person _femaleAncestor;

  private ArrayList<Filiation> _filiations;

  /**
   * Constructor.
   * @param rootPerson The root person used to compute this implex.
   * @param maleAncestor The male ancestor of this implex (may be <code>null</code>).
   * @param femaleAncestor The female ancestor of this implex (may be <code>null</code>).
   */
  public Implex(Person rootPerson, Person maleAncestor, Person femaleAncestor)
  {
    _filiations=new ArrayList<Filiation>();
    _rootPerson=rootPerson;
    _maleAncestor=maleAncestor;
    _femaleAncestor=femaleAncestor;
  }

  /**
   * Get the root person for this implex.
   * @return a person.
   */
  public Person getRootPerson()
  {
    return _rootPerson;
  }

  /**
   * Get the male ancestor for this implex.
   * @return a person or <code>null</code>.
   */
  public Person getMaleAncestor()
  {
    return _maleAncestor;
  }

  /**
   * Get the female ancestor for this implex.
   * @return a person or <code>null</code>.
   */
  public Person getFemaleAncestor()
  {
    return _femaleAncestor;
  }

  /**
   * Add a filiation.
   * @param f Filiation to add.
   */
  public void addFiliation(Filiation f)
  {
    _filiations.add(f);
  }

  /**
   * Get the number of registered filiations.
   * @return A positive number.
   */
  public int getNumberOfFiliations()
  {
    return _filiations.size();
  }

  /**
   * Get a filiation.
   * @param index Index of the targeted filiation (starting at zero).
   * @return A filiation.
   */
  public Filiation getFiliation(int index)
  {
    return _filiations.get(index);
  }
}

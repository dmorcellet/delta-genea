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
   * @param rootPerson 
   * @param maleAncestor
   * @param femaleAncestor
   */
  public Implex(Person rootPerson, Person maleAncestor, Person femaleAncestor)
  {
    _filiations=new ArrayList<Filiation>();
    _rootPerson=rootPerson;
    _maleAncestor=maleAncestor;
    _femaleAncestor=femaleAncestor;
  }

  public Person getRootPerson()
  {
    return _rootPerson;
  }

  public Person getMaleAncestor()
  {
    return _maleAncestor;
  }

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

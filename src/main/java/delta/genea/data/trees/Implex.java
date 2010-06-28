package delta.genea.data.trees;

import java.util.ArrayList;

import delta.genea.data.Person;

public class Implex
{
  private Person _rootPerson;
  private Person _maleAncestor;
  private Person _femaleAncestor;

  private ArrayList<Filiation> _filiations;

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

  public void addFiliation(Filiation f)
  {
    _filiations.add(f);
  }

  public int getNumberOfFiliations()
  {
    return _filiations.size();
  }

  public Filiation getFiliation(int index)
  {
    return _filiations.get(index);
  }
}

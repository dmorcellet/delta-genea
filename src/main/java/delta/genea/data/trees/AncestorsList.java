package delta.genea.data.trees;

import java.util.ArrayList;
import java.util.List;

import delta.common.utils.collections.BinaryTreeNode;
import delta.genea.data.Person;

/**
 * Stores the ancestors of a person as a
 * list of generations.
 * @author DAM
 */
public class AncestorsList
{
  /**
   * List of sosas for each generation.
   */
  private List<List<Long>> _sosas;
  /**
   * List of persons for each generation.
   */
  private List<List<Person>> _persons;

  /**
   * Constructor.
   * @param tree Ancestors tree to use.
   */
  public AncestorsList(AncestorsTree tree)
  {
    _sosas=new ArrayList<List<Long>>();
    _persons=new ArrayList<List<Person>>();
    handleNode(tree.getRootNode(),1,0);
  }

  private void handleNode(BinaryTreeNode<Person> node, long sosa, int generation)
  {
    if (_sosas.size()<=generation)
    {
      _sosas.add(new ArrayList<Long>());
      _persons.add(new ArrayList<Person>());
    }

    _sosas.get(generation).add(Long.valueOf(sosa));
    _persons.get(generation).add(node.getData());

    BinaryTreeNode<Person> father=node.getLeftNode();
    if (father!=null)
    {
      handleNode(father, 2*sosa, generation+1);
    }
    BinaryTreeNode<Person> mother=node.getRightNode();
    if (mother!=null)
    {
      handleNode(mother, 2*sosa+1, generation+1);
    }
  }

  /**
   * Get the list of person for a given generation.
   * @param generation Targeted generation (starting at 0
   * for the root person of the ancestors tree).
   * @return A list of persons.
   * @throws IndexOutOfBoundsException if the generation is out of range
   * (<tt>index &lt; 0 || index &gt;= number of generations</tt>).
   */
  public List<Person> getPersons(int generation)
  {
    return _persons.get(generation);
  }

  /**
   * Get the list of sosas for a given generation.
   * @param generation Targeted generation (starting at 0
   * for the root person of the ancestors tree).
   * @return A list of sosas.
   * @throws IndexOutOfBoundsException if the generation is out of range
   * (<tt>index &lt; 0 || index &gt;= number of generations</tt>).
   */
  public List<Long> getSosas(int generation)
  {
    return _sosas.get(generation);
  }

  /**
   * Get the number of generations.
   * @return a positive number.
   */
  public int getNbGenerations()
  {
    return _sosas.size();
  }

  /**
   * Get the total number of persons in this data structure.
   * @return a number of persons.
   */
  public int getNbPersons()
  {
    int ret=0;
    for(int i=0;i<_sosas.size();i++)
    {
      ret+=_sosas.get(i).size();
    }
    return ret;
  }
}

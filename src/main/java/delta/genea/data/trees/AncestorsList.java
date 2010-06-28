package delta.genea.data.trees;

import java.util.ArrayList;
import java.util.List;

import delta.common.utils.collections.BinaryTreeNode;
import delta.genea.data.Person;

public class AncestorsList
{
  private List<List<Long>> _sosas;
  private List<List<Person>> _persons;

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

  public List<Person> getPersons(int generation)
  {
    return _persons.get(generation);
  }

  public List<Long> getSosas(int generation)
  {
    return _sosas.get(generation);
  }

  public int getNbGenerations()
  {
    return _sosas.size();
  }

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

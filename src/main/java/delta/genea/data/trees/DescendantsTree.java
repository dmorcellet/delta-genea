package delta.genea.data.trees;

import java.util.List;

import delta.common.utils.collections.TreeNode;
import delta.genea.data.Person;

public class DescendantsTree
{
  private Person _rootPerson;
  private TreeNode<Person> _tree;
  private int _depth;
  private boolean _sameName;
  private String _mainName;

  public DescendantsTree(Person rootPerson, int depth, boolean sameName)
  {
    _rootPerson=rootPerson;
    _depth=depth;
    _sameName=sameName;
    _tree=new TreeNode<Person>(rootPerson);
    _mainName=rootPerson.getLastName();
  }

  public TreeNode<Person> getRootNode()
  {
    return _tree;
  }

  public boolean build(boolean usePartials)
  {
    return build(_rootPerson, _tree, _depth, usePartials);
  }

  private boolean build(Person person, TreeNode<Person> node, int depth, boolean usePartials)
  {
    boolean ret=true;
    if(depth>0)
    {
      List<Person> children=null;
      if (usePartials)
      {
        children=person.getSource().loadRelationUsingPartials(Person.CHILDREN_RELATION,person.getPrimaryKey());
      }
      else
      {
        children=person.getSource().loadRelation(Person.CHILDREN_RELATION,person.getPrimaryKey());
      }
      int nb=children.size();
      TreeNode<Person> childTreeNode;
      Person child;
      for(int i=0;i<nb;i++)
      {
        child=children.get(i);
        if ((_sameName) && (!_mainName.equals(child.getLastName()))) continue;
        childTreeNode=node.addChild(child);
        build(child, childTreeNode, depth-1, usePartials);
      }
    }
    return ret;
  }

  public long getNumberOfDescendants()
  {
    return _tree.getNumberOfDescendants();
  }
}

package delta.genea.data.trees;

import java.util.List;

import delta.common.framework.objects.data.DataProxy;
import delta.common.framework.objects.data.ObjectsManager;
import delta.common.utils.collections.TreeNode;
import delta.genea.data.Person;

/**
 * Tree of descendants.
 * @author DAM
 */
public class DescendantsTree
{
  private DataProxy<Person> _rootPersonProxy;
  private TreeNode<Person> _tree;
  private int _depth;
  private boolean _sameName;
  private String _mainName;

  /**
   * Constructor.
   * @param rootPersonProxy Proxy to the root person.
   * @param depth Depth of tree (0 means no children, 1 means children at depth 1, ...).
   * @param sameName Indicates if it shall use only descendants with the same name
   * as the root person.
   */
  public DescendantsTree(DataProxy<Person> rootPersonProxy, int depth, boolean sameName)
  {
    _rootPersonProxy=rootPersonProxy;
    _depth=depth;
    _sameName=sameName;
  }

  /**
   * Get the root node.
   * @return the root node.
   */
  public TreeNode<Person> getRootNode()
  {
    return _tree;
  }

  /**
   * Build this tree.
   * @param usePartials Indicates if it shall use partials instead of
   * fully loaded persons.
   * @return <code>true</code> if this tree was successfully built, <code>false</code>
   * otherwise.
   */
  public boolean build(boolean usePartials)
  {
    Person rootPerson=_rootPersonProxy.getDataObject();
    _tree=new TreeNode<Person>(rootPerson);
    _mainName=rootPerson.getLastName();
    return build(_rootPersonProxy.getSource(), _tree, _depth, usePartials);
  }

  private boolean build(ObjectsManager<Person> source, TreeNode<Person> node, int depth, boolean usePartials)
  {
    boolean ret=true;
    if(depth>0)
    {
      Person person=node.getData();
      List<Person> children=null;
      if (usePartials)
      {
        children=source.loadRelationUsingPartials(Person.CHILDREN_RELATION,person.getPrimaryKey());
      }
      else
      {
        children=source.loadRelation(Person.CHILDREN_RELATION,person.getPrimaryKey());
      }
      int nb=children.size();
      TreeNode<Person> childTreeNode;
      Person child;
      for(int i=0;i<nb;i++)
      {
        child=children.get(i);
        if ((_sameName) && (!_mainName.equals(child.getLastName()))) continue;
        childTreeNode=node.addChild(child);
        build(source, childTreeNode, depth-1, usePartials);
      }
    }
    return ret;
  }

  /**
   * Get the number of descendants in this tree.
   * @return A number of descendants (not including the root person).
   */
  public long getNumberOfDescendants()
  {
    return _tree.getNumberOfDescendants();
  }
}

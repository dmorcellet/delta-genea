package delta.genea.hmi;

import javax.swing.tree.DefaultTreeModel;

import delta.common.utils.collections.TreeNode;
import delta.genea.data.Person;
import delta.genea.data.trees.DescendantsTree;

public class DescendantsTreeModel extends DefaultTreeModel
{
  private static final long serialVersionUID=1L;
  private transient DescendantsTree _tree;

  public DescendantsTreeModel(DescendantsTree tree)
  {
    super(null);
    _tree=tree;
  }

  @Override
  public Object getRoot()
  {
    return _tree.getRootNode();
  }

  @Override
  public Object getChild(Object parent, int index)
  {
    TreeNode<?> node=(TreeNode<?>)parent;
    return node.getChild(index);
  }

  @Override
  public int getChildCount(Object parent)
  {
    TreeNode<?> node=(TreeNode<?>)parent;
    return node.getNumberOfChildren();
  }

  @Override
  public boolean isLeaf(Object node)
  {
    return(getChildCount(node)==0);
  }

  @Override
  @SuppressWarnings("unchecked")
  public int getIndexOfChild(Object parent, Object child)
  {
    if((parent==null)||(child==null))
    {
      return -1;
    }
    TreeNode<Person> node=(TreeNode<Person>)parent;
    TreeNode<Person> childNode=(TreeNode<Person>)child;
    return node.getChildIndex(childNode);
  }
}

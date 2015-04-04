package delta.genea.hmi;

import javax.swing.tree.DefaultTreeModel;

import delta.common.utils.collections.BinaryTreeNode;
import delta.genea.data.trees.AncestorsTree;

/**
 * Tree model for an ancestors tree.
 * @author DAM
 */
public class AncestorTreeModel extends DefaultTreeModel
{
  private static final long serialVersionUID=1L;
  private transient AncestorsTree _tree;

  /**
   * Constructor.
   * @param tree Underlying ancestors tree.
   */
  public AncestorTreeModel(AncestorsTree tree)
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
    Object ret=null;
    BinaryTreeNode<?> node=(BinaryTreeNode<?>)parent;
    if(index==0)
    {
      ret=node.getLeftNode();
      if(ret==null)
      {
        ret=node.getRightNode();
      }
    }
    else
    {
      ret=node.getRightNode();
    }
    return ret;
  }

  @Override
  public int getChildCount(Object parent)
  {
    BinaryTreeNode<?> node=(BinaryTreeNode<?>)parent;
    return node.getNumberOfChildren();
  }

  @Override
  public boolean isLeaf(Object node)
  {
    return(getChildCount(node)==0);
  }

  @Override
  public int getIndexOfChild(Object parent, Object child)
  {
    if((parent==null)||(child==null))
    {
      return-1;
    }
    BinaryTreeNode<?> node=(BinaryTreeNode<?>)parent;
    if(child==node.getLeftNode())
    {
      return 0;
    }
    if(child==node.getRightNode())
    {
      return 1;
    }
    return-1;
  }
}

package delta.genea.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.common.framework.objects.data.DataProxy;
import delta.common.utils.collections.BinaryTreeNode;
import delta.common.utils.collections.TreeNode;
import delta.genea.data.Person;
import delta.genea.data.sources.GeneaDataSource;
import delta.genea.data.trees.AncestorsTree;
import delta.genea.data.trees.AncestorsTreesRegistry;
import delta.genea.data.trees.DescendantsTree;

/**
 * Tool to remove ancestors trees or descendants trees.
 * @author DAM
 */
public class MainTreeDeleter
{
  private static final Logger LOGGER=LoggerFactory.getLogger(MainTreeDeleter.class);

  private GeneaDataSource _source;

  private void removeAscendantsTree(AncestorsTree tree)
  {
    removeAscendantsTree(tree.getRootNode());
  }

  private void removeAscendantsTree(BinaryTreeNode<Person> node)
  {
    Person p=node.getData();
    if (p!=null)
    {
      Long pk=p.getPrimaryKey();
      Person p2=_source.load(Person.class,pk);
      if (p2!=null)
      {
        _source.delete(Person.class,pk);
      }
    }
    BinaryTreeNode<Person> father=node.getLeftNode();
    if (father!=null)
    {
      removeAscendantsTree(father);
    }
    BinaryTreeNode<Person> mother=node.getRightNode();
    if (mother!=null)
    {
      removeAscendantsTree(mother);
    }
  }

  private void removeDescendantsTree(DescendantsTree tree)
  {
    removeDescendantsTree(tree.getRootNode());
  }

  private void removeDescendantsTree(TreeNode<Person> node)
  {
    Person p=node.getData();
    if (p!=null)
    {
      Long pk=p.getPrimaryKey();
      Person p2=_source.load(Person.class,pk);
      if (p2!=null)
      {
        _source.delete(Person.class,pk);
      }
    }
    int nbChildren=node.getNumberOfChildren();
    for(int i=0;i<nbChildren;i++)
    {
      TreeNode<Person> child=node.getChild(i);
      if (child!=null)
      {
        removeDescendantsTree(child);
      }
    }
  }

  private void go()
  {
    try
    {
      _source=GeneaDataSource.getInstance("genea");
      // Remove ascendants
      {
        AncestorsTreesRegistry trees=_source.getAncestorsTreesRegistry();
        long[] keys={};
        for(int i=0;i<keys.length;i++)
        {
          AncestorsTree tree=trees.getTree(Long.valueOf(keys[i]),true,true);
          if (tree!=null)
          {
            removeAscendantsTree(tree);
          }
        }
      }
      // Remove descendants
      {
        long[] keys={59,60,66};
        for(int i=0;i<keys.length;i++)
        {
          DataProxy<Person> pp=_source.buildProxy(Person.class,Long.valueOf(keys[i]));
          DescendantsTree tree=new DescendantsTree(pp,1000,false);
          tree.build(false);
          removeDescendantsTree(tree);
        }
      }
      
      _source.close();
    }
    catch (Exception e)
    {
      LOGGER.warn("Error!",e);
    }
  }

  /**
   * Main method of this tool.
   * @param args Not used.
   */
  public static void main(String[] args)
  {
    new MainTreeDeleter().go();
  }
}

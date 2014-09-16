package delta.genea.tools;

import delta.common.framework.objects.data.DataProxy;
import delta.common.utils.collections.BinaryTreeNode;
import delta.common.utils.collections.TreeNode;
import delta.genea.data.Person;
import delta.genea.data.sources.GeneaDataSource;
import delta.genea.data.trees.AncestorsTree;
import delta.genea.data.trees.AncestorsTreesRegistry;
import delta.genea.data.trees.DescendantsTree;

public class MainTreeDeleter
{
  private static int nb=0;

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
      if (p2==null)
      {
        nb++;
      }
      else
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
      if (p2==null)
      {
        nb++;
      }
      else
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
          if (tree!=null)
          {
            removeDescendantsTree(tree);
          }
        }
      }
      
      _source.close();
      System.out.println(nb+" personnes non détruites (car doublons suite à implexe(s)) !");
    }
    catch (Exception e)
    {
      e.printStackTrace();
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

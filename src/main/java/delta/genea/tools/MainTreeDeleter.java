package delta.genea.tools;

import delta.common.framework.objects.data.DataProxy;
import delta.common.framework.objects.data.ObjectSource;
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

  private void removeAscendantsTree(ObjectSource<Person> personsOS, AncestorsTree tree)
  {
    removeAscendantsTree(personsOS,tree.getRootNode());
  }

  private void removeAscendantsTree(ObjectSource<Person> personsOS, BinaryTreeNode<Person> node)
  {
    Person p=node.getData();
    if (p!=null)
    {
      long pk=p.getPrimaryKey();
      Person p2=personsOS.load(pk);
      if (p2==null)
      {
        nb++;
      }
      else
      {
        personsOS.delete(pk);
      }
    }
    BinaryTreeNode<Person> father=node.getLeftNode();
    if (father!=null)
    {
      removeAscendantsTree(personsOS,father);
    }
    BinaryTreeNode<Person> mother=node.getRightNode();
    if (mother!=null)
    {
      removeAscendantsTree(personsOS,mother);
    }
  }

  private void removeDescendantsTree(ObjectSource<Person> personsOS, DescendantsTree tree)
  {
    removeDescendantsTree(personsOS,tree.getRootNode());
  }

  private void removeDescendantsTree(ObjectSource<Person> personsOS, TreeNode<Person> node)
  {
    Person p=node.getData();
    if (p!=null)
    {
      long pk=p.getPrimaryKey();
      Person p2=personsOS.load(pk);
      if (p2==null)
      {
        nb++;
      }
      else
      {
        personsOS.delete(pk);
      }
    }
    int nbChildren=node.getNumberOfChildren();
    for(int i=0;i<nbChildren;i++)
    {
      TreeNode<Person> child=node.getChild(i);
      if (child!=null)
      {
        removeDescendantsTree(personsOS,child);
      }
    }
  }

  public void go()
  {
    try
    {
      GeneaDataSource dataSource=GeneaDataSource.getInstance("genea");
      //GeneaDataSource dataSource=GeneaDataSource.getInstance("genea_michel");
      ObjectSource<Person> personsOS=dataSource.getPersonDataSource();
      // Remove ascendants
      {
        AncestorsTreesRegistry trees=dataSource.getAncestorsTreesRegistry();
        long[] keys={};
        for(int i=0;i<keys.length;i++)
        {
          AncestorsTree tree=trees.getTree(keys[i],true,true);
          if (tree!=null)
          {
            removeAscendantsTree(personsOS,tree);
          }
        }
      }
      // Remove descendants
      {
        long[] keys={59,60,66};
        for(int i=0;i<keys.length;i++)
        {
          DataProxy<Person> pp=new DataProxy<Person>(keys[i],personsOS);
          Person p=pp.getDataObject();
          if (p!=null)
          {
            DescendantsTree tree=new DescendantsTree(p,1000,false);
            tree.build(false);
            if (tree!=null)
            {
              removeDescendantsTree(personsOS,tree);
            }
          }
        }
      }
      
      dataSource.close();
      System.out.println(nb+" personnes non détruites (car doublons suite à implexe(s)) !");
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public static void main(String[] args)
  {
    new MainTreeDeleter().go();
  }
}

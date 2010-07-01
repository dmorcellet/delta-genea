package delta.genea;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

import delta.common.framework.objects.data.DataProxy;
import delta.common.framework.objects.data.ObjectSource;
import delta.common.utils.NumericTools;
import delta.common.utils.collections.BinaryTreeNode;
import delta.common.utils.collections.TreeNode;
import delta.common.utils.configuration.Configuration;
import delta.common.utils.configuration.Configurations;
import delta.common.utils.text.StringSplitter;
import delta.genea.data.Person;
import delta.genea.data.Place;
import delta.genea.data.Sex;
import delta.genea.data.Union;
import delta.genea.data.sources.GeneaDataSource;
import delta.genea.data.trees.AncestorsTree;
import delta.genea.data.trees.DescendantsTree;
import delta.genea.time.GregorianDate;

public class DumpAncestorsTree
{
  private static ObjectSource<Union> _dsUnions;
  private static final int ANCESTORS_TREE=0;
  private static final int DESCENDANTS_TREE=1;

  public static void handle(long id, int type)
  {
    try
    {
      GeneaDataSource dataSource=GeneaDataSource.getInstance();

      ObjectSource<Person> ds=dataSource.getPersonDataSource();
      DataProxy<Person> pp=new DataProxy<Person>(id,ds);
      _dsUnions=dataSource.getUnionDataSource();
      Person moi=pp.getDataObject();
      if (type==ANCESTORS_TREE)
      {
        AncestorsTree tree=new AncestorsTree(moi,1000);
        tree.build();
        dumpAncestorsTree(tree);
      }
      else if ((type==DESCENDANTS_TREE))
      {
        DescendantsTree tree=new DescendantsTree(moi,1000,false);
        tree.build(false);
        dumpDescendantsTree(tree);
      }
      //dataSource.close();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  private static void dumpAncestorsTree(AncestorsTree tree)
  {
    dumpAncestorsTree(System.out,tree.getRootNode(),1,0);
  }

  private static void dumpAncestorsTree(PrintStream out, BinaryTreeNode<Person> node, long sosa, int step)
  {
    Person p=node.getData();
    dumpPerson(out,p,Long.toString(sosa),step);
    BinaryTreeNode<Person> father=node.getLeftNode();
    if (father!=null)
    {
      dumpAncestorsTree(out,father,sosa*2,step+3);
    }
    BinaryTreeNode<Person> mother=node.getRightNode();
    if (mother!=null)
    {
      dumpAncestorsTree(out,mother,1+(sosa*2),step+3);
    }
  }

  private static void dumpDescendantsTree(DescendantsTree tree)
  {
    String name="1";
    dumpDescendantsTree(System.out,tree.getRootNode(),name,0);
  }

  private static void dumpDescendantsTree(PrintStream out, TreeNode<Person> node, String name, int step)
  {
    Person p=node.getData();
    dumpPerson(out,p,name,step);
    int nb=node.getNumberOfChildren();
    for(int i=0;i<nb;i++)
    {
      TreeNode<Person> child=node.getChild(i);
      String newName=name+'.'+Integer.toString(i+1);
      dumpDescendantsTree(out,child,newName,step+3);
    }
  }

  private static void dumpPerson(PrintStream out, Person p, String id, int step)
  {
    for(int i=0;i<step;i++) out.print(' ');
    out.print(id);
    out.print("- ");
    out.print(p.getFirstname());
    out.print(" ");
    out.println(p.getLastName());
    // Birth
    Long birthDate=p.getBirthDate();
    String birthInfos=p.getBirthInfos();
    Place birthPlace=p.getBirthPlace();
    boolean outBirth=false;
    if ((birthDate!=null) || (birthInfos!=null) || (birthPlace!=null))
    {
      for(int i=0;i<step;i++) out.print(' ');
      out.print("o ");
      if (birthDate!=null) out.print(new GregorianDate(birthDate)+" ");
      else if (birthInfos!=null) out.print(birthInfos+" ");
      if (birthPlace!=null)
      {
        out.print(birthPlace.getFullName());
      }
      outBirth=true;
    }
    // Death
    boolean outDeath=false;
    Long deathDate=p.getDeathDate();
    String deathInfos=p.getDeathInfos();
    Place deathPlace=p.getDeathPlace();
    if ((deathDate!=null) || (deathInfos!=null) || (deathPlace!=null))
    {
      if (!outBirth) for(int i=0;i<step;i++) out.print(' ');
      else out.print(", ");
      out.print("+ ");
      if (deathDate!=null) out.print(new GregorianDate(deathDate)+" ");
      else if (deathInfos!=null) out.print(deathInfos+" ");
      if (deathPlace!=null)
      {
        out.print(deathPlace.getFullName());
      }
      outDeath=true;
    }
    if (outBirth || outDeath) out.println("");

    // Unions
    List<Union> unions=_dsUnions.loadRelation(Union.UNIONS_RELATION,p.getPrimaryKey());
    Union union;
    Long unionDate;
    String unionInfos;
    Place unionPlace;
    for(Iterator<Union> it=unions.iterator();it.hasNext();)
    {
      union=it.next();
      Person partner;
      if (p.getSex()==Sex.MALE) partner=union.getWoman();
      else partner=union.getMan();
      unionDate=union.getDate();
      unionInfos=union.getInfos();
      unionPlace=union.getPlace();
      for(int i=0;i<step;i++) out.print(' ');
      out.print("x ");
      if (unionDate!=null) out.print(new GregorianDate(unionDate)+" ");
      else if (unionInfos!=null) out.print(unionInfos+" ");
      if (unionPlace!=null)
      {
        out.print(unionPlace.getFullName());
      }
      if (partner!=null)
      {
        out.println(" avec "+partner.getFirstname()+" "+partner.getLastName());
      }
    }
  }

  public static void main(String[] args)
  {
    Configuration cfg=Configurations.getConfiguration();
    // Ancestors tree
    {
      String keysStr=cfg.getStringValue("GENEA","ANCESTORS_TREES","1");
      String[] keys=StringSplitter.split(keysStr,',');
      if (keys!=null)
      {
        int key;
        for(int i=0;i<keys.length;i++)
        {
          key=NumericTools.parseInt(keys[i],-1);
          if (key!=-1)
          {
            handle(key,ANCESTORS_TREE);
          }
        }
      }
    }
    // Descedants tree
    {
      String keysStr=cfg.getStringValue("GENEA","DESCENDANTS_TREES","1");
      String[] keys=StringSplitter.split(keysStr,',');
      if (keys!=null)
      {
        int key;
        for(int i=0;i<keys.length;i++)
        {
          key=NumericTools.parseInt(keys[i],-1);
          if (key!=-1)
          {
            handle(key,DESCENDANTS_TREE);
          }
        }
      }
    }
  }
}

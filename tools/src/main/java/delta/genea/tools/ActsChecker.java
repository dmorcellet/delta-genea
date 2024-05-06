package delta.genea.tools;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import delta.common.framework.objects.data.DataProxy;
import delta.common.framework.objects.data.ObjectsSource;
import delta.common.framework.objects.sql.SqlObjectsSource;
import delta.common.utils.NumericTools;
import delta.common.utils.collections.BinaryTreeNode;
import delta.genea.data.Act;
import delta.genea.data.ActText;
import delta.genea.data.ActType;
import delta.genea.data.ActsForPerson;
import delta.genea.data.Person;
import delta.genea.data.Place;
import delta.genea.data.Union;
import delta.genea.data.sources.GeneaDataSource;
import delta.genea.data.trees.AncestorsTree;

/**
 * Finds missing acts in a ancestors tree.
 * <br>
 * Given a database name and a root person key, it iterates on this root person's
 * ancestors and builds a list of all the missing acts (i.e. birth/wedding/wedding contract/death
 * date without a matching image file.
 * @author DAM
 */
public class ActsChecker
{
  private static final Logger LOGGER=Logger.getLogger(ActsChecker.class);
  private static final String BIRTH="Naissance";
  private static final String DEATH="Décès";
  private static final String UNION="Mariage";
  private static final String WEDDING_CONTRACT="Contrat de mariage";

  private final String _dbName;
  private final Long _rootPersonKey;
  private final SimpleDateFormat _format;

  /**
   * Constructor.
   * @param dbName Database identifier.
   * @param rootPersonKey Root person primary key.
   */
  public ActsChecker(String dbName, long rootPersonKey)
  {
    _dbName=dbName;
    _rootPersonKey=Long.valueOf(rootPersonKey);
    _format=new SimpleDateFormat("dd/MM/yyyy");
  }

  /**
   * Do the job.
   */
  public void doIt()
  {
    try
    {
      LOGGER.info("Acts checker::handling ID="+_rootPersonKey);
      GeneaDataSource dataSource=GeneaDataSource.getInstance(_dbName);

      DataProxy<Person> pp=dataSource.buildProxy(Person.class,_rootPersonKey);
      Person moi=pp.getDataObject();
      AncestorsTree tree=new AncestorsTree(moi,1000);
      tree.build();
      browseAncestorsTree(System.out,tree); // NOSONAR
      dataSource.close();
      ObjectsSource source=dataSource.getObjectsSource();
      if (source instanceof SqlObjectsSource)
      {
        long nbRequests=((SqlObjectsSource)source).getNbGetRequests();
        LOGGER.info("Requests : "+nbRequests);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  private void browseAncestorsTree(PrintStream out, AncestorsTree tree)
  {
    browseAncestorsTree(out,1,tree.getRootNode(),null);
  }

  private void browseAncestorsTree(PrintStream out, int sosa, BinaryTreeNode<Person> fatherNode, BinaryTreeNode<Person> motherNode)
  {
    GeneaDataSource dataSource=GeneaDataSource.getInstance(_dbName);
    Person father=null;
    ActsForPerson fatherActs=null;
    if (fatherNode!=null)
    {
      father=fatherNode.getData();
      fatherActs=new ActsForPerson(dataSource.getObjectsSource(),father);
      fatherActs.build();
      handleAct(out,sosa,-1,BIRTH,father,null,fatherActs.getBirthAct(),father.getBirthDate(),father.getBirthPlace());
      handleAct(out,sosa,-1,DEATH,father,null,fatherActs.getDeathAct(),father.getDeathDate(),father.getDeathPlace());
    }
    Person mother=null;
    ActsForPerson motherActs=null;
    if (motherNode!=null)
    {
      mother=motherNode.getData();
      motherActs=new ActsForPerson(dataSource.getObjectsSource(),mother);
      motherActs.build();
      handleAct(out,sosa+1,-1,BIRTH,mother,null,motherActs.getBirthAct(),mother.getBirthDate(),mother.getBirthPlace());
      handleAct(out,sosa+1,-1,DEATH,mother,null,motherActs.getDeathAct(),mother.getDeathDate(),mother.getDeathPlace());
    }
    if ((father!=null) && (mother!=null))
    {
      Long motherKey=mother.getPrimaryKey();
      Union union=fatherActs.getUnionWith(motherKey);
      if (union!=null)
      {
        Act unionAct=fatherActs.getActOfUnionWith(motherKey);
        Long unionDate=union.getDate();
        handleAct(out,sosa,sosa+1,UNION,father,mother,unionAct,unionDate,union.getPlace());
      }
      Act weddingContract=fatherActs.getActOfWeddingContractWith(motherKey);
      if (weddingContract!=null)
      {
        Long wcDate=weddingContract.getDate();
        handleAct(out,sosa,sosa+1,WEDDING_CONTRACT,father,mother,weddingContract,wcDate,null);
      }
    }
    if (fatherNode!=null)
    {
      browseAncestorsTree(out,sosa*2,fatherNode.getLeftNode(),fatherNode.getRightNode());
    }
    if (motherNode!=null)
    {
      browseAncestorsTree(out,(sosa+1)*2,motherNode.getLeftNode(),motherNode.getRightNode());
    }
  }

  private void handleAct(PrintStream out, int sosa1, int sosa2, String what, Person p, Person p2, Act act, Long date, Place place)
  {
    StringBuilder sb=new StringBuilder();
    if (p!=null) sb.append(p.getFullName());
    if (p2!=null)
    {
      if (sb.length()>0)
        sb.append("/");
      sb.append(p2.getFullName());
    }
    String persons=sb.toString();
    if (date==null) return;
    if (act==null)
    {
      showSosa(out,sosa1,sosa2);
      out.print(persons+"\t");
      out.print(what+"\t");
      if (place!=null)
      {
        out.print(place.getName());
      }
      out.print("\t");
      if (place!=null)
      {
        out.print(place.getParentPlace().getName());
      }
      out.print("\t");
      out.println(_format.format(new Date(date.longValue())));
    }
    else
    {
      if (act.getNbFiles()==0)
      {
        ActText text=act.getText();
        if (text==null)
        {
          showSosa(out,sosa1,sosa2);
          out.print(persons+"\t");
          ActType type=act.getActType();
          if (type!=null) {
            what=type.getType();
          }
          out.print("manque reproduction acte de "+what+"\t");
          if (place!=null) out.print(place.getName());
          out.print("\t");
          if (place!=null) out.print(place.getParentPlace().getName());
          out.print("\t");
          out.println(_format.format(new Date(date.longValue())));
        }
      }
      else if (!act.checkFiles())
      {
        showSosa(out,sosa1,sosa2);
        out.print(persons+"\t");
        out.print("manque fichier(s) pour acte de "+what+"\t");
        if (place!=null) out.print(place.getName());
        out.print("\t");
        if (place!=null) out.print(place.getParentPlace().getName());
        out.print("\t");
        out.println(_format.format(new Date(date.longValue())));
      }
    }
  }

  private void showSosa(PrintStream out, long sosa1, long sosa2)
  {
    if (sosa1!=-1)
    {
      out.print(sosa1);
    }
    out.print("\t");
    if (sosa2!=-1)
    {
      out.print(sosa2);
    }
    out.print("\t");
  }

  /**
   * Main method for this tool.
   * @param args 
   */
  public static void main(String[] args)
  {
    if (args.length==2)
    {
      String dbName=args[0];
      long rootPersonKey=NumericTools.parseLong(args[1],-1);
      if ((dbName.length()>0) && (rootPersonKey!=-1))
      {
        new ActsChecker(dbName,rootPersonKey).doIt();
      }
    }
  }
}

package delta.genea.data.trees;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.genea.data.Couple;
import delta.genea.data.Person;
import delta.genea.data.sources.GeneaDataSource;

/**
 * Common ancestors computer.
 * @author DAM
 */
public class MainTestCommonAncestorsComputer
{
  private static final Logger LOGGER=LoggerFactory.getLogger(MainTestCommonAncestorsComputer.class);

  /**
   * Do the job.
   * @param dbName Database name.
   * @param key1 First person identifier.
   * @param key2 Second person identifier.
   */
  public static void handle(String dbName, Long key1, Long key2)
  {
    try
    {
      GeneaDataSource dataSource=GeneaDataSource.getInstance(dbName);
      Person p1=dataSource.load(Person.class,key1);
      AncestorsTree tree1=new AncestorsTree(p1,1000);
      tree1.build();
      Person p2=dataSource.load(Person.class,key2);
      AncestorsTree tree2=new AncestorsTree(p2,1000);
      tree2.build();
      Set<Couple> set=new CommonAncestorsComputer().compute(tree1,tree2);
      System.out.println("Set : "+set);
      dataSource.close();
    }
    catch (Exception e)
    {
      LOGGER.error("Error!",e);
    }
  }

  /**
   * Main method for this tool.
   * @param args Not used.
   */
  public static void main(String[] args)
  {
    handle("genea",Long.valueOf(10481),Long.valueOf(668));
  }
}

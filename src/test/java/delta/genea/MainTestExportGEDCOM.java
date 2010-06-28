package delta.genea;

import java.io.File;
import java.util.List;

import delta.genea.data.Person;
import delta.genea.data.Union;
import delta.genea.data.sources.GeneaDataSource;
import delta.genea.gedcom.ToGEDCOM;

public class MainTestExportGEDCOM
{
  public static void go()
  {
    try
    {
      GeneaDataSource dataSource=GeneaDataSource.getInstance();

      List<Person> persons=dataSource.getPersonDataSource().loadAll();
      List<Union> unions=dataSource.getUnionDataSource().loadAll();
      System.out.println("Loaded "+persons.size()+" persons.");
      System.out.println("Loaded "+unions.size()+" unions.");
      ToGEDCOM to=new ToGEDCOM();
      File gedcomFile=new File("/tmp/dada.ged");
      to.go(gedcomFile,persons,unions);
      dataSource.close();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public static void main(String[] args)
  {
    go();
  }
}

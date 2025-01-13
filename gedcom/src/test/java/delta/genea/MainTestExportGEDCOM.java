package delta.genea;

import java.io.File;

import delta.genea.data.sources.GeneaDataSource;
import delta.genea.gedcom.ToGEDCOM;

/**
 * GEDCOM export tool.
 * @author DAM
 */
public class MainTestExportGEDCOM
{
  private static final File ROOT_DIR=new File("..\\data\\xml\\genea");

  /**
   * Main method for this tool.
   * @param args Not used.
   */
  public static void main(String[] args)
  {
    String id="xml:"+ROOT_DIR.getPath();
    GeneaDataSource dataSource=GeneaDataSource.getInstance(id);
    File toFile=new File("dada.ged");
    ToGEDCOM toGedcom=new ToGEDCOM();
    toGedcom.go(dataSource,toFile);
  }
}

package delta.genea;

import java.io.File;

import delta.genea.gedcom.ToGEDCOM;

/**
 * GEDCOM export tool.
 * @author DAM
 */
public class MainTestExportGEDCOM
{
  /**
   * Main method for this tool.
   * @param args Not used.
   */
  public static void main(String[] args)
  {
    File toFile=new File("/tmp/dada.ged");
    ToGEDCOM toGedcom=new ToGEDCOM();
    toGedcom.go(toFile,"genea");
  }
}

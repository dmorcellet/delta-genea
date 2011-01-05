package delta.genea;

import java.io.File;

import delta.genea.gedcom.ToGEDCOM;

public class MainTestExportGEDCOM
{
  public static void main(String[] args)
  {
    File toFile=new File("/tmp/dada.ged");
    ToGEDCOM toGedcom=new ToGEDCOM();
    toGedcom.go(toFile,"genea");
  }
}

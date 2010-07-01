package delta.genea.webhoover.gennpdc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import delta.common.utils.NumericTools;
import delta.common.utils.files.TextFileReader;
import delta.common.utils.files.TextFileWriter;
import delta.common.utils.text.EncodingNames;

/**
 * @author DAM
 */
public class BirthActsIO
{
  public static boolean writeActs(File toFile, List<BirthAct> acts)
  {
    boolean ok=false;
    List<BirthAct> sortedList=new ArrayList<BirthAct>(acts);
    Comparator<BirthAct> c=new Comparator<BirthAct>()
    {
      public int compare(BirthAct o1, BirthAct o2)
      {
        return o1._date.compareTo(o2._date);
      }
    };
    Collections.sort(sortedList,c);
    TextFileWriter w=new TextFileWriter(toFile,EncodingNames.UTF_8);
    if (w.start())
    {
      for(BirthAct act : sortedList)
      {
        w.writeNextLine(act._lastName);
        w.writeNextLine(act._firstName);
        w.writeNextLine(act._date.getTime());
        w.writeNextLine(act._place);
        w.writeNextLine(act._father);
        w.writeNextLine(act._mother);
      }
      w.terminate();
      ok=true;
    }
    return ok;
  }

  public static List<BirthAct> readActs(File inFile)
  {
    List<BirthAct> ret=null;
    TextFileReader r=new TextFileReader(inFile,EncodingNames.UTF_8);
    if (r.start())
    {
      ret=new ArrayList<BirthAct>();
      while (true)
      {
        String lastName=r.getNextLine();
        if (lastName==null)
        {
          break;
        }
        BirthAct act=new BirthAct();
        act._lastName=lastName;
        act._firstName=r.getNextLine();
        String dateStr=r.getNextLine();
        if ((dateStr!=null) && (dateStr.length()>0))
        {
          long dateMs=NumericTools.parseLong(dateStr,-1);
          if (dateMs!=-1)
          {
            act._date=new Date(dateMs);
          }
        }
        act._place=r.getNextLine();
        act._father=r.getNextLine();
        act._mother=r.getNextLine();
        ret.add(act);
      }
      r.terminate();
    }
    return ret;
  }
}

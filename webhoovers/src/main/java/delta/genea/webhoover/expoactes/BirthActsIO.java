package delta.genea.webhoover.expoactes;

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
 * Read/write acts data.
 * @author DAM
 */
public class BirthActsIO
{
  /**
   * Write acts to a file.
   * @param toFile File to write.
   * @param acts Acts to write.
   * @return <code>true</code> if it succeeded, <code>false</code> otherwise.
   */
  public static boolean writeActs(File toFile, List<BirthAct> acts)
  {
    boolean ok=false;
    List<BirthAct> sortedList=new ArrayList<BirthAct>(acts);
    Comparator<BirthAct> c=new Comparator<BirthAct>()
    {
      public int compare(BirthAct o1, BirthAct o2)
      {
        return o1.getDate().compareTo(o2.getDate());
      }
    };
    Collections.sort(sortedList,c);
    TextFileWriter w=new TextFileWriter(toFile,EncodingNames.UTF_8);
    if (w.start())
    {
      for(BirthAct act : sortedList)
      {
        w.writeNextLine(act.getLastName());
        w.writeNextLine(act.getFirstName());
        w.writeNextLine(act.getDate().getTime());
        w.writeNextLine(act.getPlace());
        w.writeNextLine(act.getFather());
        w.writeNextLine(act.getMother());
      }
      w.terminate();
      ok=true;
    }
    return ok;
  }

  /**
   * Read acts from a file.
   * @param inFile Input file.
   * @return A list of acts or <code>null</code>.
   */
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
        act.setLastName(lastName);
        String firstName=r.getNextLine();
        act.setFirstName(firstName);
        String dateStr=r.getNextLine();
        if ((dateStr!=null) && (dateStr.length()>0))
        {
          long dateMs=NumericTools.parseLong(dateStr,-1);
          if (dateMs!=-1)
          {
            act.setDate(new Date(dateMs));
          }
        }
        String place=r.getNextLine();
        act.setPlace(place);
        String father=r.getNextLine();
        act.setFather(father);
        String mother=r.getNextLine();
        act.setMother(mother);
        ret.add(act);
      }
      r.terminate();
    }
    return ret;
  }
}

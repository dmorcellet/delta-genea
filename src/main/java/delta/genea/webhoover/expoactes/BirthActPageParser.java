package delta.genea.webhoover.expoactes;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import delta.common.utils.text.EncodingNames;
import delta.common.utils.text.TextTools;
import delta.common.utils.text.TextUtils;

/**
 * Parse a page of birth act.
 * @author DAM
 */
public class BirthActPageParser
{
  private static final String PLACE_SEED="<strong>Commune/Paroisse</strong>"; 
  private static final String BABY_SEED="<strong>Nouveau-n";
  private static final String DATE_SEED="Acte daté du : ";
  private static final String FATHER_SEED="<strong>Père</strong> : </td>";
  private static final String MOTHER_SEED="<strong>Mère</strong> : </td>";
  private final DateFormat _dateFormat;

  /**
   * Constructor.
   */
  public BirthActPageParser()
  {
    _dateFormat=new SimpleDateFormat("dd/MM/yyyy");
  }

  /**
   * Parse the given file.
   * @param f File to use.
   * @return A birth act or <code>null</code>.
   * @throws Exception If a problem occurs.
   */
  public BirthAct readFile(File f) throws Exception
  {
    BirthAct act=null;
    List<String> lines=TextUtils.readAsLines(f,EncodingNames.ISO8859_1);
    for(String line : lines)
    {
      if (line.contains("Acte de naissance/bap"))
      {
        act=new BirthAct();
      }
      if (line.contains(PLACE_SEED))
      {
        int index=line.indexOf(PLACE_SEED);
        act._place=TextTools.findBetween(line.substring(index+PLACE_SEED.length()),"<strong>","</strong></a>");
      }
      if (line.contains(BABY_SEED))
      {
        int index=line.indexOf(BABY_SEED);
        String tmp=line.substring(index+BABY_SEED.length());
        act._lastName=TextTools.findBetween(tmp,"<strong>","</strong></a>");
        act._firstName=TextTools.findBetween(tmp,"</strong></a> <strong>","</strong></td></tr>");
      }
      if (line.contains(DATE_SEED))
      {
        String dateStr=TextTools.findBetween(line,"<strong>","</strong></td>");
        try
        {
          act._date=_dateFormat.parse(dateStr);
        }
        catch(Exception e)
        {
          e.printStackTrace();
        }
      }
      if (line.contains(FATHER_SEED))
      {
        act._father=TextTools.findBetween(line,"<td class=\"fich1\">","</td></tr>");
      }
      if (line.contains(MOTHER_SEED))
      {
        act._mother=TextTools.findBetween(line,"<td class=\"fich1\">","</td></tr>");
      }
    }
    return act;
  }
}

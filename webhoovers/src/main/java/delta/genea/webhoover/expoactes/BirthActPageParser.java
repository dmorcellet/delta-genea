package delta.genea.webhoover.expoactes;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.common.utils.text.EncodingNames;
import delta.common.utils.text.TextTools;
import delta.common.utils.text.TextUtils;
import delta.downloads.DownloadException;

/**
 * Parse a page of birth act.
 * @author DAM
 */
public class BirthActPageParser
{
  private static final String STRONG_TAG="<strong>";

  private static final Logger LOGGER=LoggerFactory.getLogger(BirthActPageParser.class);

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
   * @throws DownloadException If a problem occurs.
   */
  public BirthAct readFile(File f) throws DownloadException
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
        String place=TextTools.findBetween(line.substring(index+PLACE_SEED.length()),STRONG_TAG,"</strong></a>");
        act.setPlace(place);
      }
      if (line.contains(BABY_SEED))
      {
        int index=line.indexOf(BABY_SEED);
        String tmp=line.substring(index+BABY_SEED.length());
        String lastName=TextTools.findBetween(tmp,STRONG_TAG,"</strong></a>");
        act.setLastName(lastName);
        String firstName=TextTools.findBetween(tmp,"</strong></a> <strong>","</strong></td></tr>");
        act.setFirstName(firstName);
      }
      if (line.contains(DATE_SEED))
      {
        String dateStr=TextTools.findBetween(line,STRONG_TAG,"</strong></td>");
        try
        {
          Date date=_dateFormat.parse(dateStr);
          act.setDate(date);
        }
        catch(Exception e)
        {
          LOGGER.warn("Could not parse date ["+dateStr+"]",e);
        }
      }
      if (line.contains(FATHER_SEED))
      {
        String father=TextTools.findBetween(line,"<td class=\"fich1\">","</td></tr>");
        act.setFather(father);
      }
      if (line.contains(MOTHER_SEED))
      {
        String mother=TextTools.findBetween(line,"<td class=\"fich1\">","</td></tr>");
        act.setMother(mother);
      }
    }
    return act;
  }
}

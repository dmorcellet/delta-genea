package delta.genea.webhoover.ad49.selective;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.common.utils.NumericTools;
import delta.common.utils.text.StringSplitter;
import delta.common.utils.text.TextUtils;

/**
 * Parser for a specification file for selective pages download.
 * @author DAM
 */
public class SpecFileParser
{
  private static final Logger LOGGER=LoggerFactory.getLogger(SpecFileParser.class);

  private String normalizeString(String s)
  {
    if (s.startsWith("\""))
    {
      s=s.substring(1);
    }
    if (s.endsWith("\""))
    {
      s=s.substring(0,s.length()-1);
    }
    return s;
  }
  
  private String getName(String type, String sosaOld, String sosa, String sosa2)
  {
    String baseName;
    if ("Naissance".equals(type)) baseName="an";
    else if ("Décès".equals(type)) baseName="ad";
    else if ("Mariage".equals(type)) baseName="am";
    else if ("Publication".equals(type)) baseName="ap";
    else {
      LOGGER.warn("Type acte inconnu [{}]",type);
      return null;
    }
    String number=sosa;
    if ((sosa==null) || (sosa.length()==0))
    {
      if ((sosaOld==null) || (sosaOld.length()==0))
      {
        LOGGER.warn("Sosa invalide !");
      }
      number=sosaOld;
    }
    if (sosa2.length()>0)
    {
      if ("0".equals(sosa2))
      {
        LOGGER.warn("Bad sosa2");
        return null;
      }
      number=number+"-"+sosa2;
    }
    String name=baseName+number+".jpg";
    return name;
  }

  /**
   * Parse page descriptions from a file.
   * @param f File to use.
   * @return A list of page descriptions.
   */
  public List<PageDescription> parse(File f)
  {
    List<PageDescription> ret=new ArrayList<PageDescription>();
    List<String> lines=TextUtils.readAsLines(f);
    lines.remove(0);
    String[] items;
    for(String line : lines)
    {
      items=StringSplitter.split(line,'\t');
      if ((items==null) || (items.length<11))
      {
        LOGGER.warn("Bad line [{}]",line);
      }
      String sosaOld=items[0];
      sosaOld=normalizeString(sosaOld);
      String sosaStr=items[1];
      sosaStr=normalizeString(sosaStr);
      String sosa2Str=items[2];
      sosa2Str=normalizeString(sosa2Str);
      String type=items[4];
      type=normalizeString(type);
      String placeIdStr=items[6+2];
      placeIdStr=normalizeString(placeIdStr);
      int placeId=NumericTools.parseInt(placeIdStr,-1);
      String packageIndexStr=items[7+2];
      packageIndexStr=normalizeString(packageIndexStr);
      int packageIndex=NumericTools.parseInt(packageIndexStr,-1);
      String pageStr=items[8+2];
      pageStr=normalizeString(pageStr);
      int indexOfSeparator=pageStr.indexOf('-');
      int minPageIndex;
      int maxPageIndex;
      if (indexOfSeparator!=-1)
      {
        minPageIndex=NumericTools.parseInt(pageStr.substring(0,indexOfSeparator),-1);
        maxPageIndex=NumericTools.parseInt(pageStr.substring(indexOfSeparator+1),-1);
      }
      else
      {
        minPageIndex=NumericTools.parseInt(pageStr,-1);
        maxPageIndex=minPageIndex;
      }
      if ((placeId!=-1) && (packageIndex!=-1) && (minPageIndex!=-1) && (maxPageIndex!=-1))
      {
        PageDescription p=new PageDescription();
        p._placeId=placeId;
        p._packageIndex=packageIndex;
        p._minPageIndex=minPageIndex;
        p._maxPageIndex=maxPageIndex;
        p._name=getName(type,sosaOld,sosaStr,sosa2Str);
        if (p._name!=null)
        {
          ret.add(p);
        }
        else
        {
          LOGGER.warn("Bad line [{}]",line);
        }
      }
    }
    return ret;
  }
}

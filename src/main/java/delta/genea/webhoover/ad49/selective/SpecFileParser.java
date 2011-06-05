package delta.genea.webhoover.ad49.selective;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import delta.common.utils.NumericTools;
import delta.common.utils.text.StringSplitter;
import delta.genea.webhoover.TextTools;

/**
 * @author dm
 */
public class SpecFileParser
{
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
    String baseName="a";
    if ("Naissance".equals(type)) baseName="an";
    else if ("Décès".equals(type)) baseName="ad";
    else if ("Mariage".equals(type)) baseName="am";
    else if ("Publication".equals(type)) baseName="ap";
    else {
      System.out.println("Type acte inconnu ["+type+"]");
      return null;
    }
    String number=sosa;
    if ((sosa==null) || (sosa.length()==0))
    {
      if ((sosaOld==null) || (sosaOld.length()==0))
      {
        System.out.println("Sosa invalide !");
      }
      number=sosaOld;
    }
    if (sosa2.length()>0)
    {
      if ("0".equals(sosa2))
      {
        System.out.println("Bad sosa2");
        return null;
      }
      number=number+"-"+sosa2;
    }
    String name=baseName+number+".jpg";
    return name;
  }

  public List<PageDescription> parse(File f)
  {
    List<PageDescription> ret=new ArrayList<PageDescription>();
    List<String> lines=TextTools.splitAsLines(f);
    lines.remove(0);
    String[] items;
    String placeIdStr,packageIndexStr,pageStr,sosaOld,sosaStr,sosa2Str,type;
    int placeId,packageIndex,minPageIndex,maxPageIndex;
    for(String line : lines)
    {
      items=StringSplitter.split(line,'\t');
      if ((items==null) || (items.length<11)) {
        System.out.println("Bad line ["+line+"]");
      }
      sosaOld=items[0];
      sosaOld=normalizeString(sosaOld);
      sosaStr=items[1];
      sosaStr=normalizeString(sosaStr);
      sosa2Str=items[2];
      sosa2Str=normalizeString(sosa2Str);
      type=items[4];
      type=normalizeString(type);
      placeIdStr=items[6+2];
      placeIdStr=normalizeString(placeIdStr);
      placeId=NumericTools.parseInt(placeIdStr,-1);
      packageIndexStr=items[7+2];
      packageIndexStr=normalizeString(packageIndexStr);
      packageIndex=NumericTools.parseInt(packageIndexStr,-1);
      pageStr=items[8+2];
      pageStr=normalizeString(pageStr);
      int indexOfSeparator=pageStr.indexOf('-');
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
          System.out.println("Bad line ["+line+"]");
        }
      }
      else
      {
        //System.out.println("Error with line ["+line+"]");
      }
    }
    return ret;
  }
}

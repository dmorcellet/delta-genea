package delta.genea.webhoover.ad59;

import java.io.File;

/**
 * @author DAM
 */
public class Constants
{
  public static final String PLACE_NAME="PROVIN";
  public static final File ROOT_DIR=new File("/home/dm/ad59");

  public static final String SITE_ROOT="http://www.archivesdepartementales.cg59.fr/";
  public static final String RECHERCHE_TD="?id=recherche_tables_decennales";

  public static String getTDIndexURL(String placeName)
  {
    long now=System.currentTimeMillis();
    String url=SITE_ROOT+"/?id=get_thesaurus_select&thesaurus=liste_TDEC.xml&";
    if ((placeName==null) || (placeName.length()==0))
    {
      url=url+"selected_town=";
    }
    else
    {
      url=url+"commune="+placeName;
    }
    url=url+"&time="+now;
    return url;
  }

  public static String getURL(String placeName, int from, int to)
  {
    StringBuilder sb=new StringBuilder(SITE_ROOT);
    sb.append("?go_thesaurus=&thesaurus_opened=&node_start=&start=&open_thes=&close_thes=&select_thes=&unselect_thes=&label_geogname=Commune&form_search_town=");
    sb.append(placeName);
    sb.append("&form_search_geogname=");
    sb.append(placeName);
    sb.append("&form_search_date=").append(from).append('-').append(to);
    sb.append("&label_unitdate=P%C3%A9riode&label_unitdate_pref=entre&label_unitdate2=et&form_search_unitdate=");
    sb.append(from);
    sb.append("&form_search_unitdate2=");
    sb.append(to);
    sb.append("&label_typeacte=Type+d%27acte&form_search_typeacte=&btn_valid=Rechercher&action=search&id=recherche_tables_decennales");
    return sb.toString();
  }

  public static String getPageURL(String packageId, int pageNumber)
  {
    StringBuilder sb=new StringBuilder(SITE_ROOT);
    sb.append("?id=viewer&doc=datas/ir/Tables%20decennales/tables_decennales.xml&page_ref=");
    sb.append(packageId);
    sb.append("&lot_num=1&img_num=");
    sb.append(pageNumber);
    return sb.toString();
  }

  public static String getSizeURL(String fileName)
  {
    StringBuilder sb=new StringBuilder(SITE_ROOT);
    sb.append("viewer.cgi?filename=");
    sb.append(fileName);
    sb.append("&action=getsize&wname=main_w&hname=main_h");
    return sb.toString();
  }

  public static String getImageURL(String fileName, int x, int y, int width, int height)
  {
    StringBuilder sb=new StringBuilder(SITE_ROOT);
    sb.append("viewer.cgi?filename=");
    sb.append(fileName);
    sb.append("&x="+x+"&y="+y+"&w=").append(width).append("&h=").append(height);
    sb.append("&zoom=100000&brightness=0&contrast=0&rotate=0&negative=0");
    return sb.toString();
  }

  public static String getImageName(int pageNumber)
  {
    String fileName=String.valueOf(pageNumber);
    if (pageNumber<10) fileName="0"+fileName;
    if (pageNumber<100) fileName="0"+fileName;
    if (pageNumber<1000) fileName="0"+fileName;
    fileName="page_"+fileName+".jpg";
    return fileName;
  }

  public static File getImageFile(File rootDir, int pageNumber)
  {
    String fileName=getImageName(pageNumber);
    File out=new File(rootDir,fileName);
    return out;
  }
}

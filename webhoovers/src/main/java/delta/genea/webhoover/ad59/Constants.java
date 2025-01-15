package delta.genea.webhoover.ad59;

import java.io.File;

/**
 * Constants for webhoover 59.
 * @author DAM
 */
public class Constants
{
  /**
   * Place name.
   */
  static final String PLACE_NAME="PROVIN";
  /**
   * Use tables or acts packages.
   */
  static final boolean TD=true;

  /**
   * Site root.
   */
  private static final String SITE_ROOT="http://www.archivesdepartementales.cg59.fr";

  /**
   * Another site root.
   */
  private static final String SITE_ROOT2="http://www.archivesdepartementales.lenord.fr";

  /**
   * Get the URL of the index page for a place.
   * @param placeName Place to use.
   * @param td Use tables or acts packages.
   * @return An URL.
   */
  public static String getIndexURL(String placeName, boolean td)
  {
    long now=System.currentTimeMillis();
    String url=SITE_ROOT2+"/?id=get_thesaurus_select&thesaurus=";
    if (td)
    {
      url=url+"liste_TDEC.xml&";
    }
    else
    {
      url=url+"liste_MI.xml&";
    }
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

  //&label_unittitle=Cote+microfilm&form_search_unittitle=&tri=0
  /**
   * Get the URL for the packages index.
   * @param placeName Place name.
   * @param from Start year.
   * @param to End year.
   * @param td Tables/acts flag.
   * @return An URL.
   */
  public static String getURL(String placeName, int from, int to, boolean td)
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
    sb.append("&label_typeacte=Type+d%27acte&form_search_typeacte=&btn_valid=Rechercher&action=search&id=");
    if (td)
    {
      sb.append("recherche_tables_decennales");
    }
    else
    {
      sb.append("recherche_etat_civil");
    }
    return sb.toString();
  }

  /**
   * Get the URL for an act page.
   * @param packageId Package identifier.
   * @param pageNumber Page number (starting at one).
   * @param td Tables/acts flag.
   * @return An URL.
   */
  public static String getPageURL(String packageId, int pageNumber, boolean td)
  {
    StringBuilder sb=new StringBuilder(SITE_ROOT);

    // MI
    // http://www.archivesdepartementales.cg59.fr/?id=viewer&doc=accounts%2Fmnesys_ad59%2Fdatas%2Fir%2FEtat%20civil%2FFRAD059_MI_A_Z_2011_07_01%2Exml&page_ref=292115&lot_num=1&img_num=1    
    
    sb.append("?id=viewer&doc=");
    if (td)
    {
      sb.append("datas/ir/Tables%20decennales/tables_decennales.xml");
    }
    else
    {
      sb.append("?id=viewer&doc=accounts%2Fmnesys_ad59%2Fdatas%2Fir%2FEtat%20civil%2FFRAD059_MI_A_Z_2011_07_01%2Exml");
    }
    sb.append("&page_ref=");
    sb.append(packageId);
    sb.append("&lot_num=1&img_num=");
    sb.append(pageNumber);
    return sb.toString();
  }

  /**
   * Get the URL for the size of an act page.
   * @param fileName Filename.
   * @return An URL.
   */
  public static String getSizeURL(String fileName)
  {
    StringBuilder sb=new StringBuilder(SITE_ROOT);
    sb.append("viewer.cgi?filename=");
    sb.append(fileName);
    sb.append("&action=getsize&wname=main_w&hname=main_h");
    return sb.toString();
  }

  /**
   * Get the URL for a fragment of act page.
   * @param fileName Filename.
   * @param x Start X.
   * @param y Start Y.
   * @param width Image width.
   * @param height Image height.
   * @return An URL.
   */
  public static String getImageURL(String fileName, int x, int y, int width, int height)
  {
    StringBuilder sb=new StringBuilder(SITE_ROOT);
    sb.append("viewer.cgi?filename=");
    sb.append(fileName);
    sb.append("&x="+x+"&y="+y+"&w=").append(width).append("&h=").append(height);
    sb.append("&zoom=100000&brightness=0&contrast=0&rotate=0&negative=0");
    return sb.toString();
  }

  /**
   * Get the filename to use for a page.
   * @param pageNumber Page number (starting at one).
   * @return A filename.
   */
  public static String getImageName(int pageNumber)
  {
    String fileName=String.valueOf(pageNumber);
    if (pageNumber<10) fileName="0"+fileName;
    if (pageNumber<100) fileName="0"+fileName;
    if (pageNumber<1000) fileName="0"+fileName;
    fileName="page_"+fileName+".jpg";
    return fileName;
  }

  /**
   * Get the file to use for a page.
   * @param rootDir Root directory for this file.
   * @param pageNumber Page number (starting at one).
   * @return A file.
   */
  public static File getImageFile(File rootDir, int pageNumber)
  {
    String fileName=getImageName(pageNumber);
    File out=new File(rootDir,fileName);
    return out;
  }
}

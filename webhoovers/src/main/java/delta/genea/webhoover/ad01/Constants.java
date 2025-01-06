package delta.genea.webhoover.ad01;

import java.io.File;

/**
 * Constants for the AD01 web hoover.
 * @author DAM
 */
class Constants
{
  static final String ROOT_SITE="http://www.archives-numerisees.ain.fr";
  // public static String mainPage=ROOT_SITE+"/etatcivil.html?btCom=146&btComMere=&BT-Com=Cordon+%5B8+lots%5D";
  // public static String mainPage=ROOT_SITE+"/etatcivil.html?btCom=238&btComMere=&BT-Com=Izieu+%5B83+lots%5D";
  // public static String mainPage=ROOT_SITE+"/etatcivil.html?btCom=73&btComMere=&BT-Com=Br%E9gnier+%5B34+lots%5D";
  // public static String mainPage=ROOT_SITE+"/etatcivil.html?btCom=433&btComMere=&BT-Com=Saint-Beno%EEt+%5B219+lots%5D";
  static final String MAIN_PAGE="http://www.archives-numerisees.ain.fr/matmilitaire.html?DATE_EXAC=&RECH_NOM=MORCELLET&RECH_PRENOM=&btVal=Rechercher";
  // public static String mainPage=ROOT_SITE+"/etatcivil.html?btCom=74&btComMere=&BT-Com=Br%E9gnier-Cordon+%5B106+lots%5D";

  // public static String mainPage=ROOT_SITE+"/etatcivil.html?btCom=142&btComMere=&BT-Com=Conzieu+%5B74+lots%5D";
  // public static String mainPage=ROOT_SITE+"/etatcivil.html?btCom=7&btComMere=&BT-Com=Ambl%E9on+%5B217+lots%5D";
  // public static String mainPage=ROOT_SITE+"/etatcivil.html?btCom=135&btComMere=&BT-Com=Colomieu+%5B144+lots%5D";
  // public static String mainPage=ROOT_SITE+"/etatcivil.html?btCom=16&btComMere=&BT-Com=Arbignieu+%5B102+lots%5D";
  // public static String mainPage=ROOT_SITE+"/etatcivil.html?btCom=220&btComMere=&BT-Com=Grosl%E9e+%5B109+lots%5D";
  // public static String mainPage=ROOT_SITE+"/etatcivil.html?btCom=435&btComMere=&BT-Com=Saint-Bois+%5B170+lots%5D";
  // public static String mainPage=ROOT_SITE+"/etatcivil.html?btCom=405&btComMere=&BT-Com=Pugieu+%5B140+lots%5D";
  // public static String mainPage=ROOT_SITE+"/etatcivil.html?btCom=56&btComMere=&BT-Com=Billieu+%5B30+lots%5D";
  // public static String mainPage=ROOT_SITE+"/etatcivil.html?btCom=292&btComMere=&BT-Com=Magnieu+%5B181+lots%5D";
  // public static String mainPage=ROOT_SITE+"/etatcivil.html?btCom=456&btComMere=&BT-Com=Saint-Germain-les-Paroisses+%5B223+lots%5D";
  // public static String mainPage=ROOT_SITE+"/etatcivil.html?btCom=135&btComMere=&BT-Com=Colomieu";
  // public static String mainPage=ROOT_SITE+"/etatcivil.html?btCom=435&btComMere=&BT-Com=Saint-Bois";
  // static final File ROOT_DIR=new File("d:\\archivesDeLain\\st-benoit");
  static final File ROOT_DIR=new File("/home/dm/data/genealogie/archives en ligne/ad01");
  // static final File ROOT_DIR=new File("/home/dm/tmp/download/conzieu/recensements");
  static final File MAIN_PAGE_FILE=new File(ROOT_DIR,"mainPage.html");

  static final String VISU_PAGE=ROOT_SITE+"/visu2/visui.php?";
}

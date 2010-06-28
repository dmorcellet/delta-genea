package delta.genea.webhoover.ad01;

import java.io.File;

public class Constants
{
  static final String ROOT_SITE="http://ad01.vtech.fr";
  //static final String MAIN_PAGE=ROOT_SITE+"/etatcivil.html?btCom=433&btComMere=&BT-Com=Saint-Beno%EEt+%5B219+lots%5D";
  static final String MAIN_PAGE=ROOT_SITE+"/recensement.html?btCom=142&btComMere=&BT-Com=Conzieu+%5B14+lots%5D";

	//static final File ROOT_DIR=new File("d:\\archivesDeLain\\st-benoit");
  static final File ROOT_DIR=new File("/home/dm/tmp/download/conzieu/recensements");
  static final File MAIN_PAGE_FILE=new File(ROOT_DIR,"mainPage.html");

  static final String VISU_PAGE=ROOT_SITE+"/visu2/visui.php?";
}

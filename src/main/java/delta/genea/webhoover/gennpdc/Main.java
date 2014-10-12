package delta.genea.webhoover.gennpdc;

import java.io.File;
import java.util.List;

import delta.genea.webhoover.expoactes.BirthAct;
import delta.genea.webhoover.expoactes.BirthActsIO;
import delta.genea.webhoover.expoactes.BirthPagesManager;
import delta.genea.webhoover.expoactes.ExpoActeSession;

/**
 * @author DAM
 */
public class Main
{
  private static final File OUTPUT_DIR=new File("/home/dm/tmp/gennpdc");
  public static final File ACTS_FILE=new File("/home/dm/bb.txt");

  public Main()
  {
  }

  private void doIt() throws Exception
  {
    String placeName="Billy-Berclau";
    downloadActs(placeName);
    //parseBirthActs();
  }

  private void downloadActs(String placeName) throws Exception
  {
    ExpoActeSession session=new ExpoActeSession("gennpdc","http://www.gennpdc.net",OUTPUT_DIR);
    BirthPagesManager birthManager=new BirthPagesManager(session);
    List<BirthAct> acts=birthManager.downloadBirthActs(placeName);
    BirthActsIO.writeActs(ACTS_FILE,acts);
    session.terminate();
    System.out.println(session.getActsCounter());
  }

  /*
  private void parseBirthActs()
  {
    List<BirthAct> acts=new ArrayList<BirthAct>();
    FilesFinder finder=new FilesFinder();
    FileFilter htmlFiles=new ExtensionPredicate("html");
    List<File> files=finder.find(FilesFinder.ABSOLUTE_MODE,OUTPUT_DIR,htmlFiles,true);
    for(File f : files)
    {
      BirthAct act=readFile(f);
      if (act!=null)
      {
        acts.add(act);
      }
    }
    BirthActsIO.writeActs(ACTS_FILE,acts);
  }
  */

  public static void main(String[] args) throws Exception
  {
    new Main().doIt();
  }
}

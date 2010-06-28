package delta.genea.webhoover.ad59;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainCheckActsDir
{
  public static void handleChildDir(File childDir)
  {
    String[] childs=childDir.list();
    int nbchilds=childs.length;
    List<String> names=new ArrayList<String>(nbchilds);
    for(int i=0;i<nbchilds;i++) names.add(childs[i]);
    Collections.sort(names);
    for(int i=0;i<nbchilds;i++)
    {
      String expected=Constants.getImageName(i+1);
      if (!names.get(i).equals(expected))
      {
        System.err.println("Directory: "+childDir+". Expected : "+expected);
      }
    }
  }

  public static void doIt(File dir)
  {
    File[] childs=dir.listFiles();
    File child;
    for(int i=0;i<childs.length;i++)
    {
      child=childs[i];
      if (!child.isDirectory())
      {
        System.err.println("Not a directory : "+child);
      }
      else
      {
        System.out.println("Handling : "+child);
        handleChildDir(child);
      }
    }
  }
  public static void main(String[] args)
  {
    File dir=new File(Constants.ROOT_DIR,Constants.PLACE_NAME);
    doIt(dir);
  }
}

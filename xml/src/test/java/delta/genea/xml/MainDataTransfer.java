package delta.genea.xml;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

import delta.common.framework.objects.data.Identifiable;
import delta.common.framework.objects.data.ObjectsManager;
import delta.common.framework.objects.xml.ObjectXmlDriver;
import delta.common.utils.files.FilesDeleter;
import delta.common.utils.files.filter.ExtensionPredicate;
import delta.genea.data.Act;
import delta.genea.data.ActText;
import delta.genea.data.ActType;
import delta.genea.data.Cousinage;
import delta.genea.data.Person;
import delta.genea.data.Picture;
import delta.genea.data.Place;
import delta.genea.data.Union;
import delta.genea.data.sources.GeneaDataSource;

/**
 * Tool to transfer data from a SQL source to a XML target.
 * @author DAM
 */
public class MainDataTransfer
{
  private static final String SOURCE_DATABASE="genea";
  private static final File ROOT_DIR=new File("..\\..\\delta-genea-webapp\\genea_xml");

  private void doIt()
  {
    GeneaDataSource source=GeneaDataSource.getInstance(SOURCE_DATABASE);
    // Remove the contents of the XML source directory
    // Otherwise, deleted elements will still be there after transfer!
    FileFilter filter=new ExtensionPredicate("xml");
    FilesDeleter deleter=new FilesDeleter(ROOT_DIR,filter,false);
    deleter.doIt();
    // Perform transfer
    GeneaDataSource target=GeneaDataSource.getInstance("xml:"+ROOT_DIR.getPath());
    handleClass(source,target,Place.class);
    handleClass(source,target,ActType.class);
    handleClass(source,target,Act.class);
    handleClass(source,target,Union.class);
    handleClass(source,target,ActText.class);
    handleClass(source,target,Picture.class);
    handleClass(source,target,Cousinage.class);
    handleClass(source,target,Person.class);
  }

  private <E extends Identifiable<Long>> void handleClass(GeneaDataSource source, GeneaDataSource target, Class<E> c)
  {
    ObjectsManager<E> mgr=source.getManager(c);
    List<E> objects=mgr.loadAll();
    ObjectsManager<E> targetMgr=target.getManager(c);
    for(E object : objects)
    {
      targetMgr.create(object);
    }
    ObjectXmlDriver<E> driver=(ObjectXmlDriver<E>)targetMgr.getDriver();
    driver.saveAll(targetMgr.getCache().getAll());
  }

  /**
   * Main method for this tool.
   * @param args Not used.
   */
  public static void main(String[] args)
  {
    new MainDataTransfer().doIt();
  }
}

package delta.genea.data.trees;

import java.util.HashMap;

import delta.genea.data.Person;
import delta.genea.data.sources.GeneaDataSource;

/**
 * Registry for all ancestors trees of a data source.
 * @author DAM
 */
public class AncestorsTreesRegistry
{
  private GeneaDataSource _dataSource;
  /**
   * Storage map for known ancestors trees.
   */
  private HashMap<Long, AncestorsTree> _knownTrees;

  /**
   * Private constructor.
   * @param dataSource Managed data source.
   */
  public AncestorsTreesRegistry(GeneaDataSource dataSource)
  {
    _dataSource=dataSource;
    _knownTrees=new HashMap<Long, AncestorsTree>();
  }

  /**
   * Get an ancestors tree (build it if necessary).
   * @param key Root person key.
   * @return A tree or <code>null</code>.
   */
  public AncestorsTree getTree(Long key)
  {
    return getTree(key,true,false);
  }

  /**
   * Get an ancestors tree (build it if necessary).
   * @param key Root person key.
   * @param builtItIfNeeded Built the tree it it is not loaded yet.
   * @param tolerantMode Tree building mode.
   * @return A tree or <code>null</code>.
   */
  public AncestorsTree getTree(Long key, boolean builtItIfNeeded, boolean tolerantMode)
  {
    AncestorsTree ret=null;
    if (key!=null)
    {
      ret=_knownTrees.get(key);
      if ((ret==null) && (builtItIfNeeded))
      {
        Person root=_dataSource.load(Person.class,key);
        if (root!=null)
        {
          AncestorsTree at=new AncestorsTree(root,AncestorsTree.MAX_DEPTH);
          boolean ok=at.build(tolerantMode);
          if (ok)
          {
            ret=at;
            _knownTrees.put(key,ret);
          }
        }
      }
    }
    return ret;
  }
}

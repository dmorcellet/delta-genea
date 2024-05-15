package delta.genea.data.sources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import delta.common.framework.objects.data.DataProxy;
import delta.common.utils.collections.TreeNode;
import delta.genea.data.Place;

/**
 * Registry for places.
 * @author DAM
 */
public final class PlacesRegistry
{
  private static final Logger LOGGER=Logger.getLogger(PlacesRegistry.class);

  private String _dbName;
  private TreeNode<Place> _placesTree;
  private static HashMap<String,PlacesRegistry> _registries=new HashMap<String,PlacesRegistry>();

  /**
   * Get the places registry for a given database.
   * @param dbName Name of the targeted database.
   * @return A places registry.
   */
  public static PlacesRegistry getInstance(String dbName)
  {
    synchronized (_registries)
    {
      PlacesRegistry instance=_registries.get(dbName);
      if (instance==null)
      {
        instance=new PlacesRegistry(dbName);
        _registries.put(dbName,instance);
      }
      return instance;
    }
  }

  /**
   * Private constructor.
   * @param dbName Name of the managed database.
   */
  private PlacesRegistry(String dbName)
  {
    _dbName=dbName;
    _placesTree=load();
  }

  /**
   * Get the root node of the places tree.
   * @return the root node of the places tree.
   */
  public TreeNode<Place> getPlacesTree()
  {
    return _placesTree;
  }

  /**
   * Load all places in the managed database and put them
   * in a places tree.
   * @return the places tree.
   */
  private TreeNode<Place> load()
  {
    List<Place> places=GeneaDataSource.getInstance(_dbName).loadAll(Place.class);
    int minLevel=100;
    int maxLevel=-1;
    int nb=places.size();
    for(int i=0;i<nb;i++)
    {
      int level=places.get(i).getLevel().getValue();
      if (level<minLevel) minLevel=level;
      if (level>maxLevel) maxLevel=level;
    }
    TreeNode<Place> tree=new TreeNode<Place>(null);
    if (minLevel==maxLevel)
    {
      for(int i=0;i<nb;i++) tree.addChild(places.get(i));
    }
    else
    {
      HashMap<Long,TreeNode<Place>> map=new HashMap<Long,TreeNode<Place>>();
      List<TreeNode<Place>> nodes=new ArrayList<TreeNode<Place>>(nb);
      Place place;
      TreeNode<Place> currentNode;
      for(int i=0;i<nb;i++)
      {
        place=places.get(i);
        currentNode=tree.addChild(place);
        if (place.getLevel().getValue()>minLevel)
        {
          map.put(place.getPrimaryKey(),currentNode);
        }
        nodes.add(currentNode);

      }
      List<TreeNode<Place>> nonAttachedNodes=new ArrayList<TreeNode<Place>>();
      DataProxy<Place> proxy;
      TreeNode<Place> parentNode;
      for(int i=0;i<nb;i++)
      {
        place=places.get(i);
        proxy=place.getParentPlaceProxy();
        if (proxy!=null)
        {
          currentNode=nodes.get(i);
          parentNode=map.get(proxy.getPrimaryKey());
          if (parentNode!=null)
          {
            currentNode.changeSuperNode(parentNode);
          }
          else
          {
            nonAttachedNodes.add(currentNode);
          }
        }
      }
      if (!nonAttachedNodes.isEmpty())
      {
        LOGGER.error("There are some non attached place nodes !");
        for(int i=0;i<nonAttachedNodes.size();i++)
        {
          LOGGER.error("\t"+nonAttachedNodes.get(i).getData());
        }
      }
    }
    return tree;
  }
}

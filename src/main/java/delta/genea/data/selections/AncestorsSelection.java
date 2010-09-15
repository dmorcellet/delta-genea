package delta.genea.data.selections;

import delta.common.utils.collections.BinaryTreeNode;
import delta.genea.data.Person;
import delta.genea.data.sources.GeneaDataSource;
import delta.genea.data.trees.AncestorsTree;
import delta.genea.data.trees.AncestorsTreesRegistry;

/**
 * Selection made of ancestors of a given root person.
 * @author DAM
 */
public class AncestorsSelection extends Selection<Person>
{
  private AncestorsTree _tree;

  /**
   * Constructor.
   * @param dbName Source database.
   * @param personKey Root person key.
   * @param depth Depth of ancestors tree.
   */
  public AncestorsSelection(String dbName, long personKey, int depth)
  {
    GeneaDataSource dataSource=GeneaDataSource.getInstance(dbName);
    AncestorsTreesRegistry registry=dataSource.getAncestorsTreesRegistry();
    _tree=registry.getTree(personKey,true,false);
    Person rootPerson=_tree.getRootNode().getData();
    setName("Ancêtres de "+rootPerson.getFullName());
  }
  
  public void build()
  {
    if (_tree!=null)
    {
      handleNode(_tree.getRootNode(),1,0);
    }
  }

  private void handleNode(BinaryTreeNode<Person> node, long sosa, int generation)
  {
    Person p=node.getData();
    if (p!=null)
    {
      addObject(p);
    }
    BinaryTreeNode<Person> father=node.getLeftNode();
    if (father!=null)
    {
      handleNode(father,sosa*2,generation+1);
    }
    BinaryTreeNode<Person> mother=node.getRightNode();
    if (mother!=null)
    {
      handleNode(mother,1+(sosa*2),generation+1);
    }
  }
}

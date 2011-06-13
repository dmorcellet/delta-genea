package delta.genea.data.selections;

import delta.common.utils.collections.BinaryTreeNode;
import delta.genea.data.Person;
import delta.genea.data.sources.GeneaDataSource;
import delta.genea.data.trees.AncestorsTree;
import delta.genea.data.trees.AncestorsTreesRegistry;

/**
 * Builds selections made of ancestors of a given root person.
 * @author DAM
 */
public class AncestorsSelectionBuilder implements SelectionBuilder<Person>
{
  private String _dbName;
  private Long _personKey;

  /**
   * Constructor.
   * @param dbName Source database.
   * @param personKey Root person key.
   */
  public AncestorsSelectionBuilder(String dbName, Long personKey)
  {
    _dbName=dbName;
    _personKey=personKey;
  }
  
  public Selection<Person> build()
  {
    BasicSelection<Person> ret=null;
    GeneaDataSource dataSource=GeneaDataSource.getInstance(_dbName);
    AncestorsTreesRegistry registry=dataSource.getAncestorsTreesRegistry();
    AncestorsTree tree=registry.getTree(_personKey,true,false);
    Person rootPerson=tree.getRootNode().getData();
    if (tree!=null)
    {
      String selectionName="Ancêtres de "+rootPerson.getFullName();
      ret=new BasicSelection<Person>(selectionName);
      handleNode(ret,tree.getRootNode(),1,0);
    }
    return ret;
  }

  private void handleNode(BasicSelection<Person> selection, BinaryTreeNode<Person> node, long sosa, int generation)
  {
    Person p=node.getData();
    if (p!=null)
    {
      selection.addObject(p);
    }
    BinaryTreeNode<Person> father=node.getLeftNode();
    if (father!=null)
    {
      handleNode(selection,father,sosa*2,generation+1);
    }
    BinaryTreeNode<Person> mother=node.getRightNode();
    if (mother!=null)
    {
      handleNode(selection,mother,1+(sosa*2),generation+1);
    }
  }
}

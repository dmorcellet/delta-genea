package delta.genea.hmi;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import delta.genea.data.trees.DescendantsTree;

/**
 * Panel with a descendants tree.
 * @author DAM
 */
public class DescendantTreePanel extends JPanel
{
  private transient DescendantsTree _tree;
  private DescendantsTreeModel _model;
  private JTree _jtree;

  /**
   * Constructor.
   * @param tree Underlying descendants tree.
   */
  public DescendantTreePanel(DescendantsTree tree)
  {
    _tree=tree;
    setLayout(new BorderLayout());
    _model=new DescendantsTreeModel(_tree);
    _jtree=new JTree(_model);
    for(int i=0;i<_jtree.getRowCount();i++)
    {
      _jtree.expandRow(i);
    }
    add(new JScrollPane(_jtree), BorderLayout.CENTER);
  }
}

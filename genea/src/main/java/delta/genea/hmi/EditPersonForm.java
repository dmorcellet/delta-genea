package delta.genea.hmi;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;

import delta.genea.data.Person;
import delta.genea.data.Sex;

/**
 * Edition form for a person.
 * @author DAM
 */
public class EditPersonForm extends JDialog
{
  private transient Person _person;
  private JTextField _surnameTextField;
  private JTextField _firstNameTextField;
  private JComboBox<Sex> _sexCombo;
  private JTextArea _commentsTextField;
  private JButton _okButton;
  private JButton _cancelButton;

  /**
   * Constructor.
   * @param mother Parent frame.
   * @param modal Modality.
   */
  public EditPersonForm(JFrame mother, boolean modal)
  {
    super(mother, modal);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);

    JPanel main=(JPanel)getContentPane();
    main.setLayout(new BorderLayout());
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    setTitle("Edit person");

    JPanel dataPanel=new JPanel(new GridBagLayout());
    Insets stdInsets=new Insets(5,5,5,5);
    int n=0;
    // Surname
    JLabel surnameLabel=new JLabel("Surname");
    dataPanel.add(surnameLabel,new GridBagConstraints(0,n,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,stdInsets,5,0));
    _surnameTextField=new JTextField(10);
    dataPanel.add(_surnameTextField,new GridBagConstraints(1,n,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,stdInsets,5,0));
    n++;
    // First name
    JLabel firstNameLabel=new JLabel("First name");
    dataPanel.add(firstNameLabel,new GridBagConstraints(0,n,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,stdInsets,5,0));
    _firstNameTextField=new JTextField(10);
    dataPanel.add(_firstNameTextField,new GridBagConstraints(1,n,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,stdInsets,5,0));
    n++;
    // Sex
    JLabel sexLabel=new JLabel("Sex");
    dataPanel.add(sexLabel,new GridBagConstraints(0,n,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,stdInsets,5,0));
    List<Sex> listOfSex=Sex.getListOfSex();
    _sexCombo=new JComboBox<Sex>(listOfSex.toArray(new Sex[listOfSex.size()]));
    dataPanel.add(_sexCombo,new GridBagConstraints(1,n,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,stdInsets,5,0));
    n++;
    // Comments
    JLabel commentsLabel=new JLabel("Comments");
    dataPanel.add(commentsLabel,new GridBagConstraints(0,n,1,1,0.0,0.0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,stdInsets,5,0));
    _commentsTextField=new JTextArea(5,40);
    _commentsTextField.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    dataPanel.add(_commentsTextField,new GridBagConstraints(1,n,1,1,1.0,1.0,GridBagConstraints.WEST,GridBagConstraints.BOTH,stdInsets,5,0));
    main.add(dataPanel,BorderLayout.CENTER);
    JPanel commandsPanel=new JPanel(new FlowLayout(FlowLayout.RIGHT));
    n++;
    // OK button
    _okButton=new JButton("OK");
    _okButton.addActionListener(new ActionListener()
    { public void actionPerformed(ActionEvent e) { okButtonCallback(); } });
    // Cancel button
    _cancelButton=new JButton("Cancel");
    _cancelButton.addActionListener(new java.awt.event.ActionListener()
    { public void actionPerformed(ActionEvent e) { cancelButtonCallback(); } });

    commandsPanel.add(_okButton);
    commandsPanel.add(_cancelButton);
    main.add(commandsPanel,BorderLayout.SOUTH);
    pack();
  }

  @Override
  protected void processWindowEvent(WindowEvent e)
  {
    super.processWindowEvent(e);
    if(e.getID()==WindowEvent.WINDOW_CLOSING)
    {
      cancelButtonCallback();
    }
  }

  void okButtonCallback()
  {
    _person.setLastName(_surnameTextField.getText());
    _person.setFirstname(_firstNameTextField.getText());
    _person.setSex((Sex)_sexCombo.getSelectedItem());
    _person.setComments(_commentsTextField.getText());
    System.out.println(_person);
    System.out.println(_person.getSex());
    System.out.println(_person.getComments());
    dispose();
  }

  void cancelButtonCallback()
  {
    dispose();
  }

  /**
   * Fill this form with a person.
   * @param data Person to set.
   */
  public void fillWithData(Person data)
  {
    _person=data;
    _surnameTextField.setText(_person.getLastName());
    _firstNameTextField.setText(_person.getFirstname());
    _sexCombo.setSelectedItem(_person.getSex());
    _commentsTextField.setText(_person.getComments());
  }
}

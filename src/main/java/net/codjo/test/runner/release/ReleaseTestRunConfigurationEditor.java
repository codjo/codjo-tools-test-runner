/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.test.runner.release;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jetbrains.annotations.NotNull;
/**
 * Editeur graphique permettant de parametrer un {@link ReleaseTestRunConfiguration}.
 */
class ReleaseTestRunConfigurationEditor extends SettingsEditor<ReleaseTestRunConfiguration> {
    private Project project;
    private MyEditor currentEditor;


    ReleaseTestRunConfigurationEditor(Project project) {
        this.project = project;
    }


    @Override
    protected void resetEditorFrom(ReleaseTestRunConfiguration settings) {
        resetTextFieldFrom(currentEditor.getFileName(), settings.getReleaseTestFileName());
        resetTextFieldFrom(currentEditor.getVmParameters(), settings.getVMParameters());
        currentEditor.getModules().setSelectedItem(settings.getTargetModule());
    }


    @Override
    protected void applyEditorTo(ReleaseTestRunConfiguration configuration) {
        configuration.setReleaseTestFileName(currentEditor.getFileName().getText());
        configuration.setVMParameters(currentEditor.getVmParameters().getText());
        configuration.setTargetModule(((Module)currentEditor.getModules().getSelectedItem()));
    }


    @NotNull
    @Override
    protected JComponent createEditor() {
        currentEditor = new MyEditor();
        currentEditor.getModules()
              .setModel(new DefaultComboBoxModel(ModuleManager.getInstance(project).getSortedModules()));
        currentEditor.getModules().setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list,
                                                          Object value,
                                                          int index,
                                                          boolean isSelected,
                                                          boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    Module module = (Module)value;
                    setText(module.getName());
                }
                return this;
            }
        });
        currentEditor.getChooseFileButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                chooseReleaseTestFile();
            }
        });
        return currentEditor;
    }


    @Override
    protected void disposeEditor() {
        currentEditor = null;
    }


    private void resetTextFieldFrom(JTextField textField, String value) {
        textField.setText((value != null ? value : ""));
    }


    private void chooseReleaseTestFile() {
        FileChooserDialog fileChooser =
              FileChooserFactory.getInstance().createFileChooser(new FileChooserDescriptor(
                    true,
                    false,
                    false,
                    false,
                    false,
                    false), project);
        VirtualFile[] virtualFiles = fileChooser.choose(null, project);
        if (virtualFiles.length != 0) {
            currentEditor.getFileName().setText(virtualFiles[0].getPath());
        }
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("Test Editor");
        MyEditor contentPane = new MyEditor();
        contentPane.setBorder(BorderFactory.createEtchedBorder());
        frame.setContentPane(contentPane);
        frame.setVisible(true);
        frame.setSize(600, 300);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                System.exit(0);
            }


            @Override
            public void windowClosed(WindowEvent evt) {
                System.exit(0);
            }
        });
    }


    private static class MyEditor extends JPanel {
        private JTextField fileName = new JTextField();
        private JComboBox modules = new JComboBox(new String[]{"Client", "Test"});
        private JButton chooseFileButton = new JButton("...");
        private JTextField vmParameters = new JTextField();


        MyEditor() {
            super(new GridBagLayout());
            GridBagConstraints constraints;

            add(new JLabel(IconLoader.getIcon("icon-big.png", ReleaseTestRunConfiguration.class),
                           JLabel.LEFT),
                newConstraints(0, 0));

            add(newLabelFor(fileName, 'f', "Release Test filename (absolute path):"),
                newConstraints(0, 1));
            add(fileName, newConstraints(0, 2));
            constraints = newConstraints(1, 2);
            constraints.fill = GridBagConstraints.NONE;
            constraints.insets = new Insets(5, 0, 0, 5);
            chooseFileButton.setMargin(new Insets(0, 2, 0, 2));
            add(chooseFileButton, constraints);
            fileName.requestFocus();

            add(newLabelFor(vmParameters, 'V', "VM parameters"), newConstraints(0, 3, 10));
            add(vmParameters, newConstraints(0, 4));

            add(newLabelFor(modules, 'o', "Use classpath and JDK of module:"), newConstraints(0, 5, 10));
            constraints = newConstraints(0, 6);
            constraints.weighty = 1.0;
            constraints.weightx = 1.0;
            constraints.anchor = GridBagConstraints.NORTH;
            add(modules, constraints);
        }


        public JTextField getFileName() {
            return fileName;
        }


        public JTextField getVmParameters() {
            return vmParameters;
        }


        public JComboBox getModules() {
            return modules;
        }


        public JButton getChooseFileButton() {
            return chooseFileButton;
        }


        private JLabel newLabelFor(JComponent comp, char mnemonic, String label) {
            JLabel jLabel = new JLabel(label);
            jLabel.setLabelFor(comp);
            jLabel.setDisplayedMnemonic(mnemonic);
            return jLabel;
        }


        private GridBagConstraints newConstraints(int xx, int yy) {
            return newConstraints(xx, yy, 5);
        }


        private GridBagConstraints newConstraints(int xx, int yy, int top) {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = xx;
            constraints.gridy = yy;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.insets = new Insets(top, 10, 0, 5);
            return constraints;
        }
    }
}

/*
 * Editor.java
 *
 * Created on August 4, 2000, 9:06 AM
 */
package org.netbeans.editor.example;

import java.util.*;

import java.net.URL;
import java.io.*;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.undo.UndoManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;
import org.netbeans.editor.*;
import org.netbeans.editor.ext.*;


/**
 *
 * @author  Petr Nejedly
 * @version 0.2
 */
public class Editor extends javax.swing.JFrame {

    private static final File distributionDirectory;
    
    static {
	URL url = Editor.class.getProtectionDomain().getCodeSource().getLocation();
	String protocol = url.getProtocol();
	File file = new File(url.getFile());
	if (!file.isDirectory()) file = file.getParentFile();
	distributionDirectory = file;
    }
    
    /** Document property holding String name of associated file */
    private static final String FILE = "file";
    /** Document property holding Boolean if document was created or opened */
    private static final String CREATED = "created";
    /** Document property holding Boolean modified information */
    private static final String MODIFIED = "modified";
        
    private ResourceBundle settings = ResourceBundle.getBundle( "settings" );

    private JFileChooser fileChooser;
    
    private int fileCounter = -1;
    Map com2text = new HashMap();
    
    private Impl impl = new Impl("org.netbeans.editor.Bundle");
    
    private class Impl extends FileView implements WindowListener,
                                    ActionListener, LocaleSupport.Localizer {
                                        
        private ResourceBundle bundle;
	
	public Impl( String bundleName ) {
	    bundle = ResourceBundle.getBundle( bundleName );
	}

        // FileView implementation
	public String getName( File f ) { return null; }
	public String getDescription( File f ) { return null; }
	public String getTypeDescription( File f ) { return null; }
	public Boolean isTraversable( File f ) { return null; }
        public Icon getIcon( File f ) {
            if( f.isDirectory() ) return null;
            KitInfo ki = KitInfo.getKitInfoForFile( f );
            return ki == null ? null : ki.getIcon();
        }
        
        // Localizer
        public String getString( String key ) {
	    return bundle.getString( key );
	}
        
        // Mostly no-op WindowListener for close
        public void windowActivated(WindowEvent evt) {}
        public void windowClosed(WindowEvent evt) {}
        public void windowDeactivated(WindowEvent evt) {}
        public void windowDeiconified(WindowEvent evt) {}
        public void windowIconified(WindowEvent evt) {}
        public void windowOpened(WindowEvent evt) {}
        public void windowClosing(java.awt.event.WindowEvent evt) {
            doExit();
        }

        // ActionListener for menu items
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            Object src = evt.getSource();

            if (src == openItem) {
                fileChooser.setMultiSelectionEnabled( true );
                int returnVal = fileChooser.showOpenDialog(Editor.this);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    File[] files = fileChooser.getSelectedFiles();
                    for( int i=0; i<files.length; i++ ) openFile( files[i] );
                }
                fileChooser.setMultiSelectionEnabled( false );
            } else if (src == closeItem) {
                Component editor = tabPane.getSelectedComponent();
                if( checkClose( editor ) ) {
                    tabPane.remove( editor );
                    com2text.remove( editor );
                }
            } else if (src == saveItem) {
                saveFile( tabPane.getSelectedComponent() );
            } else if (src == saveAsItem) {
                saveAs( tabPane.getSelectedComponent() );
            } else if (src == saveAllItem) {
                int index = tabPane.getSelectedIndex();
                for( int i = 0; i < tabPane.getComponentCount(); i++ ) {
                    saveFile( tabPane.getComponentAt( i ) );
                }
                tabPane.setSelectedIndex( index );
            } else if (src == exitItem) {
                doExit();
            } else if (src instanceof JMenuItem) {
                Object ki = ((JMenuItem)src).getClientProperty("kitInfo");
                
                if (ki instanceof KitInfo) {
                    createNewFile( (KitInfo)ki );
                }
            }
        }        
    }
    
    public Editor() {
        super( "NetBeans Editor" );
	LocaleSupport.addLocalizer(impl);
	
        // Feed our kits with their default Settings
        Settings.addInitializer(new BaseSettingsInitializer(), Settings.CORE_LEVEL);
        Settings.addInitializer(new ExtSettingsInitializer(), Settings.CORE_LEVEL);
        Settings.reset();
        
        // Create visual hierarchy
        initComponents ();
        openItem.addActionListener(impl);
        closeItem.addActionListener(impl);
        saveItem.addActionListener(impl);
        saveAsItem.addActionListener(impl);
        saveAllItem.addActionListener(impl);
        exitItem.addActionListener(impl);
        addWindowListener(impl);
        
        // Prepare the editor kits and such things
        readSettings();
                
        // Do the actual layout
        setLocation( 150, 150 );
        pack ();
    }
    
    public Dimension getPreferredSize() {
        Dimension size = new Dimension( 640,480 );
        return size;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        tabPane = new javax.swing.JTabbedPane();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        newMenu = new javax.swing.JMenu();
        openItem = new javax.swing.JMenuItem();
        closeItem = new javax.swing.JMenuItem();
        sep1 = new javax.swing.JSeparator();
        saveItem = new javax.swing.JMenuItem();
        saveAsItem = new javax.swing.JMenuItem();
        saveAllItem = new javax.swing.JMenuItem();
        sep2 = new javax.swing.JSeparator();
        exitItem = new javax.swing.JMenuItem();

        getContentPane().setLayout(new java.awt.GridLayout(1, 1));

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        getContentPane().add(tabPane);

        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.setText("File");
        newMenu.setMnemonic(KeyEvent.VK_N);
        newMenu.setText("New...");
        fileMenu.add(newMenu);
        openItem.setMnemonic(KeyEvent.VK_O);
        openItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openItem.setText("Open File...");
        fileMenu.add(openItem);
        closeItem.setMnemonic(KeyEvent.VK_C);
        closeItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.CTRL_MASK));
        closeItem.setText("Close");
        fileMenu.add(closeItem);
        fileMenu.add(sep1);
        saveItem.setMnemonic(KeyEvent.VK_S);
        saveItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveItem.setText("Save");
        fileMenu.add(saveItem);
        saveAsItem.setMnemonic(KeyEvent.VK_A);
        saveAsItem.setText("Save As...");
        fileMenu.add(saveAsItem);
        saveAllItem.setMnemonic(KeyEvent.VK_L);
        saveAllItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        saveAllItem.setText("Save All");
        fileMenu.add(saveAllItem);
        fileMenu.add(sep2);
        exitItem.setMnemonic(KeyEvent.VK_E);
        exitItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        exitItem.setText("Exit");
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

    }//GEN-END:initComponents
                
    private boolean saveFile( Component comp, File file, boolean checkOverwrite ) {
        if( comp == null ) return false;
        tabPane.setSelectedComponent( comp );
        JTextComponent edit = (JTextComponent)com2text.get( comp );
        Document doc = edit.getDocument();
        
        if( checkOverwrite && file.exists() ) {
            tabPane.setSelectedComponent( comp );
            int choice = JOptionPane.showOptionDialog(this,
            "File " + file.getName() + " already exists, overwrite?",
            "File exists",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,     // don't use a custom Icon
            null,     // use standard button titles
            null      // no default selection
            );
            if( choice != 0 ) return false;
        }
        
        try {
            edit.write( new FileWriter( file ) );
        } catch( IOException exc ) {
            JOptionPane.showMessageDialog( this, "Can't write to file '" +
            file.getName() + "'.", "Error", JOptionPane.ERROR_MESSAGE );
            return false;
        }
        
        
        doc.putProperty( MODIFIED, new Boolean( false ) );
        doc.putProperty( CREATED, new Boolean( false ) );
        doc.putProperty( FILE, file );
        doc.addDocumentListener( new MarkingDocumentListener( comp ) );
        
        int index = tabPane.indexOfComponent( comp );
        tabPane.setTitleAt( index, file.getName() );
        
        return true;
    }
    
    private boolean saveFile( Component comp ) {
        if( comp == null ) return false;
        JTextComponent edit = (JTextComponent)com2text.get( comp );
        Document doc = edit.getDocument();
        File file = (File)doc.getProperty( FILE );
        boolean created = ((Boolean)doc.getProperty( CREATED )).booleanValue();
        
        return saveFile( comp, file, created );
    }
    
    private boolean saveAs( Component comp ) {
        if( comp == null ) return false;
        JTextComponent edit = (JTextComponent)com2text.get( comp );
        File file = (File)edit.getDocument().getProperty( FILE );
        
        fileChooser.setCurrentDirectory( file.getParentFile() );
        fileChooser.setSelectedFile( file );
        KitInfo fileInfo = KitInfo.getKitInfoOrDefault( file );
        
        if( fileInfo != null ) fileChooser.setFileFilter( fileInfo );
        
        // show the dialog, test the result
        if( fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
            return saveFile( comp, fileChooser.getSelectedFile(), true );
        else
            return false; // Cancel was pressed - not saved
    }

    private void openFile( File file ) {
        KitInfo info = KitInfo.getKitInfoOrDefault( file );
        
        final JEditorPane pane = new JEditorPane( info.getType(), "" );
        try {
            pane.read( new FileInputStream( file ), file.getCanonicalPath() );
        } catch( IOException exc ) {
            JOptionPane.showMessageDialog( this, "Can't read from file '" +
            file.getName() + "'.", "Error", JOptionPane.ERROR_MESSAGE );
            return;
        }
        addEditorPane( pane, info.getIcon(), file, false );
    }

    private void doExit() {
        boolean exit = true;
        while( tabPane.getComponentCount() > 0 ) {
            Component editor = tabPane.getComponentAt( 0 );
            if( checkClose( editor ) ) {
                tabPane.remove( editor );
                com2text.remove( editor );
            } else {
                System.err.println("keeping");
                exit = false;
                break;
            }
        }
        if( exit ) System.exit (0);
    }
    
    private boolean checkClose( Component comp ) {
        if( comp == null ) return false;
        JTextComponent edit = (JTextComponent)com2text.get( comp );
        Document doc = edit.getDocument();
        
        Object mod = doc.getProperty( MODIFIED );
        if( mod == null || ! ((Boolean)mod).booleanValue()) return true;
        
        tabPane.setSelectedComponent( comp );
        File file = (File)doc.getProperty( FILE );
        
        for( ;; ) {
            int choice = JOptionPane.showOptionDialog(this,
            "File " + file.getName() + " was modified, save it?",
            "File modified",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,     // don't use a custom Icon
            new String[] { "Save", "Save As...", "Discard", "Cancel" },     // use standard button titles
            "Cancel"      //default selection
            );
            
            switch( choice ) {
                case JOptionPane.CLOSED_OPTION:
                case 4:
                    return false; // Cancel or Esc pressed
                case 1:
                    if( !saveAs( comp ) ) continue;  // Ask for fileName, then save
                    return true;
                case 0:
                    if( !saveFile( comp ) ) continue; // else fall through
                case 2:
                    return true;  // Discard changes, close window
            }
            return false;
        }
    }
    
    private void addEditorPane( JEditorPane pane, Icon icon, File file, boolean created ) {
        final JComponent c = (pane.getUI() instanceof BaseTextUI) ?
        Utilities.getEditorUI(pane).getExtComponent() : new JScrollPane( pane );
        Document doc = pane.getDocument();
        
        doc.addDocumentListener( new MarkingDocumentListener( c ) );
        doc.putProperty( FILE, file );
        doc.putProperty( CREATED, new Boolean( created ) );
        
        UndoManager um = new UndoManager();
        doc.addUndoableEditListener( um );
        doc.putProperty( BaseDocument.UNDO_MANAGER_PROP, um );
        
        com2text.put( c, pane );
        tabPane.addTab( file.getName(), icon, c, file.getAbsolutePath() );
        tabPane.setSelectedComponent( c );
        pane.requestFocus();
    }
    
    
    private void createNewFile( KitInfo info ) {
        final String fileName = ((++fileCounter == 0 ) ?
            "unnamed" :
            ("unnamed" + fileCounter)) + info.getDefaultExtension();
        final File file = new File( fileName ).getAbsoluteFile();

        final JEditorPane pane = new JEditorPane( info.getType(), "" );
        URL template = info.getTemplate();
        if( template != null ) {
            try {
                pane.read( template.openStream(), file.getCanonicalPath() );
            } catch( IOException e ) {
                JOptionPane.showMessageDialog( this, "Can't read template", "Error", JOptionPane.ERROR_MESSAGE );
            }
        }
        addEditorPane( pane, info.getIcon(), file, true );
    }
    
    
    
    public static File getDistributionDirectory() {
       return distributionDirectory;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main (String args[]) {
      if (!getDistributionDirectory().canRead()) {
         System.err.println("Fatal error while startup - can read from distribution directory.");
         System.exit(0);
      }
      
        Editor editor = new Editor ();
        editor.show ();
        
        for( int i = 0; i < args.length; i++ ) {
            String fileName = args[i];
            editor.openFile( new File( fileName ) );
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSeparator sep2;
    private javax.swing.JSeparator sep1;
    private javax.swing.JMenu newMenu;
    private javax.swing.JMenuItem saveAllItem;
    private javax.swing.JMenuItem closeItem;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem exitItem;
    private javax.swing.JTabbedPane tabPane;
    private javax.swing.JMenuItem saveAsItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem openItem;
    private javax.swing.JMenuItem saveItem;
    // End of variables declaration//GEN-END:variables

    
    private void readSettings() throws MissingResourceException {
        File currentPath = new File( System.getProperty( "user.dir" ) ).getAbsoluteFile();
        fileChooser = new JFileChooser( currentPath );
        
        fileChooser.setFileView(impl);
        
        String kits = settings.getString( "InstalledEditors" );
        String defaultKit = settings.getString( "DefaultEditor" );
        
        StringTokenizer st = new StringTokenizer( kits, "," );
        while( st.hasMoreTokens() ) {
            String kitName = st.nextToken();
            // At the first, we have to read ALL info about kit
            String contentType = settings.getString( kitName + "_ContentType" );
            String extList = settings.getString( kitName + "_ExtensionList" );
            String menuTitle = settings.getString( kitName + "_NewMenuTitle" );
            char menuMnemonic = settings.getString( kitName + "_NewMenuMnemonic" ).charAt( 0 );
            String templateURL = settings.getString( kitName + "_Template" );
            String iconName = settings.getString( kitName + "_Icon" );
            String filterTitle = settings.getString( kitName + "_FileFilterTitle" );
            String kit = settings.getString( kitName + "_KitClass" );

            // At the second, we surely need an instance of kitClass
            Class kitClass;
            try {
                kitClass = Class.forName( kit );
            } catch( ClassNotFoundException exc ) { // we really need it
                throw new MissingResourceException( "Missing class", kit, "KitClass" ); 
            }

            // At the third, it is nice to have icon although we could live without one
            Icon icon = null;
            ClassLoader loader = kitClass.getClassLoader();
            if( loader == null ) loader = ClassLoader.getSystemClassLoader();
            URL resource = loader.getResource( iconName );
            if( resource == null ) resource = ClassLoader.getSystemResource( iconName );
            if( resource != null ) icon = new ImageIcon( resource );

            // At the fourth, try to get URL for template
            URL template = loader.getResource( templateURL );
            if( resource == null ) template = ClassLoader.getSystemResource( templateURL );

            // Finally, convert the list of extensions to, ehm, List :-)
            List l = new ArrayList( 5 );
            StringTokenizer extST = new StringTokenizer( extList, "," );
            while( extST.hasMoreTokens() ) l.add( extST.nextToken() );            
            
            // Actually create the KitInfo from provided informations
            KitInfo ki = new KitInfo( contentType, l, template, icon, filterTitle, kitClass, loader, defaultKit.equals( kitName ) );

            // Make the MenuItem for it
            JMenuItem item = new JMenuItem( menuTitle, icon );
            item.setMnemonic( menuMnemonic );
            item.putClientProperty( "kitInfo", ki );
            item.addActionListener( impl );
	    newMenu.add( item );

            // Register a FileFilter for given type of file
            fileChooser.addChoosableFileFilter( ki );
        }
        
        // Finally, add fileFilter that would recognize files of all kits

        fileChooser.addChoosableFileFilter( new FileFilter() {
            public String getDescription() {
                return "All recognized files";
            }
        
            public boolean accept( File f ) {
                return f.isDirectory() || KitInfo.getKitInfoForFile( f ) != null;
            }
        });
        
        if( KitInfo.getDefault() == null ) throw new MissingResourceException( "Missing default kit definition", defaultKit, "DefaultEditor" ); 
    }
            
    private static final class KitInfo extends FileFilter{
        
        private static List kits = new ArrayList();
        private static KitInfo defaultKitInfo;
        
        public static List getKitList() {
            return new ArrayList( kits );
        }
        
        public static KitInfo getDefault() {
            return defaultKitInfo;
        }
        
        public static KitInfo getKitInfoOrDefault( File f ) {
            KitInfo ki = getKitInfoForFile( f );
            return ki == null ? defaultKitInfo : ki;
        }
        
        public static KitInfo getKitInfoForFile( File f ) {
            for( int i = 0; i < kits.size(); i++ ) {
                if( ((KitInfo)kits.get(i)).accept( f ) )
                    return (KitInfo)kits.get(i);
            }
            return null;
        }

        private String type;
        private String[] extensions;
        private URL template;
        private Icon icon;
        private Class kitClass;
        private String description;
        
        public KitInfo( String type, List exts, URL template, Icon icon, String description, Class kitClass, ClassLoader loader, boolean isDefault ) {
            // Fill in the structure
            this.type = type;
            this.extensions = (String[])exts.toArray( new String[0] );
            this.template = template;
            this.icon = icon;
            this.description = description;
            this.kitClass = kitClass;
            
            // Register us
            JEditorPane.registerEditorKitForContentType( type, kitClass.getName(), loader );
            kits.add( this );
            if( isDefault ) defaultKitInfo = this;
        }
        

        
        public String getType() {
            return type;
        }
        
        public String getDefaultExtension() {
            return extensions[0];
        }

        public URL getTemplate() {
            return template;
        }
        
        public Icon getIcon() {
            return icon;
        }

        public Class getKitClass() {
            return kitClass;
        }
                        
        public String getDescription() {
            return description;
        }
        
        public boolean accept( File f ) {
            if( f.isDirectory() ) return true;
            String fileName = f.getName();
            for( int i=0; i<extensions.length; i++ ) {
                if( fileName.endsWith( extensions[i] ) ) return true;
            }
            return false;
        }
    }   
    
    /** Listener listening for document changes on opened documents. There is
     * initially one instance per opened document, but this listener is
     * one-fire only - as soon as it gets fired, markes changes and removes
     * itself from document. On save, new Listener is hooked again.
     */
    private class MarkingDocumentListener implements DocumentListener {
        private Component comp;
        
        public MarkingDocumentListener( Component comp ) {
            this.comp = comp;
        }
        
        private void markChanged( DocumentEvent evt ) {
            Document doc = evt.getDocument();
            doc.putProperty( MODIFIED, new Boolean( true ) );
            
            File file = (File)doc.getProperty( FILE );
            int index = tabPane.indexOfComponent( comp );
            
            tabPane.setTitleAt( index, file.getName() + '*' );
            
            doc.removeDocumentListener( this );
        }
        
        public void changedUpdate( DocumentEvent e ) {
        }
        
        public void insertUpdate( DocumentEvent evt ) {
            markChanged( evt );
        }
        
        public void removeUpdate( DocumentEvent evt ) {
            markChanged( evt );
        }
    }
}

import java.awt.*;
import java.awt.event.*; // Using AWT events and listener interfaces
import javax.swing.*;    // Using Swing components and containers
import java.io.File;
import java.io.IOException;
import java.util.Date;
 
// A Swing GUI application inherits from top-level container javax.swing.JFrame
public class MainFrame extends JFrame {
   private JTextField pcapFilePathField;
   private JButton extractTcpUdp;
   private JTextField pcapFileSavedDir; 
   private JButton extractTcpData;
 
   /** Constructor to setup the GUI */
   public MainFrame () {
      // Retrieve the content-pane of the top-level container JFrame
      // All operations done on the content-pane
      Container cp = getContentPane();

      cp.setLayout( new FlowLayout(FlowLayout.LEFT, 10, 20) );
      pcapFilePathField = new JTextField( 40 );
      pcapFilePathField.addMouseListener(new MouseAdapter() {
        public void mouseClicked( MouseEvent e ) {
          JFileChooser fileChooser = new JFileChooser( "./" );
          int result = fileChooser.showOpenDialog( MainFrame.this );
          if ( result == JFileChooser.APPROVE_OPTION ) {
            String pcapFilePath = fileChooser.getSelectedFile().getAbsolutePath();
            pcapFilePathField.setText( pcapFilePath );
          }
        }
      });

      cp.add(pcapFilePathField);
      extractTcpUdp = new JButton("算法1");
       extractTcpUdp.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          long start = new Date().getTime();
          String pcapFilePath = pcapFilePathField.getText();
          if ( pcapFilePath == null || pcapFilePath.length() == 0 ) {
            JOptionPane.showMessageDialog( MainFrame.this, "请选择pcap文件!");
            return;
          }
          new ExtractTcpUdpFromPcap( pcapFilePath ).extract();
          long end = new Date().getTime();
          long time = ( end - start ) / 1000l;
          JOptionPane.showMessageDialog( MainFrame.this, "解析成功, 共花费 " + time + " 秒!");
        }
      });
      cp.add(extractTcpUdp);

      pcapFileSavedDir = new JTextField( 40 );
      pcapFileSavedDir.addMouseListener(new MouseAdapter() {
        public void mouseClicked( MouseEvent e ) {
          JFileChooser fileChooser = new JFileChooser( "./" );
          fileChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
          int result = fileChooser.showOpenDialog( MainFrame.this );
          if ( result == JFileChooser.APPROVE_OPTION ) {
            String pcapSavedDir = fileChooser.getSelectedFile().getAbsolutePath();
            pcapFileSavedDir.setText( pcapSavedDir );
          }
        }
      });
      cp.add( pcapFileSavedDir );

      extractTcpData = new JButton( "算法2" );
      extractTcpData.addActionListener( new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          String pcapSavedDir = pcapFileSavedDir.getText();
          if ( pcapSavedDir == null || pcapSavedDir.length() == 0 ) {
            JOptionPane.showMessageDialog( MainFrame.this, "请选择pcap文件夹!");
            return;
          }
          new ExtractTcpData( pcapSavedDir ).extract();
          JOptionPane.showMessageDialog( MainFrame.this, "解析成功!");
        }
      });
      cp.add(extractTcpData);
 
      // Allocate an anonymous instance of an anonymous inner class that
      //  implements ActionListener as ActionEvent listener
     
 
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Exit program if close-window button clicked
      setTitle("信息安全算法"); // "this" JFrame sets title
      setBounds( new Rectangle(220, 100, 650, 400) );         // "this" JFrame sets initial size
      setVisible(true);          // "this" JFrame shows
   }
 
   /** The entry main() method */
   public static void main(String[] args) {
      // Run the GUI construction in the Event-Dispatching thread for thread-safety
      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {
            new MainFrame(); // Let the constructor do the job
         }
      });
   }
}
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package videoportalquery;

import java.awt.Window;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

/**
 *
 * @author User
 */
public class myProgressBar implements Runnable{

    @Override
    public void run() {
        
         JFrame f= new JFrame();
         f.setAlwaysOnTop(true);
         f.setLocationRelativeTo(null);
                        
         JWindow window = new JWindow(f);
         window.setLocationRelativeTo(f);
         JProgressBar progressbar = new JProgressBar();
         progressbar.setIndeterminate(true);
         window.getContentPane().add(progressbar);
         window.setSize(100, 20);
         window.setVisible(true);
         f.toFront();
    }
    
}

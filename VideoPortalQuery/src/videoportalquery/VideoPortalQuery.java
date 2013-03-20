/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package videoportalquery;


import de.uop.dimis.air.internalObjects.mpqf.DescriptionResourceType;
import de.uop.dimis.air.internalObjects.mpqf.InputQueryType;
import de.uop.dimis.air.internalObjects.mpqf.MpegQueryType;
import de.uop.dimis.air.internalObjects.mpqf.MpegQueryType.Query;
import de.uop.dimis.air.internalObjects.mpqf.QueryConditionType;
import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.SwingWorker.StateValue;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import myDescriptionScheme.ParamValueList;
import myDescriptionScheme.ParamValueListType;

/**
 *
 * @author User
 */
public class VideoPortalQuery extends JFrame {
    
    JFrame f;
    JPanel jp1;
    ResultPanel jp2 = null;
    static BrowserLauncher bl;
    static int presentOffset = 1;
    Result result;
    List<ResultItem> rl;
    JButton next;
    JButton previous;
    public static ParamValueList pvl = new ParamValueList() ;
    static final String timePeriodList[] = {"ALL TIME", "TODAY", "THIS WEEK", "THIS MONTH" };
    static final String orderingList[] = {"RELEVANCE", "PUBLISHED", "VIEW COUNT", "RATING" };

    public VideoPortalQuery() throws HeadlessException, MalformedURLException {
           
           this.setTitle("VideoPortalQuery");
           setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
           setSize(1000, 650); 
           setResizable(false);
           addComponentsToPane(getContentPane());
           setVisible(true); 
           setLocationRelativeTo(null);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws BrowserLaunchingInitializingException, UnsupportedOperatingSystemException, JAXBException, FileNotFoundException {
        try {
            System.setProperty("java.net.useSystemProxies", "true");
            bl = new BrowserLauncher();
            NativeRequestManager.registerDB();
            VideoPortalQuery videoPortalQuery= new VideoPortalQuery();
            videoPortalQuery.setVisible(true);
            
        } catch (Exception ex) {
            Logger.getLogger(VideoPortalQuery.class.getName()).log(Level.SEVERE, null, ex);
        } 

    }
    
    private void addComponentsToPane(final Container pane) throws MalformedURLException{
        
        jp1 = new JPanel();
        jp1.setBackground(new Color(136,238,136));
        GridBagLayout jp1Layout = new GridBagLayout();    
        GridBagConstraints c = new GridBagConstraints(); 
        jp1.setLayout(jp1Layout);

        jp2 = new ResultPanel();

        jp1.setPreferredSize(new Dimension(200,650));
        
        JLabel jlabel1 = new JLabel("Keywords:");
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = 0;
	c.gridy = 0;
        c.insets  = new Insets(10,10,0,50);
        jp1.add(jlabel1, c);
        
        final JTextField textfield = new JTextField();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 1;
        c.insets  = new Insets(5,10,0,50);
        jp1.add(textfield, c);
        
        JLabel jlabel2 = new JLabel("TimePeriod:");
        c.gridy = 2;
        c.insets  = new Insets(10,10,0,50);
        jp1.add(jlabel2, c);
        
        JLabel jlabel3 = new JLabel("(YouTube & Metacafe only)");
        jlabel3.setFont(new Font("Arial", Font.PLAIN, 11));
        c.insets  = new Insets(0,10,0,50);
        c.gridy = 3;
        jp1.add(jlabel3, c);
        
        final JComboBox timePeriod = new JComboBox(timePeriodList);
        c.gridy = 4;
        c.insets  = new Insets(5,10,0,50);
        jp1.add(timePeriod, c);
        
        JLabel jlabel4 = new JLabel("Ordering:");
        c.gridy = 5;
        c.insets  = new Insets(10,10,0,50);
        jp1.add(jlabel4, c);
        final JComboBox ordering = new JComboBox(orderingList);
        //c.weighty = 1.0;
        c.gridy = 6;
        c.insets  = new Insets(5,10,0,50);
        jp1.add(ordering, c);
        
        JButton send = new JButton("Send");
        c.weighty = 0.005;
        c.gridy = 7;
        c.insets  = new Insets(20,10,50,50);
        send.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                SwingWorker<Void,JFrame>  loadResults = new SwingWorker<Void,JFrame>() {
                    

                    @Override
                    protected Void doInBackground() throws Exception {
                        
                        publish(new JFrame()); 
                        presentOffset = 1;
                        
      
                    try {
                            if(!(textfield.getText().equals(""))){
                            pvl = new ParamValueList();
                            pvl.createParamValuePair("QueryByText", textfield.getText());
                            
                            String timePeriodString = timePeriod.getSelectedItem().toString();
                            timePeriodString = timePeriodString.trim();
                            timePeriodString = timePeriodString.replace(" ","_");
                            timePeriodString = timePeriodString.toLowerCase();
                            
                            pvl.createParamValuePair("TimePeriod", timePeriodString);
                            
                            String orderingString = ordering.getSelectedItem().toString();
                            orderingString = orderingString.trim();

                            pvl.createParamValuePair("Ordering", orderingString);
                            
                            
                         } else{
                            JOptionPane.showMessageDialog(null, "No Search Words entered, no Query was sent", "Error", JOptionPane.INFORMATION_MESSAGE);
                            return null;
                        }    
                        

                        MpegQueryType qry = VideoPortalQuery.createMPQF(pvl.pvl);
                        result = NativeRequestManager.sendRequest(qry);
                        rl = result.getResults();
                        jp2.removeAll();
                        jp2.presentResult(rl, 0);
                        jp2.repaint();
                        
                         
                        
                        JAXBContext context = JAXBContext.newInstance("de.uop.dimis.air.internalObjects.mpqf:myDescriptionScheme");
                        Marshaller marshaller = context.createMarshaller();
                        StringWriter sw = new StringWriter();
                        
                        marshaller.marshal(qry, sw);
                        
                        String query = sw.toString();

                        System.out.println(query);
                        
                        pvl.marshal();

                        pvl = new ParamValueList();
                        //f.setVisible(false);

                       
                    } 
                    catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Query Processing failed", "Error", JOptionPane.ERROR_MESSAGE);

                    Logger.getLogger(VideoPortalQuery.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    return null;
                 }
                    @Override
                    protected void process(List<JFrame> chunks) {
                        for (JFrame progBar : chunks){
                        f = progBar;      
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
                };
                loadResults.addPropertyChangeListener(new PropertyChangeListener() {

                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if ("state".equals(evt.getPropertyName())) {
				if (StateValue.DONE.equals(evt.getNewValue())) {
					f.setVisible(false);
                                        f = null;
				}
                    }
                } 
                });
                
                loadResults.execute();
     
                }
            
        });
        
        jp1.add(send, c);
        
        next = new JButton("Next");
        c.weighty = 1.0;
        c.gridy = 8;
        c.insets  = new Insets(10,10,0,50);
        next.setVisible(false);
        next.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                SwingWorker<Void,JFrame>  loadResults = new SwingWorker<Void,JFrame>() {
                    

                    @Override
                    protected Void doInBackground() throws Exception {
                        
                        publish(new JFrame()); 
                        
                    try {
                        jp2.removeAll();
                        jp2.presentResult(rl, 20);
                        jp2.repaint();

                    } catch (MalformedURLException ex) {
                        Logger.getLogger(VideoPortalQuery.class.getName()).log(Level.SEVERE, null, ex);
                        return null;
                    }
                        return null;  
                   }
                    @Override
                    protected void process(List<JFrame> chunks) {
                        for (JFrame progBar : chunks){
                        f = progBar;       
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
                };
                loadResults.addPropertyChangeListener(new PropertyChangeListener() {

                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if ("state".equals(evt.getPropertyName())) {
				if (StateValue.DONE.equals(evt.getNewValue())) {
					f.setVisible(false);
                                        f = null;
				}
                    }
                } 
                });
                
                loadResults.execute();
                };

        });
        
        jp1.add(next, c);
        

        previous = new JButton("Previous");
        c.weighty = 0.0;
        c.gridy = 9;
        c.insets  = new Insets(10,10,120,50);
        previous.setVisible(false);
        previous.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                SwingWorker<Void,JFrame>  loadResults = new SwingWorker<Void,JFrame>() {
                    

                    @Override
                    protected Void doInBackground() throws Exception {
                        
                        publish(new JFrame()); 
                        
                    try {
                        jp2.removeAll();
                        jp2.presentResult(rl, -20);
                        jp2.repaint();

                    } catch (MalformedURLException ex) {
                        Logger.getLogger(VideoPortalQuery.class.getName()).log(Level.SEVERE, null, ex);
                        return null;
                    }
                        return null;  
                   }
                    @Override
                    protected void process(List<JFrame> chunks) {
                        for (JFrame progBar : chunks){
                        f = progBar;      
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
                };
                loadResults.addPropertyChangeListener(new PropertyChangeListener() {

                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if ("state".equals(evt.getPropertyName())) {
				if (StateValue.DONE.equals(evt.getNewValue())) {
					f.setVisible(false);
                                        f = null;
				}
                    }
                } 
                });
                
                loadResults.execute();
                };

        });
        
        jp1.add(previous, c);
        
        final JCheckBox youtubeCheck = new JCheckBox("YouTube");
        youtubeCheck.setSelected(true);
        youtubeCheck.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(!youtubeCheck.isSelected()){
                        NativeRequestManager.singleRegisterDB("de.uop.dimis.iisDemo.interpreter.YouTubeAdapter", false);
                    } else{
                        NativeRequestManager.singleRegisterDB("de.uop.dimis.iisDemo.interpreter.YouTubeAdapter", true);
                    }
            }
        });
        c.gridy = 10;
        c.insets  = new Insets(10,10,0,100);
        jp1.add(youtubeCheck, c);
        
        final JCheckBox vimeoCheck = new JCheckBox("Vimeo");
        vimeoCheck.setSelected(true);
        vimeoCheck.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(!vimeoCheck.isSelected()){
                        NativeRequestManager.singleRegisterDB("de.uop.dimis.iisDemo.interpreter.VimeoAdapter", false);
                    } else{
                        NativeRequestManager.singleRegisterDB("de.uop.dimis.iisDemo.interpreter.VimeoAdapter", true);
                    }
            }
        });
        c.gridy = 11;
        jp1.add(vimeoCheck, c);
        
        final JCheckBox myvideoCheck = new JCheckBox("MyVideo");
        myvideoCheck.setSelected(true);
        myvideoCheck.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(!myvideoCheck.isSelected()){
                        NativeRequestManager.singleRegisterDB("de.uop.dimis.iisDemo.interpreter.MyVideoAdapter", false);
                    } else{
                        NativeRequestManager.singleRegisterDB("de.uop.dimis.iisDemo.interpreter.MyVideoAdapter", true);
                    }
            }
        });
        c.gridy = 12;
        jp1.add(myvideoCheck, c);
        
        final JCheckBox metacafeCheck = new JCheckBox("Metacafe");
        metacafeCheck.setSelected(true);
        metacafeCheck.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(!metacafeCheck.isSelected()){
                        NativeRequestManager.singleRegisterDB("de.uop.dimis.iisDemo.interpreter.MetacafeAdapter", false);
                    } else{
                        NativeRequestManager.singleRegisterDB("de.uop.dimis.iisDemo.interpreter.MetacafeAdapter", true);
                    }
            }
        });
        c.gridy = 13;
        c.insets  = new Insets(10,10,10,100);
        jp1.add(metacafeCheck, c);
        
        
        
        pane.add(jp1, BorderLayout.WEST);
        //pane.add(new JSeparator(), BorderLayout.NORTH);
        pane.add(jp2, BorderLayout.CENTER);
        

    }

    private class ResultPanel extends JPanel {

        public ResultPanel() throws MalformedURLException {
            
            setLayout(new GridLayout(4,5));
            setBackground(Color.DARK_GRAY);
             
            }
        public void presentResult(List<ResultItem> rl,int offset) throws MalformedURLException{
        
    

        presentOffset = presentOffset+offset;
        int index = presentOffset;
        System.out.println(rl.size());

  
            for(;rl.size()>=index;){


                if(index%20 == 0){
                    this.createItems(index, rl, false);
                }
                if(index%20 == 0)break;

                this.createItems(index, rl, false);
                
                System.out.println(index++);

            }
            
            int modulo = index%20;
            
            if(modulo != 0){
                int rest = 20-modulo;
                for(;rest!=0;rest--){
                 
                 this.createItems(index-1, rl, true);  
                    
                }
            }
               next.setVisible(false);
               if(index < rl.size())next.setVisible(true);
                else next.setVisible(false);
               previous.setVisible(true);
                if(presentOffset == 1)previous.setVisible(false);
                else previous.setVisible(true);
        }
        
        public void createItems(final int index,final List<ResultItem> rl, boolean invisible) throws MalformedURLException{
            
            
            JPanel singleResultPanel = new JPanel();
            singleResultPanel.setBackground(Color.DARK_GRAY);
            URL thumbURL = new URL(rl.get(index-1).getThumbURL());
            ImageIcon ii = new ImageIcon(thumbURL);
            
            BufferedImage resizedImg = new BufferedImage(133, 100, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = resizedImg.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(ii.getImage(), 0, 0, 133, 100, null);
            g2.dispose();
            ImageIcon ii2 = new ImageIcon(resizedImg);
            
            
            JLabel photoLabel = new JLabel();
            photoLabel.setIcon(ii2);
            
            String title = rl.get(index-1).getTitle();
            final JLabel text = new JLabel(title);
            text.setForeground(Color.white);
            text.setVerticalAlignment(SwingConstants.TOP);
            text.setHorizontalAlignment(SwingConstants.CENTER);
            text.setPreferredSize(new Dimension(133,20));
            text.setToolTipText(title);
            text.addMouseListener(new MouseAdapter(){
                
                @Override
               public void mouseEntered(MouseEvent me) {  
                  text.setCursor(new Cursor(Cursor.HAND_CURSOR));  
               }  
                @Override
               public void mouseExited(MouseEvent me) {  
                  text.setCursor(Cursor.getDefaultCursor());  
               }  
                @Override
               public void mouseClicked(MouseEvent me)  
               {  

                  bl.openURLinBrowser(rl.get(index-1).getImageURL());

               }  
 
            });
            
            final String source = rl.get(index-1).getSource();
            /*String[] sourceArray = source.split("\\.");
            String endSource = sourceArray[1];*/
            final JLabel text2 = new JLabel(source);
            text2.setForeground(Color.white);
            text2.addMouseListener(new MouseAdapter(){
                
                @Override
               public void mouseEntered(MouseEvent me) {  
                  text2.setCursor(new Cursor(Cursor.HAND_CURSOR));  
               }  
                @Override
               public void mouseExited(MouseEvent me) {  
                  text2.setCursor(Cursor.getDefaultCursor());  
               }  
                @Override
               public void mouseClicked(MouseEvent me)  
               {   

                  bl.openURLinBrowser("http://"+source);

               }  
 
            });
            
            singleResultPanel.add(photoLabel);
            singleResultPanel.add(text2);
            singleResultPanel.add(text);
            if(invisible)singleResultPanel.setVisible(false);
            this.add(singleResultPanel);

        }
            
    }
    public static MpegQueryType createMPQF(ParamValueListType pvl) {
        
        myDescriptionScheme.ObjectFactory myDescriptionOf = new myDescriptionScheme.ObjectFactory();
        de.uop.dimis.air.internalObjects.mpqf.ObjectFactory mpqfObj = new de.uop.dimis.air.internalObjects.mpqf.ObjectFactory();
        
        MpegQueryType mpegQuery = mpqfObj.createMpegQueryType();
        mpegQuery.setMpqfID(IDManager.getCurrentQueryID());

        Query query = mpqfObj.createMpegQueryTypeQuery();
        mpegQuery.setQuery(query);

        InputQueryType inputQuery = mpqfObj.createInputQueryType();
        inputQuery.setImmediateResponse(true);
        query.setInput(inputQuery);

        QueryConditionType condition = mpqfObj.createQueryConditionType();
        
        inputQuery.setQueryCondition(condition);
        de.uop.dimis.air.internalObjects.mpqf.QueryByDescription result = mpqfObj.createQueryByDescription();
        result.setPreferenceValue(1.0f);

        result.setMatchType("similar");
        
        condition.setCondition(result);
        
        DescriptionResourceType resource = mpqfObj.createDescriptionResourceType();
        resource.setResourceID(IDManager.getResourceID());
        result.setDescriptionResource(resource);

        DescriptionResourceType.AnyDescription description = mpqfObj.createDescriptionResourceTypeAnyDescription();
        resource.setAnyDescription(description);

        JAXBElement<ParamValueListType> jpvl =
                myDescriptionOf.createParamValueList(pvl);
        description.getContent().add(jpvl);
 
        return mpegQuery;
    }
        
        
}




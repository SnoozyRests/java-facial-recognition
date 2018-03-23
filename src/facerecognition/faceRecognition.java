package facerecognition;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

/**
 * @author Jacob Williams - 15008632
 * Contributors: n/a
 * Function: Handles form load, detection, and capture mode.
 * Notes: frame edit could be put in a function?
 */
public class faceRecognition extends javax.swing.JFrame {

    
    public boolean captureMode = false;
    /*
        VideoCapture(0) will be the default recording device
        VideoCapture(1+) further will be determined by activation order.
    */
    VideoCapture vc = new VideoCapture(0);
    
    //General runtime variables
    Mat vframe = new Mat();
    MatOfByte mob = new MatOfByte();
    CascadeClassifier cc = new CascadeClassifier("G:\\opencv\\sources\\data"
            + "\\haarcascades\\haarcascade_frontalface_alt.xml");
    MatOfRect mor = new MatOfRect();
    private cam cam = new cam();
    Thread t = new Thread(cam);
    public cUser us = new cUser();
    public DbBean db = new DbBean();
    
    class cam implements Runnable{
        @Override
        public void run() {
            if(captureMode == true){
                captureMode();
            }
            detect();
        }
    }
    
    public void captureMode(){
        boolean userConf = false;
        try{
        while(!userConf){
        String s = (String)JOptionPane.showInputDialog(null, 
                                                    "Please enter your ID",
                                                    "Enter ID",
                                                    JOptionPane.PLAIN_MESSAGE)
                                                    .trim();
        if(s.length() == 8 && s.matches("\\d+")){
            db.setUser(s);
            int count = 0;
            int index = 0;
            int imgNum = us.getImageCount() + 1;
            userConf = true;
            //Mat frame = new Mat();
            if(!vc.isOpened()){
                System.out.println("ERROR IN CAPTURE MODE: CAMERA OPEN(?)");
            }else{
                while(true){
                    if(vc.grab()){
                        vc.retrieve(vframe);
                        count++;
                        if(count % 17 == 0){
                            face face = model.getface(vframe.clone());
                            
                            if(face != null){
                                index++;
                                imwrite("G:\\Uni\\third_year\\DSP\\faceRecognition\\src\\faces" 
                                        + us.getID() + "_" + imgNum + ".png", 
                                        face.crop);
                                imgNum++;
                            }
                            try {
                                Graphics graphic = camPanel.getGraphics();
                                cc.detectMultiScale(vframe, mor);
                                for(Rect rect : mor.toArray()){
                                    Imgproc.rectangle(vframe, new Point(rect.x, 
                                        rect.y), new Point(rect.x + rect.width, 
                                                rect.y + rect.height), 
                                                new Scalar(0,255,0), 2);
                                }
                    
                                //display modified frame
                                Imgcodecs.imencode(".bmp", vframe, mob);
                                Image im;

                                im = ImageIO.read(new ByteArrayInputStream(
                                        mob.toArray()));
                                BufferedImage bi = (BufferedImage) im;

                                graphic.drawImage(bi, 0, 0, camPanel.getWidth(), 
                                camPanel.getHeight(), 0, 0, bi.getWidth(), 
                                bi.getHeight(), null);
                            } catch (IOException ex) {
                                Logger.getLogger(faceRecognition.class.getName
                                                ()).log(Level.SEVERE, null, ex);
                            }
                            
                            if(index == 10){
                                us.updateCount(index);
                                db.updateImgCount();
                                changeMode.setEnabled(true);
                                captureMode = false;
                                break;
                            }
                        }
                    }
                }
            }
        }else if(s.contains("2")){
            throw new NullPointerException();
        }else{
            JOptionPane.showMessageDialog(null, "Incorrect ID format.\n"
                    + "Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        }
        }catch(NullPointerException ex){
            changeMode.setEnabled(true);
            captureMode = false;
        }
    }
    
    public void detect(){
        while(captureMode != true){
            if(vc.grab()){
                try{
                    //initialise frame, find faces.
                    vc.retrieve(vframe);
                    Graphics graphic = camPanel.getGraphics();
                    cc.detectMultiScale(vframe, mor);
                     
                    //mark faces with scalar
                    for(Rect rect : mor.toArray()){
                        Imgproc.rectangle(vframe, new Point(rect.x, 
                                        rect.y), new Point(rect.x + rect.width, 
                                                rect.y + rect.height), 
                                        new Scalar(0,255,0), 2);
                    }
                    
                    //display modified frame
                    Imgcodecs.imencode(".bmp", vframe, mob);
                    Image im = ImageIO.read(new ByteArrayInputStream(
                            mob.toArray()));
                    BufferedImage bi = (BufferedImage) im;

                    graphic.drawImage(bi, 0, 0, camPanel.getWidth(), 
                        camPanel.getHeight(), 0, 0, bi.getWidth(), 
                        bi.getHeight(), null);
                    changeMode.repaint();
                }catch(IOException ex){
                    System.out.println(ex);
                }
            }
        }   
    }
    /**
     * Creates new form faceRecognition
     */
    public faceRecognition() {
        initComponents();
        t.start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        camPanel = new javax.swing.JPanel();
        changeMode = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setSize(new java.awt.Dimension(512, 288));

        camPanel.setFocusable(false);
        camPanel.setPreferredSize(new java.awt.Dimension(512, 288));

        changeMode.setText("Capture");
        changeMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeModeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout camPanelLayout = new javax.swing.GroupLayout(camPanel);
        camPanel.setLayout(camPanelLayout);
        camPanelLayout.setHorizontalGroup(
            camPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, camPanelLayout.createSequentialGroup()
                .addContainerGap(422, Short.MAX_VALUE)
                .addComponent(changeMode, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        camPanelLayout.setVerticalGroup(
            camPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, camPanelLayout.createSequentialGroup()
                .addContainerGap(247, Short.MAX_VALUE)
                .addComponent(changeMode, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(camPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(camPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void changeModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeModeActionPerformed
        try {
            captureMode = true;
            TimeUnit.SECONDS.sleep(1);

            t = new Thread(cam);
            t.start();

            changeMode.setEnabled(false);
        } catch (InterruptedException ex) {
            Logger.getLogger(faceRecognition.class.getName()).log(Level.SEVERE, 
                    null, ex);
        }

    }//GEN-LAST:event_changeModeActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(faceRecognition.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(faceRecognition.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(faceRecognition.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(faceRecognition.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new faceRecognition().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel camPanel;
    private javax.swing.JButton changeMode;
    // End of variables declaration//GEN-END:variables
}

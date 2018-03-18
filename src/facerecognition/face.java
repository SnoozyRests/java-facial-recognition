package facerecognition;

import org.opencv.core.Mat;
import org.opencv.core.Point;

/**
 * @author Jacob Williams
 */
public class face {
        public Point lEye;
        public Point rEye;
        public Mat crop;
        
        public face(){
            
        }
        
        public face(Point lEye, Point rEye){
            this.lEye = lEye;
            this.rEye = rEye;
        }
        
        public Point getLEye(){
            return lEye;   
        }
        
        public Point getREye(){
            return rEye;
        }
        
        public Mat getCrop(){
            return crop;
        }
        
        public void setCrop(Mat temp){
            crop = temp;
        }
}

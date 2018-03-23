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
        
        //a face consists of two eyes
        public face(Point lEye, Point rEye){
            this.lEye = lEye;
            this.rEye = rEye;
        }
        
        //get left eye
        public Point getLEye(){
            return lEye;   
        }
        
        //get right eye
        public Point getREye(){
            return rEye;
        }
        
        //get the currently saved crop
        public Mat getCrop(){
            return crop;
        }
        
        //set the crop
        public void setCrop(Mat temp){
            crop = temp;
        }
}

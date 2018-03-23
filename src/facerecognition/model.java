package facerecognition;

import static facerecognition.utils.crop;
import java.util.List;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import static org.opencv.imgproc.Imgproc.rectangle;
import org.opencv.objdetect.CascadeClassifier;

/**
 * @author Jacob Williams
 * Contributors: n/a
 * Function: Handles the creation of the face model and retrieval of precise
 *           face frames
 * Notes: n/a
 */
public class model {
    public static face createFaceModel(Mat origin){
        /*
            Operation: creates the face model from an inputted face.
            Inputs: Mat origin, the current frame.
            Outputs: returns the face detected. 
        */
        
        //define classifiers.
        CascadeClassifier fc = new CascadeClassifier("G:\\opencv\\sources\\data"
                + "\\haarcascades\\haarcascade_frontalface_alt.xml");
        CascadeClassifier ec = new CascadeClassifier("G:\\opencv\\sources\\data"
                + "\\haarcascades\\haarcascade_eye.xml");
        Mat image = origin.clone();
        
        //if theres no input, dont perform function.
        if(image.empty() == false){
            //define required variables.
            MatOfRect faces = new MatOfRect();
            Mat fgrey = new Mat();
            //cvtColor(image, fgrey, COLOR_BGR2GRAY);
            fc.detectMultiScale(image, faces);
            
            List<Rect> fRectFace = faces.toList();
            Rect face0 = fRectFace.get(0);
            
            //if there is a face, continue.
            if(fRectFace.size() == 1){
                //find the face.
                Rect _face = new Rect(face0.x, face0.y, face0.width, 
                        face0.height);
                rectangle(image, new Point(_face.x, _face.y), new Point
                            (_face.x + _face.width, _face.y + _face.height), 
                            new Scalar(0, 255,0,0), Math.max(1, (int) 
                            Math.round(image.cols() / 150)), 8, 0);
                
                //declare eye points for later use.
                Point lEye = new Point();
                Point rEye = new Point();
                
                //find the eyes.
                Mat faceROI = new Mat(image, face0);
                MatOfRect eyesMOR = new MatOfRect();
                List<Rect> RectEyes = eyesMOR.toList();
                if(RectEyes.size() != 2){
                    eyesMOR.release();
                    eyesMOR = new MatOfRect();
                    ec.detectMultiScale(faceROI, eyesMOR);
                    
                    RectEyes = eyesMOR.toList();
                }
                
                int r1, r2;
                
                /**
                In the case of this function, the concept of left eye and right
                eye are arbitrary, whether the first eye detected is left or 
                right is function continues assuming the first eye is left and 
                the second right, the two are corrected if necessary towards the
                end of this function.
                */
                
                //case based on how many eyes are detected.
                switch (RectEyes.size()) {
                    
                    //two eyes.
                    case 2:
                        //get both eye details.
                        Rect eye0 = RectEyes.get(0);
                        Rect eye1 = RectEyes.get(1);
                        
                        //define positions from retrieved details.
                        lEye = new Point((float)(face0.x + eye0.x + eye0.width 
                                * 0.5),
                                (float)(face0.y + eye0.y + eye0.height * 0.5));
                        rEye = new Point((float)(face0.x + eye1.x + eye1.width 
                                * 0.5),
                                (float)(face0.y + eye1.y + eye1.height * 0.5));
                        
                        //area sizing
                        r1 = (int) Math.round((face0.width + face0.height) 
                                * 0.15);
                        r2 = (int) Math.round((eye1.width + eye1.height) 
                                * 0.15);
                        //System.out.println("2");
                        break;
                    
                    //one eye.
                    case 1:
                        /*
                            if theres on one eye detected, then find other based 
                            on image size.
                        */
                        lEye = new Point((float) (face0.x + face0.x + 
                                face0.width * 0.5),
                                (float) (face0.y + face0.y + face0.height 
                                        * 0.5));
                        rEye = new Point(image.cols() / 2.0f, image.rows() / 
                                2.0f);
                        
                        //assume eye size is the same as the only one detected.
                        r1 = (int) Math.round((face0.width + face0.height) 
                                * 0.15);
                        r2 = r1;
                        //System.out.println("1");
                        break;
                    default:
                        //never reached, but exists as a safety blanket.
                        //assumes eye positioning based on image size.
                        lEye = new Point(image.cols() / 3.0f, 
                                image.rows() / 2.0f);
                        rEye = new Point((2.0f * image.cols()) / 3.0f, 
                                image.rows() / 2.0f);
                        
                        //assumes eye size based on image size, assumes eye size
                        r1 = (int) Math.round((face0.width + face0.height) 
                                * 0.03);
                        r2 = r1;
                        System.out.println("Default");
                        break;
                }
                
                /*
                    if the left x value is more than the right x value, then the
                    left eye is actually the right eye.
                */
                if(lEye.x > rEye.x){
                    Point temp = rEye;
                    rEye = lEye;
                    lEye = temp;
                }
                
                //save the face, return the face.
                face face = new face(lEye, rEye);
                return face;
            }
        }else{
            System.out.println("empty");
        }
        return null;
    }
    
    static face getface(Mat origin){
        //creates face model from the input.
        face face = createFaceModel(origin);
        if(face == null){
            System.out.println("Error at faceModel Creation.");
            return null;
        }
        
        //crops the face from the image based on the eye positions.
        Point left = face.getLEye();
        Point right = face.getREye();
        Mat image = origin.clone();
        image = crop(image, left, right, new Point(0.25f, 0.25f), 
                new Size(200, 200));
        
        //save the crop in the face model, return the cropped image.
        if(image != null && image.cols() > 0 && image.rows() > 0){
            face.crop = image;
            return face;
        }
        return null;
    }
}

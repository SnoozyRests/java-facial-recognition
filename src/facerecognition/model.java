package facerecognition;

//import java.awt.List;
import static facerecognition.utils.crop;
import java.util.List;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.equalizeHist;
import static org.opencv.imgproc.Imgproc.rectangle;
import org.opencv.objdetect.CascadeClassifier;

/**
 * @author Jacob Williams
 */
public class model {
    public static face createFaceModel(Mat origin){
        CascadeClassifier fc = new CascadeClassifier("G:\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_alt.xml");
        CascadeClassifier ec = new CascadeClassifier("G:\\opencv\\sources\\data\\haarcascades\\haarcascade_eye.xml");
        Mat image = origin.clone();
        
        if(image.empty() == false){
            MatOfRect faces = new MatOfRect();
            Mat fgrey = new Mat();
            //cvtColor(image, fgrey, COLOR_BGR2GRAY);
            imwrite("G:\\output\\img.png", image);
            //equalizeHist(fgrey, fgrey);
            fc.detectMultiScale(image, faces);
            
            List<Rect> fRectFace = faces.toList();
            Rect face0 = fRectFace.get(0);
            
            if(fRectFace.size() == 1){
                Rect _face = new Rect(face0.x, face0.y, face0.width, face0.height);
                rectangle(image, new Point(_face.x, _face.y), new Point
                            (_face.x + _face.width, _face.y + _face.height), 
                            new Scalar(0, 255,0,0), Math.max(1, (int) 
                            Math.round(image.cols() / 150)), 8, 0);
                Point lEye = new Point();
                Point rEye = new Point();
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
                switch (RectEyes.size()) {
                    case 2:
                        Rect eye0 = RectEyes.get(0);
                        Rect eye1 = RectEyes.get(1);
                        lEye = new Point((float)(face0.x + eye0.x + eye0.width * 0.5),
                                (float)(face0.y + eye0.y + eye0.height * 0.5));
                        rEye = new Point((float)(face0.x + eye1.x + eye1.width * 0.5),
                                (float)(face0.y + eye1.y + eye1.height * 0.5));
                        r1 = (int) Math.round((face0.width + face0.height) * 0.15);
                        r2 = (int) Math.round((eye1.width + eye1.height) * 0.15);
                        System.out.println("2");
                        break;
                    case 1:
                        lEye = new Point((float) (face0.x + face0.x + face0.width * 0.5),
                                (float) (face0.y + face0.y + face0.height * 0.5));
                        rEye = new Point(image.cols() / 2.0f, image.rows() / 2.0f);
                        r1 = (int) Math.round((face0.width + face0.height) * 0.15);
                        r2 = r1;
                        System.out.println("1");
                        break;
                    default:
                        lEye = new Point(image.cols() / 3.0f, image.rows() / 2.0f);
                        rEye = new Point((2.0f * image.cols()) / 3.0f, image.rows() / 2.0f);
                        r1 = (int) Math.round((face0.width + face0.height) * 0.03);
                        r2 = r1;
                        System.out.println("Default");
                        break;
                }
                
                if(lEye.x > rEye.x){
                    Point temp = rEye;
                    rEye = lEye;
                    lEye = temp;
                }
                face face = new face(lEye, rEye);
                return face;
            }
        }else{
            System.out.println("empty");
        }
        return null;
    }
    
    static face getface(Mat origin){
        face face = createFaceModel(origin);
        if(face == null){
            System.out.println("Error at faceModel Creation.");
            return null;
        }
        Point left = face.getLEye();
        Point right = face.getREye();
        Mat image = origin.clone();
        image = crop(image, left, right, new Point(0.25f, 0.25f), new Size(200, 200));
        
        if(image != null && image.cols() > 0 && image.rows() > 0){
            face.crop = image;
            return face;
        }
        return null;
    }
}

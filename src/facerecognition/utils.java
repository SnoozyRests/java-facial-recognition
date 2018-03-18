package facerecognition;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import static org.opencv.imgproc.Imgproc.INTER_LINEAR;
import static org.opencv.imgproc.Imgproc.getAffineTransform;
import static org.opencv.imgproc.Imgproc.getRotationMatrix2D;
import static org.opencv.imgproc.Imgproc.resize;
import static org.opencv.imgproc.Imgproc.warpAffine;

/**
 * @author Jacob Williams
 */
public class utils {
    static double getDistance(Point p1, Point p2){
        double x = p2.x - p1.x;
        double y = p2.y - p1.y;
        return Math.sqrt((x * x) + (y * y));
    }
    
    static Mat rotate(Mat image, double angle){
        Mat temp = new Mat();
        int x = image.cols(), y = image.rows(), length = Math.max(x, y);
        Mat rotation = getRotationMatrix2D(new Point(x / 2.0, y /2.0), angle, 1);
        warpAffine(image, temp, rotation, new Size(length, length));
        return temp;
    }
    
    static Mat crop(Mat image, Point lEye, Point rEye, Point offset, Size cropSize){
        try{
            double offset1 = Math.floor((offset.x) * cropSize.width),
                   offset2 = Math.floor((offset.y) * cropSize.height);
            
            double[] eDirection = new double[]{ rEye.x - lEye.x, rEye.y - lEye.y};
            double rotation = -Math.atan2(eDirection[1], eDirection[0]),
                   dist = getDistance(lEye, rEye),
                   ref = cropSize.width - 2.0 * offset1,
                   scale = dist / ref;
            
            image = srt(image, rotation, lEye, new Point(-1, -1), 1.0);
            
            Point crop = new Point(lEye.x - scale * offset1, lEye.y - scale * offset2);
            Point newSize = new Point(cropSize.width * scale, cropSize.height * scale);
            
            if(newSize.x < 50 || newSize.y < 50){
                return null;   
            }
            
            image = new Mat(image, new Rect((int) crop.x, (int) crop.y, (int) newSize.x, (int)newSize.y));
            
            resize(image, image, cropSize, 0.0, 0.0, INTER_LINEAR);
            return image;
        }catch(Exception x){
            System.out.println(x);
        }
        return null;
    }
    
    static Mat srt(Mat image, double angle, Point centre, Point newCentre, double scale){
        if(newCentre.x >= 0){
            Point Stri0 = new Point(centre.x, centre.y);
            Point Stri1 = new Point(centre.x + 1, centre.y);
            Point Stri2 = new Point(centre.x, centre.y + 1);
            MatOfPoint2f Stri = new MatOfPoint2f(Stri0, Stri1, Stri2);
            
            Point dTri0 = new Point(newCentre.x, newCentre.y);
            Point dTri1 = new Point(newCentre.x + 1, newCentre.y);
            Point dTri2 = new Point(newCentre.x, newCentre.y + 1);
            MatOfPoint2f dTri = new MatOfPoint2f(dTri0, dTri1, dTri2);
            
            Mat wrp = new Mat(image.rows(), image.cols(), image.type());
            
            Mat wrp_MAT = getAffineTransform(Stri, dTri);
            warpAffine(image, wrp, wrp_MAT, wrp.size());
            return wrp;
        }else{
            Mat wrp = image;
            Mat rot = new Mat();
            Mat rotMAT = getRotationMatrix2D(centre, angle, scale);
            warpAffine(wrp, rot, rotMAT, wrp.size());
            return rot;
        }
    }
}

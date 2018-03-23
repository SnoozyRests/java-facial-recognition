package facerecognition;
/**
 * @author Jacob Williams
 */
public class cUser {
    public static String id;
    public static int imageCount;
    
    public String getID(){
        return id;
    }
    
    public void setID(String uID){
        this.id = uID;
    }
    
    public void setImageCount(int nimagecount){
        this.imageCount = nimagecount;
    }
    
    public int getImageCount(){
        return imageCount;
    }
    
    public void updateCount(int update){
        this.imageCount = imageCount + update;
    }
    
    @Override
    public String toString(){
        return "id='" + id + "',\nimage count='" + imageCount + "'\n";
    }
}

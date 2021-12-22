import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.HighGui;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;


import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class main {
    private static final String filename = "src/secondvid_edit.mp4";

    static Scalar[][] rgbRange= new Scalar[3][2];
    static double ballArea;

    public static void main(String[] args){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // Loading the OpenCV library.
        int numOfFrames = 0;
        int RANGE = 30;
        Mat procImg;
        Mat circles;
        int initialY = 0;
        int firstBallFramesCounter = 0, secondBallFramesCounter = 0, thirdBallFramesCounter = 0, countOfBalls;

        VideoCapture vid = new VideoCapture(filename);
        Mat img = new Mat();
        JFrame jframe = new JFrame("Video"); // We create a new JFrame object.
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // We inform jframe what to do when we close the program.
        JLabel vidPanel = new JLabel(); // We create a new JLabel object.
        jframe.setContentPane(vidPanel); // We assign vidpanel to the jframe we created.
        jframe.setSize(2000, 4000); // We set the frame size to 2000x4000
        jframe.setVisible(true); // We make the jframe visible.
        double fps = vid.get(Videoio.CAP_PROP_FPS);

        while(vid.read(img)) {

            if(numOfFrames == 0){
                initialY = getFrameData(img);
            }



            procImg = prepareImage(img); // We prepare the image.

            /*
            We apply the HoughCircles algorithm, and get an array named circles that represents each circle we find.
            https://docs.opencv.org/4.x/dd/d1a/group__imgproc__feature.html#ga47849c3be0d0406ad3ca45db65a25d2d
            */
            /*
            circles = new Mat(); // Will represent the circles we find.
            Imgproc.HoughCircles(procImg, circles, Imgproc.CV_HOUGH_GRADIENT, 1.0, 80, 110.0, 24.0, 40, 100); // param2 = 24 for vid2, param2 = 28 for vid1

            countOfBalls = 0;
            for (int x = 0; x < circles.cols(); x++) {
                double[] c = circles.get(0, x); // Getting the circle, c is in the format of: {x, y, radius}.
                Point center = new Point(Math.round(c[0]), Math.round(c[1]));
                System.out.print(Math.abs(center.y - initialY) + "     "); // Printing the height
                // Drawing the circle's center.
                Imgproc.circle(img, center, 1, new Scalar(0, 100, 100), 3, 8, 0);
                // Drawing the circle's outlines.
                int radius = (int) Math.round(c[2]);
                Imgproc.circle(img, center, radius, new Scalar(255, 0, 255), 3, 8, 0);
                if(center.y >= initialY - RANGE && center.y <= initialY + RANGE) countOfBalls++;  // Only counts the balls that are in the range of the initial Y axis position.
            }

            System.out.println();

            switch(countOfBalls){
                case 3:
                    firstBallFramesCounter++;
                case 2:
                    secondBallFramesCounter++;
                case 1:
                    thirdBallFramesCounter++;
                    break;
            }

             */
            img = findContoursAndDraw(img);

            ImageIcon result = new ImageIcon(Mat2BufferedImage(img));
            vidPanel.setIcon(result);
            vidPanel.repaint();
            numOfFrames++;
       }

        System.out.println("Final Analysis: " + "\nFirst Ball: " + firstBallFramesCounter / fps
                + " seconds\nSecond Ball: " + secondBallFramesCounter / fps
                + " seconds\nThird Ball: " + thirdBallFramesCounter / fps + " seconds");
    }

    /**
     * This function takes the first frame and gets the initial position of the balls. It returns the initial Y axis position.
     * @param img the frame we analyze.
     * @return the initial Y axis position.
     */
    private static int getFrameData(Mat img) {
        Mat procImg = prepareImage(img);
        Mat circles = new Mat();


        Imgproc.HoughCircles(procImg, circles, Imgproc.CV_HOUGH_GRADIENT, 1.0, 80, 95.0, 26.0, 40, 100);

        double[] c = circles.get(0, 0); // Getting the circle, c is in the format of: {x, y, radius}.
        Point center = new Point(Math.round(c[0]), Math.round(c[1]));
        double[] rgb = img.get((int)center.y, (int)center.x);
        int sensitivity = 22;
        rgbRange[0][0] = new Scalar(rgb[0] - sensitivity, rgb[1] - sensitivity, rgb[2] - sensitivity);
        rgbRange[0][1] = new Scalar(rgb[0] + sensitivity, rgb[1] + sensitivity, rgb[2] + sensitivity);
        ballArea = Math.PI * c[2] * c[2];


        c = circles.get(0, 1);
        center = new Point(Math.round(c[0]), Math.round(c[1]));
        rgb = img.get((int)center.y, (int)center.x);
        rgbRange[1][0] = new Scalar(rgb[0] - sensitivity, rgb[1] - sensitivity, rgb[2] - sensitivity);
        rgbRange[1][1] = new Scalar(rgb[0] + sensitivity, rgb[1] + sensitivity, rgb[2] + sensitivity);
        ballArea = Math.min(ballArea, Math.PI * c[2] * c[2]);

        c = circles.get(0, 2);
        center = new Point(Math.round(c[0]), Math.round(c[1]));
        rgb = img.get((int)center.y, (int)center.x);
        rgbRange[2][0] = new Scalar(rgb[0] - sensitivity, rgb[1] - sensitivity, rgb[2] - sensitivity);
        rgbRange[2][1] = new Scalar(rgb[0] + sensitivity, rgb[1] + sensitivity, rgb[2] + sensitivity);
        ballArea = Math.min(ballArea, Math.PI * c[2] * c[2]);


        return (int)Math.round(circles.get(0, 0)[1]);
    }

    private static Mat findContoursAndDraw(Mat img){
        Mat green = new Mat();
        Mat blue = new Mat();
        Mat orange = new Mat();

        Core.inRange(img, rgbRange[0][0], rgbRange[0][1], green);
        Core.inRange(img, rgbRange[1][0], rgbRange[1][1], orange);
        Core.inRange(img, rgbRange[2][0], rgbRange[2][1], blue);


        List<MatOfPoint> greenContours = new ArrayList<MatOfPoint>();
        List<MatOfPoint> blueContours = new ArrayList<MatOfPoint>();
        List<MatOfPoint> orangeContours = new ArrayList<MatOfPoint>();

        Imgproc.findContours(orange, greenContours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.findContours(blue, blueContours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.findContours(green, greenContours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        double maxArea = ballArea * 0.4; // 500 for second vid
        float[] radius = new float[1];
        Point center = new Point();
        for (int i = 0; i < greenContours.size(); i++) {
            MatOfPoint cnt = greenContours.get(i);
            if (Imgproc.contourArea(cnt) > maxArea) {
                MatOfPoint2f c2f = new MatOfPoint2f(cnt.toArray());
                Imgproc.minEnclosingCircle(c2f, center, radius);
                if(radius[0] * radius[0] * Math.PI <= (ballArea * 1.9)) {
                    Imgproc.circle(img, center, (int) radius[0], new Scalar(0, 0, 255), 3, 8, 0);
               }
            }
        }

        for (int i = 0; i < blueContours.size(); i++) {
            MatOfPoint cnt = blueContours.get(i);
            if (Imgproc.contourArea(cnt) > maxArea) {
                MatOfPoint2f c2f = new MatOfPoint2f(cnt.toArray());
                Imgproc.minEnclosingCircle(c2f, center, radius);
                if(radius[0] * radius[0] * Math.PI <= (ballArea * 1.9)) {
                    Imgproc.circle(img, center, (int) radius[0], new Scalar(0, 0, 255), 3, 8, 0);
                }
            }
        }

        for (int i = 0; i < orangeContours.size(); i++) {
            MatOfPoint cnt = orangeContours.get(i);
            if (Imgproc.contourArea(cnt) > maxArea) {
                MatOfPoint2f c2f = new MatOfPoint2f(cnt.toArray());
                Imgproc.minEnclosingCircle(c2f, center, radius);
                if(radius[0] * radius[0] * Math.PI <= (ballArea * 1.9)) {
                    Imgproc.circle(img, center, (int) radius[0], new Scalar(0, 0, 255), 3, 8, 0);
                }
            }
        }

        return img;
    }

    /**
     * This function loads the image from the path into a matrix.
     * @param imagePath the path of the image we load.
     * @return the image represented in a matrix.
     */
    public static Mat loadImage(String imagePath) {
        Imgcodecs imageCodecs = new Imgcodecs(); // We create an Imgcodecs Object.
        return imageCodecs.imread(imagePath); // We return a Mat Object representing the image.
    }

    /**
     * This function displays the image in a new window.
     * @param image the image to display.
     */
    public static void showImage(Mat image){
        HighGui.imshow("result", image);
        HighGui.waitKey();
    }

    /**
     * This function prepares the image for the HoughCircles algorithm.
     * @param img the image we prepare.
     * @return the prepared image.
     */
    public static Mat prepareImage(Mat img){
        Mat dest = new Mat(); // Creating a destination Matrix (Image) for the grayscale.
        Imgproc.cvtColor(img, dest, Imgproc.COLOR_RGB2GRAY); // We grayscale the image to get it to binary form
        Imgproc.medianBlur(dest, dest, 3); // We blur the grayscale image using a kernel size of 3.
        // Imgproc.Canny(dest, dest, 70, 50);
        return dest;
    }

    /**
     * This function converts a matrix to a buffered image.
     * @param m the matrix we convert.
     */
    public static BufferedImage Mat2BufferedImage(Mat m) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if ( m.channels() > 1 ) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels()*m.cols()*m.rows();
        byte [] b = new byte[bufferSize];
        m.get(0,0,b); // get all the pixels
        BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }
}

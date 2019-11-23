/*Author: Danyang Wang
 * Class: ICS4U
 * Instructor: Mr Radulovich
 * Assignment name: Review Assignment
 * Description: This program takes user input for the original function, and attempts to draw
 * an approximation of the original function.
 */
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.animation.AnimationTimer;
import javafx.scene.shape.Line;
public class Assignment1 extends Application {

	//size of the window
    private int WINDOW_WIDTH;
    private int WINDOW_HEIGHT;
    
    private int numOfLines;	//the number of spinning lines used to draw the function
    private double deltaT;	//the time increment used to calculate CFX and CFY
    private int numOfPoints;	//the number of points that the original function has
    private int lineThickness;	//the thickness of the line 
    
    private Group linesGroup;	//stores the spinning lines
    private Group functionGroup;	//stores the original and approximated function
    
    //stores x and y coordinates of hardcoded function or the file 
    private ArrayList <Double> Xcoord;	
    private ArrayList <Double> Ycoord;
    
    //each line's starting end point
    private ArrayList <Double> CFX;
    private ArrayList <Double> CFY;

    
    @Override
    public void start(Stage primaryStage)  {
    	
    	WINDOW_WIDTH = 600;
    	WINDOW_HEIGHT = 600;
    	numOfLines = 1057;		//CHANGES THE NUMBER OF SPINNING LINES YOU HAVE
    	lineThickness = 2;
    	linesGroup = new Group();
    	functionGroup = new Group();
    	
    	Xcoord = new ArrayList<Double>();
    	Ycoord = new ArrayList<Double>();
    	CFX = new ArrayList <Double>();
        CFY = new ArrayList <Double>();
       
     
        loadFunction("function.txt");	//LOADS THE FUNCTION YOU WANT TO APPROXIMATE
        deltaT = 1.0/(numOfPoints-1);

        drawFunction();

        
        Group root = new Group();
        root.getChildren().addAll(linesGroup, functionGroup);
        
        //adds x-axis and y-axis to the window
        Line xaxis = new Line (0, WINDOW_WIDTH/2, WINDOW_HEIGHT, WINDOW_WIDTH/2);
        Line yaxis = new Line (WINDOW_HEIGHT/2, 0, WINDOW_HEIGHT/2, WINDOW_WIDTH);
        root.getChildren().addAll(xaxis, yaxis);
        primaryStage.setTitle("Assignment 1");
        primaryStage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
        primaryStage.show();
    }

    public static void main(String[] args) throws Exception{
        launch(args);
    }

    //creates coordinates for a hardcoded mathematical function 
    //also draws that function on screen
    public void loadFunction() {
        double startDomain, endDomain, domainIncrement;
        double y;
        double prevX = 0 , prevY = 0;
        startDomain=-20;	//starting x value 
        endDomain=20;		//ending x value
        domainIncrement=0.1;	//how much to increment x by each time
        
        //generates the coordinates for the given function
        for (double x=startDomain;x<=endDomain;x+=domainIncrement) {
            
            y=x*x;    // CHANGE THIS TO HARDCODE THE MATHEMATICAL FUNCTION YOU WANT
            
            if (y!=y) {		//checks if y is a real number
            	System.out.println("y is not a real number");
            	continue;		
            }
            
            Xcoord.add(x);
            Ycoord.add(y);
            numOfPoints++;
            
            //draws the original function on screen
            if (x!=startDomain) {
                Line line = new Line(prevX, prevY,x + WINDOW_WIDTH/2,
                        -y + WINDOW_HEIGHT/2);
                line.setStroke(Color.RED);
                functionGroup.getChildren().add(line);
            }
            prevX=x + WINDOW_WIDTH/2;
            prevY=-y + WINDOW_HEIGHT/2;
        }
    }
    
    //takes the name of the file, and creates two arraylists of coordinates after reading from it
    //also draws the function on screen
    public void loadFunction(String file)  {
        
    	double x,y;
    	double prevX = 0 , prevY = 0;
        File input = new File(file);
        Scanner scan;
		try {
			scan = new Scanner(input);
			
			//read the x and y coordinates on each line of the file and store them in arraylists
			while (scan.hasNextLine()) {
				String line[] = scan.nextLine().split(",");
		        x=Double.parseDouble(line[0]);
		        y=Double.parseDouble(line[1]);
		        Xcoord.add(x);
		        Ycoord.add(y);
		            
		            //draws the function corresponding to the origin (300,300)
		        if (numOfPoints>1) {
		        	Line lines = new Line(prevX, prevY, x + WINDOW_WIDTH/2,
		                    -y + WINDOW_HEIGHT/2);
		        
		        	lines.setStroke(Color.RED);
		        	functionGroup.getChildren().add(lines);
		        }
		        prevX = x + WINDOW_WIDTH/2;
		        prevY = -y + WINDOW_HEIGHT/2;
		        numOfPoints++;
		            
		   }
		   scan.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("File Not Found");
			e.printStackTrace();
		}

      
    }

    //Calculates and stores each line's starting end point based on the given coordinates
    public void findCFXY() {
        deltaT=1.0/(numOfPoints-1);
        
        //calculates each line 
        for (int freq = -numOfLines/2; freq <= numOfLines/2; freq++) {

            double startX = 0;
            double startY = 0;

            int tIndex = 0;

            //used t = tIndex * deltaT for better precision 
            //if I used t += deltaT, the value of t would be less accurate 
            for (double t = 0; tIndex < Xcoord.size(); t = tIndex*deltaT) {
                Double X = Xcoord.get(tIndex);
                Double Y = Ycoord.get(tIndex);
                startX += deltaT * (X * Math.cos(2 * Math.PI * freq * t)
                        + Y * Math.sin(2 * Math.PI * freq * t));
                startY -= deltaT * (X * Math.sin(2 * Math.PI * freq * t)
                        - Y * Math.cos(2 * Math.PI * freq * t));
                tIndex++;
            }
            //store the results inside the arraylists
            CFX.add(startX);
            CFY.add(startY);
        }
    }

    //animates the approximated function along with the spinning lines
    public void drawFunction() {
        findCFXY();
        
        AnimationTimer timer = new AnimationTimer() {
            double time = 0;
            double prevX,prevY;
            int i = 0;
            @Override
            public void handle(long now) {
            	//more accurate than time += deltaT
            	time = i*deltaT;
            	
            	//stops updating after 1 cycle
            	if (time <= 1) {
            		linesGroup.getChildren().clear();

            		double lastX = WINDOW_WIDTH / 2;
            		double lastY = WINDOW_HEIGHT / 2;
                	
            		//updates the coordinates of the spinning lines
            		for (int i = 0, freq = -numOfLines/2; freq <= numOfLines/2; i++, freq++) {
            			double updatedEndX = findX(CFX.get(i), CFY.get(i), freq, time);
            			double updatedEndY = findY(CFX.get(i), CFY.get(i), freq, time);
            			Line line = new Line(lastX, lastY,
                            lastX + updatedEndX,
                            lastY - updatedEndY);
                
            			linesGroup.getChildren().add(line);
                            
            			
            			lastX += updatedEndX;
                    	lastY -= updatedEndY;
            		}
       
            		
            		//draws the approximated function
            		if (time != 0) {
            			Line line = new Line(prevX,prevY,lastX,lastY);
           //changes the thickness of the approximated function to differentiate from spinning lines
            			line.setStrokeWidth(lineThickness);	
            			functionGroup.getChildren().add(line);     	
            		}
            		prevX=lastX;
            		prevY=lastY;
            	}
            	i++;
                
            }
        };

        timer.start();

    }

    //plots a line of length a that spins around the origin at frequency f
    public double sin(double a, double f, double t, double startAngle) {
        return a * Math.sin(Math.toRadians(360 * f) * t);
    }
    public double cos(double a, double f, double t, double startAngle) {
        return a * Math.cos(Math.toRadians(360 * f) * t);
    }

    //approximates the x coordinate of the approximated function at the current time
    public double findX(double cfx, double cfy, double freq, double time) {
        return cfx* Math.cos(2*Math.PI * freq * time) - cfy * Math.sin(2 * Math.PI * freq * time);
    }
    
    //approximates the y coordinate of the approximated function at the current time
    public double findY(double cfx, double cfy, double freq, double time) {
        return cfx* Math.sin(2*Math.PI * freq * time) + cfy * Math.cos(2 * Math.PI * freq * time);
    }
}
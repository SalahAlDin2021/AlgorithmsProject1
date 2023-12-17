

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainController implements Initializable{
	//declare components in sceneBuilder
	@FXML
	Text salah;
	@FXML
	Button fileChooser ;
	@FXML
	AnchorPane pane ;
	@FXML
	ScrollBar scrollH;
	@FXML
	ScrollBar scrollV;
	
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	/////function that execute when click th button 'Click to Upload File' in the pane/////
	public  void read(ActionEvent event) throws Exception {
		/*Clear the content of previous operation(previous calculation of max Leds)
		to add the new result of new file we read it*/
		pane.getChildren().clear();
		//add the text of my name and id to pane and the button that upload file in the top of pane. 
		pane.getChildren().addAll(salah,fileChooser);
		Scanner read = null;
	      try {
	    	  //file choser to allow select file from my computer by select it
	    	  FileChooser fileChooser = new FileChooser();
	    	  Stage stage =(Stage)pane.getScene().getWindow();
	    	  //get the file we chose it to read it by scanner
	    	  File file = fileChooser.showOpenDialog(stage);
		 read = new Scanner(file);
		 //go to function readFile(int file); to read the file and return the array in file
		 int[] arr = readFile(file);
		 //scrollH or V value property to allow user to see all pane by scrolling
		 scrollH.valueProperty().addListener(event2 ->{
				pane.setTranslateX(scrollH.getValue()*-(arr.length)/2);
			});
			scrollV.valueProperty().addListener(event2 ->{ 
				pane.setTranslateY(scrollV.getValue()*-(arr.length)/1.4);
			});
			//if readFile function return null then that happend exception while read file
			if(arr != null) {
				/*check if the file conent is valid for this project 
				 *the file content will have size array N in first line and N numbers in next line
				 *the numbers will be >= 1 and <= N , without repetion , we check it by
				 *hash table of N bits size( N/8 Bytes) , we give every index 1 bit(explaind in function)
				 *if the number>N or number<1 -> return false (invalid content)  
				 *and if the number is repetion -> return false (invalid content)
				 */
				if(check(arr, arr.length)) {
					/*max Led is the main function in the project (Core of project)
					 * we draw the result after calculate it in this function
					 ***(every thing explaind in function)
					 */
					maxLed(arr, arr.length);
				}else {
					Text ErrorMassge = text("ERROR!!!, the content of file is not valid!!", 20, 400, 45, "RED") ;
					pane.getChildren().add(ErrorMassge);
				}	
			}else {
				Text ErrorMassge = text("ERROR!!!, the type or content of file is not valid!!", 20, 400, 45, "RED") ;
				pane.getChildren().add(ErrorMassge);
			}
			
		 
		 }catch (Exception e) {
			 Text ErrorMassge = text("ERROR!!!,No selected File OR size is 0 ", 20, 400, 45, "RED") ;
			 pane.getChildren().add(ErrorMassge);
		 }
		
		
		
		
		
	}
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////Read File Function/////////////////////////////////////
public int[] readFile(File file) throws FileNotFoundException{
	//first read the size of array and then read all aray by for loop and nextInt() function.
		 Scanner read = new Scanner(file);
	      int size = 0;
	      int arr[] = null;
	      try {
		      size =Integer.parseInt(read.nextLine());
		      arr = new int[size];
		      for(int i = 0 ; i < size ; i++) {
		    	  arr[i]=read.nextInt();
		      }
	      }catch (Exception e) {
	    	  //if an error accure return null to draw warning(Error) text to user
	    	  return null;
		}
	      //return array after read it
	      return arr;

	}
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////


	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	////////////////////////Check Data Validation Function/////////////////////////////////
public boolean check(int[] arr,int size) {
	/* we need to control the bits in the byte because we need 1 bit
	 * to represent array in hash(to check it) , if array element metions 1st time
	 * then the bit of this element(byte -> arr[i]/8 and bit -> (8-arr[i])%8 
	 * 'we put 8 not 7 becouse the data should be >= 1 ) will be 1
	 * and if the bit of element is 1 then we know its repetion and we return false
	 * if element > N or element <1 return false .
	 */
	int byteNumber = size /8 ;
	byte bitNumber = (byte) (size%8);
	byte[] hash ;
	if(size%8 == 0) hash= new byte[byteNumber];//suppose we have 8 element we need 1 byte to represent it(8/8)
	else hash= new byte[byteNumber+1];//suppose we have 5 element we need 1 byte to represent it(5/8+1)
	for(int i = 0 ; i < hash.length;i++)hash[i]=0;//assign zero to all elements of hash
	for(int i=0 ; i < size ; i++) {
		//if element > N or element <1 return false .
		if(arr[i] > size || arr[i] <1) {
			return false;
		}
		//get byte number and bit number of array element to check it
		byteNumber=(arr[i]-1)/8;
		bitNumber=(byte) (8-arr[i]%8);
		//check if the bit is 0 or one by shift left and bitwise AND operation with 0 byte 
		byte k = (byte) ((hash[byteNumber])&(1<<bitNumber));
		if(k == 0) {
			//if k == 0 then the byte does not metion befor and we need to make it 1
			//we make it 1 by shift left with bitwise OR with bit number
			hash[byteNumber]=(byte) (hash[byteNumber]|(1<<bitNumber));
		}else {
			return false ;
			}
		}
	return true ;
	}




	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////Algorithm Function//////////////////////////////////////

public void maxLed(int[] arr ,int size ) {
	/*here we declare an array of size same input array to store the optimal result 
	 * first we assign 1 to all aray element , becouse this element is first solution 
	 */
	int[] optimal = new int[size];
	/*here we declare an array of size same input array to store the indices of optimal solution
	 * first we assign -1 to all aray element , so we sart traverse the optimal solution in index(max of optimal array)
	 * and then we traverse the optimal numbers , so we find indices = -1 we stop traverse. 
	 */
	int[] indices = new int[size];
	for(int i =0;i<size;i++) {
		optimal[i]=1;
		indices[i]=-1;
	}
	//fill the two arrays to find the optimal solution
	for(int i = 1 ; i < size ;i++) {
		for(int j = 0 ; j < i ;j++) {
			if(arr[i]>arr[j] && optimal[j] >= optimal[i]) {
				optimal[i]=optimal[j]+1;
				indices[i]=j;
			}
		}
	}
	//get the index of optimal solution (get max number of leads)
	//and with this index we find the road of traverse optimal numbers 
	int maxIndex = 0;
	for(int i=0 ; i < size ; i++) {
		if(optimal[i] > optimal[maxIndex])
			maxIndex=i;
	}
//	System.out.println(optimal[maxIndex]);
	//draw the result in the scene of javafx
	draw(arr,indices,maxIndex,optimal[maxIndex]);
	
}

	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////


	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////Draw part////////////////////////////////////////


	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////Print the Result in the scene Function///////////////////////////
 void draw(int[] arr , int[] indices ,int maxIndex,int solutionSize){
	 Text maxLeds = text("MAX LED's : "+solutionSize, 5 , 150 , 35, "DARKBLUE");
	 pane.getChildren().add(maxLeds);
	 int[] solution=new int[solutionSize];
//	 solution[solutionSize]=-2;
	 int k1=solutionSize-1, tempMaxIndex =maxIndex;
	 while(true) {
		 if(tempMaxIndex==-1) {
			break;
			}else {
				solution[k1]=arr[tempMaxIndex];tempMaxIndex=indices[tempMaxIndex];k1--;
			}
	 }
	 drawStructOfArray(arr, indices,solution);
	 drawContentOfArray(arr, indices, maxIndex,solution);
	 //draw the lines between battiry and LED's
	int k2=0;k1=0;
	for(int i = 0; i < indices.length;i++) {
		//array 
		Text inputArray = text(""+arr[i], 30 , 705+50*i , 22, "DARKBLUE");
		//circles 1
		Circle battiry = new Circle(15);
		battiry.setCenterX(80);
		battiry.setCenterY(700+50*i);
		battiry.setFill(Paint.valueOf("BLACK"));
		//array 
		Text battery1ToN = text(""+(i+1), 255 , 705+50*i , 22, "DARKBLUE");
		//circles 1
		Circle led = new Circle(15);
		led.setCenterX(230);
		led.setCenterY(700+50*i);
		led.setFill(Paint.valueOf("BLACK"));
		if(k1 < solutionSize)
		if(solution[k1] == arr[i] ) {
			battiry.setFill(Paint.valueOf("YELLOW"));
			Line line =  line(80, 700+50*i, 230, 700+50*(arr[i]-1), 4);
			pane.getChildren().add(line);
			k1++;
			
		}
		if(k2 < solutionSize)
		if(solution[k2] == i+1 ) {
			led.setFill(Paint.valueOf("YELLOW"));k2++;
		}
		
		pane.getChildren().addAll(led,battery1ToN,battiry,inputArray);
		
	}

}

	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////draw the content of arrays we need to draw it//////////////////////
 void drawContentOfArray(int[] input, int[] indices,int maxIndex,int[] solution) {
	 int k =0;

		for(int i = 0; i <solution.length;i++) {
			//show the array to the pane
			Text solutionIndex = text(""+i, 60+i*50, 550, 22, "LIGHTGREEN");
			//show ineces to the pane
			Text solutionArray = text(""+solution[i], 60+i*50, 595, 22, "LIGHTGREEN");
			
			Line line = line(50+i*50, 560, 50+i*50, 630, 1);
			pane.getChildren().addAll(solutionArray,solutionIndex,line);
		}
		for(int i = 0; i <indices.length;i++) {
			//show the input Array and its index of every element to the pane
			Text inputIndex = text(""+i, 60+i*50, 240, 22, "RED");
			Text inputArray = text(""+input[i], 60+i*50, 285, 22, "RED"); 
			//show the ineces Array and its index of every element to the pane
			Text indicesIndex =text(""+i, 60+i*50, 400, 22, "RED");
			Text indicesArray = text(""+indices[i], 60+i*50, 445, 22, "RED"); 
			//line between array elements
			Line line1 = line(50+i*50, 410, 50+i*50, 480, 1);
			Line line2 = line(50+i*50, 250, 50+i*50, 320, 1); 
			if(k < solution.length)
			if(i == solution[k] ) {
				//if the number is part of optimal solution , then change the color of it.
				Line li = new Line();
				inputArray.setFill(Paint.valueOf("LIGHTGREEN"));
				inputIndex.setFill(Paint.valueOf("LIGHTGREEN"));
				indicesIndex.setFill(Paint.valueOf("LIGHTGREEN"));
				indicesArray.setFill(Paint.valueOf("LIGHTGREEN"));
				k++;
				
			}
			
			pane.getChildren().addAll(line2,line1,indicesIndex,inputIndex,inputArray,indicesArray);
		}
 }
 

	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
 

	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////draw the box of arrays we need to draw it//////////////////////////
 void drawStructOfArray(int[] input ,int[] indices,int[] result) {
	 
		
	 		//input Array
	 		Text index1 = text("index", 0, 245, 15, "PURPLE");
	 		Text content1 = text("array", 0, 285, 15, "PURPLE");
			//line 1
			Line line11 = line(50, 250, 50+indices.length*50, 250, 1);
			//line 2
			Line line12 = line(50, 320, 50+indices.length*50, 320, 1);
			//line 3
			Line line13 = line(50, 250, 50, 320, 1);
			//line 4
			Line line14 = line(50+indices.length*50, 250, 50+indices.length*50, 320, 1);
			
			//indices Array
			Text index2 = text("index", 0, 405, 15, "PURPLE"); 
			Text content2 = text("indices", 0, 445, 15, "PURPLE");
			//line 21
			Line line21 = line(50, 410, 50+indices.length*50, 410, 1);
			//line 22
			Line line22 = line(50, 480, 50+indices.length*50, 480, 1);
			//line 23
			Line line23 = line(50, 410, 50, 480, 1); 
			//	line 24
			Line line24 = line(50+indices.length*50, 410, 50+indices.length*50, 480, 1);
			//Solution Array
			 Text index3 = text("index", 0, 555, 15, "PURPLE");
			 Text content3 = text("Result", 0, 595, 15, "PURPLE");new Text("Result");
			//line 31
			Line line31 = line(50, 560, 50+result.length*50, 560, 1);
			//line 32
			Line line32 = line(50, 630, 50+result.length*50, 630, 1);
			//line 33
			Line line33 = line(50, 560, 50, 630, 1);
			//line 34
			Line line34 = line(50+result.length*50, 560, 50+result.length*50, 630, 1);
		pane.getChildren().addAll(line11,line12,line13,line14,
							      line21,line22,line23,line24,
							      line31,line32,line33,line34,
							      index1,content1,
							      index2,content2,
							      index3,content3);
 }

	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
 

	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////make a Text object  with this attributes ////////////////////////
 public Text text(String text,int startX,int startY,int size,String color) {
	 Text txt = new Text(text);
	 txt.setX(startX);
	 txt.setY(startY);
	 txt.setFont(Font.font("regular", FontWeight.BOLD, size));
	 txt.setFill(Paint.valueOf(color));
	 return txt ;
	
}

	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
 

	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////make a Line object  with this attributes ////////////////////////
 public Line line(int startX,int startY,int endX,int endY,int strokeWidth) {
	 Line line = new Line();
		line.setStartX(startX);
		line.setStartY(startY);
		line.setEndX(endX);
		line.setEndY(endY);
		line.setStrokeWidth(strokeWidth);
		return line;
 }
 
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated function stub
		
	}
	

}

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 */

/**
 * @author Peter Armstrong
 *
 */
public class LinkChecker {
	
	 private static ArrayList<String> outArray;
	 private static ArrayList<String> linkArray;
	 public LinkChecker(String url)
	 {
		// create output array
		 outArray=new ArrayList<String>();
		 linkArray=new ArrayList<String>();
		 
		 try 
		 {
			URL linkPage=new URL(url);
			
			BufferedReader page=new BufferedReader(new InputStreamReader(linkPage.openStream()));
			String str;
			
			//goes through each line of the page until no lines left
			while((str=page.readLine())!=null) 
			{
			
				//replaces any refrence to A HREF with lowercase
				// Attempt to use regular expressions
				//str=str.replaceAll("A(\\s+)HREF(\\s+)=(\\s+)","a href=");
				
				//str=str.replaceAll("a(\\s+)href(\\s+)=(\\s+)", "a href=");
			//	System.out.println(str);
				// finds all lines that contain reference to a link
				
				str=str.replaceAll("A HREF=","a href=");
				
				if(str.contains("<a href=")) 
				{
					
					//finds the index of that reference
					int frontindex=str.indexOf("<a href=");
					// adds 9 to it to move the frontindex to the start of the actual link
					frontindex=frontindex+9;
					// finds the end of the link - the next place there is a "
					int endindex=str.indexOf("\"", frontindex);
					// creates a substring between these two indexes
					String link=str.substring(frontindex,endindex);
					String firstLetter=str.substring(frontindex, frontindex+1);
					if (!link.contains("google")) 
					{
						if(firstLetter.equals("h")) 
						{
						//	System.out.println(link + " is an external link");
							
				//			isLive(link);
							linkArray.add(link);
							
						}
						
						// have to start again if theyre not external links					
						else if(firstLetter.equals("/"))
						{
						//	System.out.println(link +" is an internal link");
							link=url+link;
							linkArray.add(link);
						//	System.out.println(link +" is an internal link");		
					//		isLive(link);
						}
						else if(firstLetter.equals("#")) 
						{
							link=url+link;
						//	System.out.println(link);
							linkArray.add(link);
							
							// needs to call another method that checks if theres an name id
				//			isLive(link);
						}
						else 
						{
							// link is an internal one, hmm....
							
						}
					}
					
				}
				
			}
			page.close();
				
			
		} 
		catch (MalformedURLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	 }
	 
	 public void printArray()
	 {
		 for (int i=0; i<linkArray.size(); i++)
 		{
 			System.out.println(linkArray.get(i));
 		}
	 }
	 
	 public void checkLinks()
	 {
		 for (int i=0; i<linkArray.size(); i++)
		 {
			 try 
			 {
				// tries to create a new URL object with the contents of the element of the arraylist
				URL link= new URL(linkArray.get(i));
				
				HttpURLConnection connection = (HttpURLConnection) link.openConnection();
				connection.setConnectTimeout(5000);
				connection.setRequestMethod("HEAD");
				connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");
				connection.connect();
				int code=connection.getResponseCode();
				
				
				if (code !=200)
				{
				//	System.out.println(code);
					// add link and the code to the arraylist
					outArray.add("<a href=\""+link+ "\"</a>"+link+"  \t |\t "+code +"\n </br>");
				}
			 } 
			 catch (UnknownHostException e)
			 {
				 outArray.add("<a href=\""+linkArray.get(i)+"\"</a>"+ linkArray.get(i)+" \t | \t Unknown Host Exception \n </br>");
				 
			 }
			 catch (SocketTimeoutException e)
			 {
				 outArray.add("<a href=\""+linkArray.get(i)+"\"</a> "+ linkArray.get(i)+"\t | \t Timed Out \n </br>");
				 
			 }
			 catch (MalformedURLException e) 
			 {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
	 }
	 
	 
	 private void printOutArrayToFile()
	 {
		 try {
			FileWriter outputFile=new FileWriter("linkeroutput.html");
			BufferedWriter out=new BufferedWriter(outputFile);
    		
    		//prints arraylist outArray to the file
    		for (int i=0; i<outArray.size(); i++)
    		{
    			out.write(outArray.get(i));
    			out.newLine();
    		}
    		out.close();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	 }
	
	public static void main(String[] args) 
	{
		
		for(String link: args)
		{
			LinkChecker checker=new LinkChecker(link);
			//checker.printArray();
			checker.checkLinks();
			checker.printOutArrayToFile();
			
			
		}
		
		
	}
	

}

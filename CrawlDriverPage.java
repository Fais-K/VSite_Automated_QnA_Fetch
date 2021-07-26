package questionextractor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class CrawlDriverPage {
	public static void main(String[] args) throws IOException {
		System.setProperty("webdriver.chrome.driver", "C:\\Selenium Jars\\chromedriver.exe"); 
		WebDriver driver = new ChromeDriver(); 
		
		driver.get("https://www.vskills.in/practice/selenium-webdriver-questions"); 
		
		driver.manage().window().maximize(); 
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		driver.findElement(By.id("btn-finish")).click();
		
		driver.findElement(By.xpath("//form[@id='login']//input[@placeholder='Email Address']")).sendKeys("************");
		driver.findElement(By.xpath("//form[@id='login']//input[@placeholder='Password']")).sendKeys("**********");
		
		driver.findElement(By.xpath("//button[normalize-space()='Login']")).click();
		
		XWPFDocument document = new XWPFDocument(); 
	      
		FileOutputStream out = new FileOutputStream(new File("C:\\Training\\vskills_webdriver_questions.docx"));
		XWPFParagraph paragraph = document.createParagraph();
		XWPFRun run = paragraph.createRun();

		int FETCHCOUNT = 500;
		
		ArrayList<Integer> arr = new ArrayList<Integer>(FETCHCOUNT);
		
		int duplicatecount = 0;
		int uniquecount = 0;
		
		for(int page=0; page<(FETCHCOUNT/10); page++) {
			WebElement answerform = driver.findElement(By.xpath("//ul[@class='list-unstyled']"));
			List<WebElement> elements = answerform.findElements(By.xpath("./child::*"));
			
			if(elements.size()==10) {
				System.out.println("All 10 questions found on page: "+(page+1));
			}
			else {
				System.out.println("Questions missing on page: "+(page+1));
			}
			
			for(WebElement i:elements) {
				String id = i.getAttribute("id");
				int identifier = Integer.valueOf(id);
				if(arr.contains(identifier)) {
					duplicatecount += 1;
					continue;
				}
				else {
					arr.add(identifier);
					uniquecount += 1;
				}
				
				String question = i.findElement(By.xpath(".//strong")).getText();
				String output = question;
				
				run.addBreak();
				run.setText("Q). "+output);
				run.addBreak();
				
				List<WebElement> options = i.findElements(By.xpath(".//ol/child::*"));
			      
				for(WebElement j:options) {
					if(j.getAttribute("class").equals("green ")) {
						output = j.getText();
												
//						run.setBold(true);
						run.setText(output+" (Answer)");
						run.addBreak();
					}
					else {
						output = j.getText();
												
						run.setText(output);
						run.addBreak();
					}
				}
			}
			
			driver.findElement(By.xpath("//div[@id='display-answers']//a[@class='next-test'][normalize-space()='Next Test']")).click();
			driver.findElement(By.id("btn-finish")).click();
		}
	document.write(out);
	driver.close();
	out.close();	
	document.close();
	
	System.out.println("Duplicates: "+duplicatecount);
	System.out.println("Uniques: "+uniquecount);
	}
}

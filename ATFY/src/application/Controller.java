package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class Controller implements Initializable{
	
	private String cwd = "", grammarName = "", textInput = "", ruleName = "";
	private ArrayList<String> previousCommand;
	private int curCommandIndex = 0;
	private Runtime rt = Runtime.getRuntime();
	
	private boolean isCwdSet = false, isGrammarSet = false, isInputSet = false;
	
	@FXML private AnchorPane apBase;
	
	@FXML private Button btnCompileG;
	@FXML private Button btnCompileJ;
	@FXML private Button btnGui;
	@FXML private Button btnTree;
	@FXML private Button btnSubmit;
	@FXML private Button btnClear;
	@FXML private Button btnSetting;
	
	@FXML private Label lblCWD;
	@FXML private Label lblGrammarName;
	@FXML private Label lblFileName;
	@FXML private Label lblDescCWD;
	@FXML private Label lblDescG;
	@FXML private Label lblDescF;
	@FXML private Label lblDescR;
	@FXML private Label lblRule;
	@FXML private Label lblRuleValidator;
	@FXML private Label cwdValidator;
	@FXML private Label grammarValidator;
	@FXML private Label filenameValidator;
	@FXML private Label lblReport;
	
	@FXML private TextArea taConsole;
	
	@FXML private TextField txtCwd;
	@FXML private TextField txtGrammar;
	@FXML private TextField txtRule;
	@FXML private TextField txtFileName;
	@FXML private TextField txtCommand;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		previousCommand = new ArrayList<>(50);
		taConsole.setText(">>> Initializing Console...\n>>> ");
		
		txtCommand.setOnKeyPressed(new EventHandler<KeyEvent>() {

			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) submitToRuntime();
				else if(event.getCode() == KeyCode.UP) getPreviousCommand();
				else if (event.getCode() == KeyCode.DOWN) getNextCommand();
			}
		});
		
		
	}
	
	
	@FXML public boolean isCwdValid()
	{
		getData();
		File pathCwd = new File(cwd);
		
		if (pathCwd.isDirectory() && !pathCwd.isFile())
		{
			cwdValidator.setText("Path is valid");
			cwdValidator.setStyle("-fx-text-fill: green;");
			if (!txtGrammar.getText().trim().equals(""))
				isGrammarValid();
			if (!txtFileName.getText().trim().equals(""))
				isInputValid();
			return true;
		}
		else
		{
			cwdValidator.setText("Path is invalid !");
			cwdValidator.setStyle("-fx-text-fill: red;");
			if (!txtGrammar.getText().trim().equals(""))
				isGrammarValid();
			if (!txtFileName.getText().trim().equals(""))
				isInputValid();
			return false;
		}	
	}
	
	@FXML public boolean isGrammarValid()
	{
		getData();
		File pathGram = new File(cwd+ "\\" + grammarName);
		
		if (pathGram.exists() && pathGram.isFile() && pathGram.getPath().matches(".+\\.g4$") )
		{
			grammarValidator.setText("Grammar is valid !");
			grammarValidator.setStyle("-fx-text-fill: green;");
			grammarDynamicValidator();
			return true;
		}
		else
		{
			grammarValidator.setText("Grammar is invalid !");
			grammarValidator.setStyle("-fx-text-fill: red;");
			grammarDynamicValidator();
			return false;
		}
	}
	
	@FXML public boolean isInputValid()
	{
		getData();
		File pathInput = new File(cwd + "\\" + txtFileName.getText());
		
		if (pathInput.exists() && pathInput.isFile() && pathInput.getPath().matches(".+\\.txt$"))
		{
			filenameValidator.setText("Input is valid");
			filenameValidator.setStyle("-fx-text-fill: green;");
			inputDynamicValidator();
			return true;
		}
		else
		{
			filenameValidator.setText("Input is invalid !");
			filenameValidator.setStyle("-fx-text-fill: red;");
			inputDynamicValidator();
			return false;
		}
		
	}
	
	@FXML public void grammarDynamicValidator() 
	{
		
		File pathCwd = new File(txtCwd.getText());
		File pathGram = new File(pathCwd.getPath() + "\\" + txtGrammar.getText());
			
		if (pathGram.exists() && pathGram.isFile() && pathGram.getPath().matches(".+\\.g4"))
		{
			grammarValidator.setText("Grammar is valid");
			grammarValidator.setStyle("-fx-text-fill: green;");
		}
		else
		{
			grammarValidator.setText("Grammar is invalid !");
			grammarValidator.setStyle("-fx-text-fill: red;");
		}
			
	}
	
	@FXML public void inputDynamicValidator() 
	{
		
		File pathCwd = new File(txtCwd.getText());
		File pathInput = new File(pathCwd.getPath() + "\\" + txtFileName.getText());
		
		if (pathInput.exists() && pathInput.isFile()  && pathInput.getPath().matches(".+\\.txt"))
		{
			filenameValidator.setText("Input is valid");
			filenameValidator.setStyle("-fx-text-fill: green;");
		}
		else
		{
			filenameValidator.setText("Input is invalid !");
			filenameValidator.setStyle("-fx-text-fill: red;");
		}
	}
	
	
	@FXML public void getData()
	{
		cwd = txtCwd.getText();
		grammarName = txtGrammar.getText();
		ruleName = txtRule.getText();	
		textInput = txtFileName.getText();			
		
	}
	
	@FXML public void compileGrammar()
	{	
		if (!isCwdValid() || !isGrammarValid())
		{
			lblReport.setText("Error: Field(s) are invalid, Please check your input !");
		}
		else
		{
			try
			{
				rt.exec("cmd.exe /c antlr4 " + grammarName, null, new File(cwd));
				lblReport.setText("No error occured :)");
			} 
			catch (Exception e)
			{
				e.printStackTrace();
				lblReport.setText("Error: exception ocurred");
			}
		}
	}
	
	@FXML public void compileJavaFile()
	{
		if (!isCwdValid() || !isGrammarValid())
		{
			lblReport.setText("Error: Field(s) are invalid, or java file(s) are not exist !");
		}
		else
		{
			try
			{
				rt.exec("cmd.exe /c javac " + grammarName.substring(0, grammarName.length()-3) + "*.java", null, new File(cwd));
				lblReport.setText("No error occured :)");
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				lblReport.setText("Error: exception occured");
			}
		}
		
	}
	
	@FXML public void openGui()
	{
		if (!isCwdValid() || !isGrammarValid() || !isInputValid())
		{
			lblReport.setText("Error: Field(s) are invalid, or input file(s) are not exist !");
		}
		else
		{
			try
			{
				rt.exec("cmd.exe /c grun " + grammarName.substring(0, grammarName.length()-3) + " prog -gui " + textInput, null, new File(cwd));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@FXML public void openTree()
	{
		if (!isCwdValid() || !isGrammarValid() || !isInputValid())
		{
			lblReport.setText("Error: Field(s) are invalid, or input file(s) are not exist !");
		}
		else
		{
			try
			{
				rt.exec("cmd.exe /c grun " + grammarName.substring(0, grammarName.length()-3) + " prog -tree" + textInput, null, new File(cwd));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@FXML public void clearConsole()
	{
		taConsole.setText(">>> Console cleared, previous on going activity is cancelled...\n>>> ");
	}
	
	@FXML public void onConsoleClicked()
	{
		txtCommand.requestFocus();
	}
	
	@FXML public void apBaseFocus()
	{
		apBase.requestFocus();
	}
	
	@FXML public void setPreviousCommand()
	{
		if (curCommandIndex >= 50)
		{
			previousCommand.remove(0);
			previousCommand.add(txtCommand.getText());
			curCommandIndex = 49;
		
		} 
		else if (curCommandIndex < 0)
		{
			curCommandIndex = 0;
		} 
		else 
		{
			previousCommand.add(txtCommand.getText());
			curCommandIndex++;
		}
	}
	
	@FXML public void getPreviousCommand()
	{
		curCommandIndex--;
		if (curCommandIndex < 0 && !previousCommand.isEmpty()) 
		{ 
			curCommandIndex = 0;
			txtCommand.setText(previousCommand.get(curCommandIndex));
		}
		else if (curCommandIndex < 0 && previousCommand.isEmpty())
		{
			curCommandIndex = 0;
		}
		else if (curCommandIndex == 0 && previousCommand.isEmpty())
		{
			txtCommand.setText("");
		}
		else
		{
			txtCommand.setText(previousCommand.get(curCommandIndex));
		}
	}
	
	@FXML public void getNextCommand()
	{
		curCommandIndex++;
		
		if (curCommandIndex < previousCommand.size())
		{
			txtCommand.setText(previousCommand.get(curCommandIndex));
		}
		else if (previousCommand.size() == 0)
		{
			curCommandIndex--;
			txtCommand.setText("");
		}
		else if (curCommandIndex >= previousCommand.size())
		{
			curCommandIndex--;
			txtCommand.setText(previousCommand.get(curCommandIndex));
		}
	}
	
	
	@FXML public void submitToRuntime()
	{
		Process proc = null;
		String stdFromCMD, str = "";
		String tempCmd = txtCommand.getText().trim(), tempCons = taConsole.getText();
		
		taConsole.setText(tempCons + txtCommand.getText().trim());
		setPreviousCommand();
		
		if (tempCmd.matches("^cwd .+"))
		{
			
			if (new File(tempCmd.substring(4)).isDirectory())
			{
				taConsole.setText(tempCons + "\ndirectory is valid and setted.\n>>> ");
				txtCwd.setText(tempCmd.substring(4));
				isCwdValid();
				isCwdSet = true;
			} else
			{
				taConsole.setText(tempCons + "\ndirectory is invalid and not setted, if any, previously setted cwd will be current cwd\n>>> ");
			}
			
		}
		else if (tempCmd.matches("^help"))
		{
			String help = "help												: show list of description for all command.\n" +		
						  "cwd <your directory here>						: set your current working directory.\n" +
						  "javac <your grammar name without extension>*.java: compile your java file into class file.\n" +
						  "show <your rule> [gui|tree] <your input file>	: show the tree or gui representation for your input according to the grammar.\n" +
						  "clear											: clear the console\n\n" +
						  "note that compile g and show command are exist just in case you haven't\n make the bat file either for Tool or TestRig.\n" +
						  "if you already set the bat file, you can call compile g or show command  as the bat file name and still allowed to use\n" +
						  "compile g and show command.\n>>> ";
						
			taConsole.setText(tempCons + "\n" + help);
		}
		else if (tempCmd.matches("^clear"))
		{
			clearConsole();
		}
		else 
		{
			if (isCwdValid())
			{
				try {
					proc = rt.exec("cmd.exe /c " + tempCmd, null, new File(cwd));
				} catch (IOException e) { e.printStackTrace(); }
				
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
				BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
				
				try {
					while ((stdFromCMD = stdInput.readLine()) != null)
					{
						str = stdFromCMD;
					}
					
					if (str.trim() != "")
					{
						taConsole.setText(tempCons + txtCommand.getText().trim() +"\n" + str + "\n>>> ");						
					}
					
					while ((stdFromCMD = stdError.readLine()) != null)
					{
						str = stdFromCMD;
					}
					
					if (str.trim() != "")
					{
						taConsole.setText(tempCons + txtCommand.getText().trim() +"\n" + str + "\n>>> ");
					}
					
					} catch (IOException e1) {
					e1.printStackTrace();
					}
			}
			else
			{
				taConsole.setText(tempCons + "\ndirectory is invalid, set your directory first before use this console.\n>>> ");
			}
			
		}
		txtCommand.setText("");
	}
	
	
	
	
	
	
	
	
	
}
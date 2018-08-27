package application;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

public class Controller implements Initializable{
	
	private String cwd, grammarName, textInput, ruleName;
	private ArrayList<String> previousCommand;
	private int curCommandIndex = 0;
	private Runtime rt = Runtime.getRuntime();
	
	@FXML private AnchorPane apBase;
	
	@FXML private Button btnCompileG;
	@FXML private Button btnCompileJ;
	@FXML private Button btnGui;
	@FXML private Button btnTree;
	@FXML private Button btnSubmit;
	@FXML private Button btnClear;

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
		taConsole.setText(">>> Initializing Console...");
		
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
			isGrammarValid();
			isInputValid();
			return true;
		}
		else
		{
			cwdValidator.setText("Path is invalid !");
			cwdValidator.setStyle("-fx-text-fill: red;");
			isGrammarValid();
			isInputValid();
			return false;
		}	
	}
	
	@FXML public boolean isGrammarValid()
	{
		getData();
		File pathCwd = new File(cwd);
		File pathGram = new File(cwd + "\\" + grammarName);
		
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
	
	@FXML public void openGui()
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
	
	@FXML public void openTree()
	{
		try
		{
			rt.exec("cmd.exe /c grun " + grammarName.substring(0, grammarName.length()-3) + " prog -tree" + textInput, null, new File(cwd));
		}
		catch (Exception e)
		{
			
		}
	}
	
	@FXML public void clearConsole()
	{
		taConsole.setText(">>> Console cleared, previous activity is cancelled....");
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
		System.out.println(curCommandIndex);
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
		System.out.println(curCommandIndex);
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
			txtCommand.setText(previousCommand.get(curCommandIndex));
		}
		else if (curCommandIndex >= previousCommand.size())
		{
			curCommandIndex--;
			txtCommand.setText(previousCommand.get(curCommandIndex));
		}
		System.out.println(curCommandIndex);
	}
	
	@FXML public void submitToRuntime()
	{
		taConsole.setText(taConsole.getText() + "\n>>> "+ txtCommand.getText());
		setPreviousCommand();
		txtCommand.setText("");
		
	}
	
	
	
	
}
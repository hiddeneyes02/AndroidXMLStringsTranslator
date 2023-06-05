package com.dzboot.android_translator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainController implements Initializable {

   @FXML private CheckBox toFile;
   @FXML private TextField langCode;
   @FXML private Label originLabel;
   @FXML private Label destLabel;
   @FXML private CheckBox includeRoot;
   @FXML private TextArea origin;
   @FXML private TextArea destination;

   private final Map<String, String> strings = new LinkedHashMap<>();


   @Override
   public void initialize(URL url, ResourceBundle resourceBundle) {
      langCode.lengthProperty().addListener((observable, oldValue, newValue) -> {
         String text = langCode.getText();
         if (newValue.intValue() > oldValue.intValue() && text.length() > 2)
            langCode.setText(text.substring(0, 2));
      });
   }

   public void pasteToOrigin(ActionEvent event) {
      Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this);
      if (contents == null)
         return;
      try {
         origin.setText((String) contents.getTransferData(DataFlavor.stringFlavor));
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void prepare(ActionEvent event) {
      destLabel.setText("");
      String xml = origin.getText();
      strings.clear();
      Pattern pattern = Pattern.compile("<string name=\"(.*)\">(.*)</string>");
      Matcher matcher = pattern.matcher(xml);

      while (matcher.find()) {
         strings.put(matcher.group(1), matcher.group(2));
      }

      StringBuilder sb = new StringBuilder();
      for (Map.Entry<String, String> stringEntry : strings.entrySet())
         sb.append(stringEntry.getValue()).append(" || ");

      StringSelection stringSelection = new StringSelection(sb.toString());
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      clipboard.setContents(stringSelection, null);
      originLabel.setText("Prepared text and copied to clipboard. Use Google Translate to translate it");
   }

   public void pasteToDest(ActionEvent event) {
      Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this);
      if (contents == null)
         return;
      try {
         destination.setText((String) contents.getTransferData(DataFlavor.stringFlavor));
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void convert(ActionEvent event) {
      originLabel.setText("");
      String[] translated = destination.getText().split(Pattern.quote("||"));
      int i = 0;
      StringBuilder sb = new StringBuilder();

      for (Map.Entry<String, String> stringEntry : strings.entrySet()) {
         sb.append("<string name=\"")
           .append(stringEntry.getKey())
           .append("\">")
           .append(stringConditioning(translated[i++]))
           .append("</string>\n");
      }

      if (toFile.isSelected()) {
         sb.insert(0, "<?xml version=\"1.0\"encoding=\"utf-8\"?>\n<resources>\n");
         sb.append("\n</resources>");
         String code = langCode.getText();
         String pathname = code.isBlank() ? "values" : "values-" + code;
         new File(pathname).mkdir();
         try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathname + "\\strings.xml"))) {
            //            new File(pathname, "strings.xml").createNewFile();
            writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n" + sb.toString());
         } catch (IOException e) {
            e.printStackTrace();
         }
      } else {
         if (includeRoot.isSelected()) {
            sb.insert(0, "<?xml version=\"1.0\"encoding=\"utf-8\"?>\n<resources>\n");
            sb.append("</resources>");
         }

         StringSelection stringSelection = new StringSelection(sb.toString());
         Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
         clipboard.setContents(stringSelection, null);
         destLabel.setText("Converted text copied to clipboard. Paste it in Android Studio");
      }
   }

   private String stringConditioning( String string) {
      Pattern pattern = Pattern.compile("[%٪％]\\s(\\d+)\\s\\$\\s([ds])");
      String result =
            Pattern.compile("([%٪％]\\s\\d+\\s\\$\\s[ds])")
                   .matcher(string.trim())
                   .replaceAll(matchResult -> {
                      Matcher matcher2 = pattern.matcher(matchResult.group());

                      if (matcher2.find())
                         return "%" + matcher2.group(1) + "\\$" + matcher2.group(2);

                      return "";
                   });

      return result.replaceAll("'", "\\\\'")
                   .replaceAll("\"", "\\\\\"");
   }

   public void reset(ActionEvent event) {
      originLabel.setText("");
      destLabel.setText("");
      strings.clear();
      origin.setText("");
      destination.setText("");
   }

   public void toggleToFileTextArea(ActionEvent event) {
      boolean convertToFile = toFile.isSelected();
      langCode.setEditable(convertToFile);
      includeRoot.setDisable(convertToFile);
      if (convertToFile)
         includeRoot.setSelected(true);
   }
}

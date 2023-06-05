module com.dzboot.android_translator {
    requires javafx.controls;
    requires java.desktop;
    requires javafx.fxml;
    requires kotlin.stdlib;
    
   requires org.controlsfx.controls;
   requires net.synedra.validatorfx;
   requires org.kordamp.ikonli.javafx;
                
    opens com.dzboot.android_translator to javafx.fxml;
    exports com.dzboot.android_translator;
}
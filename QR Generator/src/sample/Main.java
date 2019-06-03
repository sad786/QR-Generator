package sample;

import javafx.application.Application;
import javafx.scene.paint.Color;
import javafx.scene.Scene;
import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXButton.ButtonType;
import com.jfoenix.controls.JFXSlider;

import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

public class Main extends Application {

    private String data = "";
    private QRCode qr = null;
    @Override
    public void start(Stage primaryStage) throws Exception{
        var label = new Label("Enter Data Here");
        label.setFont(Font.font(15));
        var width_label = new Label("Width:");
        width_label.setFont(Font.font(15));

        var height_label = new Label("Height");
        height_label.setFont(Font.font(15));

        var error_label = new Label("");
        error_label.setFont(Font.font(15));
        error_label.setTextFill(Color.BLACK);

        var textArea = new TextArea();
        textArea.setMaxHeight(Double.MAX_VALUE);

        var clear_btn = new JFXButton("Clear");
        clear_btn.setFont(Font.font(20));
        clear_btn.setButtonType(ButtonType.RAISED);
        clear_btn.setTextFill(Color.WHITE);
        clear_btn.setRipplerFill(Color.DARKSEAGREEN);
        clear_btn.setStyle("-fx-background-color:#2E8B57");

        var gen_btn = new JFXButton("Generate");
        gen_btn.setFont(Font.font(20));
        gen_btn.setButtonType(ButtonType.RAISED);
        gen_btn.setTextFill(Color.WHITE);
        gen_btn.setRipplerFill(Color.DARKSEAGREEN);
        gen_btn.setStyle("-fx-background-color:#2E8B57");

        var pre_label = new Label("You will see Preview here");
        pre_label.setFont(Font.font(15));

        var imageView = new ImageView();
        imageView.setFitHeight(100.0D);
        imageView.setFitWidth(100.0D);

        var width_slider = new JFXSlider(100,600,100);
        width_slider.setDisable(true);
        var height_slider = new JFXSlider(100,600,100);
        height_slider.setDisable(true);
        imageView.fitHeightProperty().bind(height_slider.valueProperty());
        imageView.fitWidthProperty().bind(width_slider.valueProperty());

        var save_btn = new JFXButton("Save");
        save_btn.setFont(Font.font(20));
        save_btn.setButtonType(ButtonType.RAISED);
        save_btn.setRipplerFill(Color.DARKSEAGREEN);
        save_btn.setTextFill(Color.WHITE);
        save_btn.setStyle("-fx-background-color:#2E8B57");
        save_btn.setDisable(true);

        save_btn.setOnAction(e ->{
            var save_file = new FileChooser();
            save_file.setTitle("Save File ");
            save_file.setInitialDirectory(new File("."));
            save_file.getExtensionFilters().addAll( new FileChooser.ExtensionFilter("JPG","*.jpg"),
                                                    new FileChooser.ExtensionFilter("PNG","*.png"),
                                                    new FileChooser.ExtensionFilter("GIF","*.gif"));
            save_file.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("JPG","*.jpg"));
            var file = save_file.showSaveDialog(primaryStage);

            if(file!=null)
            {
                qr = qr.withSize((int)width_slider.getValue(),(int)height_slider.getValue());
                var des = save_file.getSelectedExtensionFilter().getDescription();
                var res = saveImage(des,file);
                if(res) {
                    error_label.setText("Saved Successfully...");
                }
            }
        });

        // qr code generator code is here
        gen_btn.setOnAction(e ->{
            data = textArea.getText();
            if(!data.equals(""))
            {
                qr = QRCode.from(data).to(ImageType.JPG).withSize(100,100);
                var inputStream = new ByteArrayInputStream(qr.stream().toByteArray());
                var image = new Image(inputStream,100.0D,100.0D,true,true);
                imageView.setImage(image);
                save_btn.setDisable(false);
                width_slider.setDisable(false);
                height_slider.setDisable(false);
            }
        });

        var btn_box = new HBox(10,gen_btn,clear_btn);
        var gen_box = new VBox(10,label,textArea,btn_box);
        VBox.setVgrow(textArea,Priority.ALWAYS);

        var width_box = new HBox(10,width_label,width_slider);
        var height_box = new HBox(10,height_label,height_slider);

        var pre_box = new VBox(10,pre_label,imageView,width_box,height_box,save_btn);
        VBox.setVgrow(imageView,Priority.ALWAYS);

        var hbox = new HBox(20.0D,gen_box,pre_box);
        hbox.setPadding(new Insets(10.0));

        var scene = new Scene(hbox,710,400);
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(400.0D);
        primaryStage.setMinWidth(710.0D);
        primaryStage.minHeightProperty().bind(height_slider.valueProperty().add(300));
        primaryStage.minWidthProperty().bind(width_slider.valueProperty().add(610));
        primaryStage.setTitle("QR Code Generator");
       // primaryStage.getIcons().add(new Image("https://cdn3.iconfinder.com/data/icons/business/16/qr_code-512.png",true));
        primaryStage.show();
    }

    private boolean saveImage(String desc,File file)
    {
        boolean flag = true;
        try {
            var fos = new FileOutputStream(file);
            switch (desc) {
                case "JPG":
                    qr.to(ImageType.JPG).writeTo(fos);
                    fos.flush();
                    fos.close();
                    break;
                case "PNG":
                    qr.to(ImageType.PNG).writeTo(fos);
                    fos.flush();
                    fos.close();
                    break;
                case "GIF":
                    qr.to(ImageType.GIF).writeTo(fos);
                    fos.flush();
                    fos.close();
                    break;
                default:
                    flag = false;
            }
        }catch(Exception e){flag = false;}
        return flag;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

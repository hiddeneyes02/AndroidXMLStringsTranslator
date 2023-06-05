package com.dzboot.android_translator

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

//test
class MainApp : Application() {
    private var mainController: MainController? = null

    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {
        val loader = FXMLLoader(MainApp::class.java.getResource("main.fxml"))
        val root = loader.load<Parent>()
        mainController = loader.getController()
        val scene = Scene(root)
        primaryStage.scene = scene
        primaryStage.title = "Translator (Manuel)"
        primaryStage.isMaximized = false
        primaryStage.isResizable = false
        primaryStage.show()
    }
}

fun main(args: Array<String>) {
    Application.launch(*args)
}


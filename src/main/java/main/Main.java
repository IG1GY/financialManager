package main;

/*
    this is a simple javafx applicatiom for managingg projects and finances. it uses xml and mysql for data storage.
    it has been built with the hackable ATOM ide which I recommend. the packages I used were the openSdk and openFx libraries.
    thus, it is mostly open source. later I'll add more functionality to atom for those that wish to use javafx for cross platform development.
    I recommend installing ide-java and lint-java. ide-java has a problem, as it doesn't allow for me to add classpaths or modules,
    this has been irritating for development, so I'll probably add that functionality as it is open source. however, It'll take a while.

    also, atom doesn't have a package to implement scene builder inside it. I can probably make such a package. this is why open source is best btw.
    so, asides from managing projects, I'll also add a tool that would allow you to more easily document programs.
    I'll automatically change the code into a viewable flowchart with different documentation showing on click.
    I have a few ideas to add to javafx.

    I think it'll be nice to add more functionality to javafx. it should be possible for us to add elements relative to other elements borders
    without too much trouble. also, adding a scripting language like javascript or python is another good idea.
    anyways, adding something relative to something else would allow us to add constraints just like we have them in android studio.
    I liked those alot.
    we need one tool for all UI platforms. I'll add some automating features since
    I think most people would find it comfortable to add minimal code to set and edit responsive UI on javafx.
    java is also set to be multiplatform with it's runtime environment and all, so I think java is all too perfect for this.

    later I would add a controlable web browser you could use alongside ATOM. it'll also be open source, written in python. I think it'll use the gecko driver alongside
    some of chrome's functionality. the thing is that it'll be *hackable* - which is important since everything hackable and opensource works 100 times better.

    problems are fixed more easily, since you can add personal functionality and therefor make more interesting and better applications. those personal features could be shared
    to make all similar problems automatically solved. with an open standart and documentation, opensource can basically do everything and anything you want.
    also, security is almost bulletproof. not having it as opensource just means mistakes will linger and less people will try to hack it. that means that when someone truly tries to hack
    it they'll succeed. which is bad. in order to hack open source you basically have to find a strong enough breach that hasn't been discovered yet by the community.
    almost everyone in the community would be fighting against you, since it's their interest that their application will be safe.
    opensource allows you to manage your own security as well.
    not only it works better, also, no one would spy and you. that's always nice.
    the only issue here is that  there is no financial incentive for making and editing opensource applications.
    however, that can be easily fixed with decentralized currency and a good enough app\website.
    of course I'm going to make something like that in the future. I'm researching AI at the moment, as soon as I finish comprehensively understanding it (decetenralized currency)
    I'll make it. there's a decentralized and opensource media sharing apps as well, I'll build an app that would use that library and replace youtube. their app is great and all,
    but it doesn't use the responsive multiplatform app standart which I'll make with some effort. (remat for short).
    also, it doesn't have an AI managing it's suggestion and search algorithms like google has (which is why so many people use youtube instead).
*/

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;

import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.Map;
import java.io.IOException;
import java.io.File;

import java.util.Scanner;

public class Main extends Application{

    public static boolean DEBUG = true;

    @Override
    public void start(Stage stage) throws Exception{

        //load fxml and create basic scene
        final int width = 300 * 4;
        final int height = 450 * 2;

        stage.setTitle("hello mofos");

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/financialManager.fxml"));

        if(getClass().getClassLoader().getResource("fxml/financialManager.fxml") == null){ System.out.println("the resource is not found"); return; }
        XML xml = new XML("records.xml");

        Parent root = loader.load();
        Scene scene = new Scene(root, width, height);

        Map<String, Object> mapper = loader.getNamespace();
        AnchorPane pane = (AnchorPane) mapper.get("splitpane1_anchorpane");

        if(pane != null)
            SplitPane.setResizableWithParent(pane, false);
        else
            System.out.println("it's null!");

        //enable the economic report button
        Button btn = (Button) mapper.get("economicReport");

        btn.setOnMouseClicked((event) -> {

            try{

                FXMLLoader loader2 = new FXMLLoader(getClass().getClassLoader().getResource("fxml/alert.fxml"));
                Alert alert = new Alert(loader2);
                
                while(!alert.didFinish) ; //do nothing
                if(alert.canceled) return;
                System.out.println(alert.toString());

                if(getClass().getClassLoader().getResource("xml/records.xml") == null) System.out.println("xml resource not found");
                xml.addTransaction(alert.toTransaction());
            }
            catch(Exception e){

                e.printStackTrace();
            }
        });

        //initialize grapher module:
        Grapher grapher = new Grapher(loader, xml);
        grapher.initModule();

        /*
        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) ->
                pane.setDividerPositions(0.20219435736677116);

        stage.widthProperty().addListener(stageSizeListener);
        stage.heightProperty().addListener(stageSizeListener);
        */

        //'launch'
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args){
        launch();
    }
}


/*
    TODO:

    --was nearly
    task 1 - database

        1) make XML more generic so it'll allow for empty xml files. |done
        2) learn how to use mysql with java.                         |done
        3) create a sample database.                                 |done
        4) create a class to interact with the sample database.      |need to recreate the database, since it's no longer working.

        --advanced database and xml management:

        1) when xml reaches 1000-10000 entries, output it to the database in bulk, regularly.
        2) start using the created server for interactions. learn sql cluster to maintain more than one database.

    task 2 - graphs (financial manager)

        1) create a simple class for displaying information on a line graph |done
        2) allow for multiple graphs                                        |done
        3) link each graph to a different source, and create one automatically foreach source. |done

        *) make sure the scrollbar works right.
        *) make the enhancement bar work
        *) create a secure webclient to supervise sources (bank account bitcoint etc)


    task 3 - server

        1) make the server work with git.   |working on it
        2) link apache with django.
        3) learn angular.
        4) link apache with angular.
        5) start creating the site.

    task 4 - tasklist

        *) make a task object that saves through xml. have the program constantly maintain time.
        *) make the program go on startup, create a first build.
        *) create "task categories" for managing different type of tasks.
        *) create an android app that basically uses this code. (check whether this is reusable)
        *) the android app should have a notification system.
        *) create a calander to add on top of the tasklist, to work alongside the task list.

    task 5 - project manager

        goal -
             making projects more easy to create and manage

        for ongoing projects:
            the project manager side of the app is a list of users - groups. each person is in charge of a group
            and a list of task (linked to tasklist).
            you click on the group and that person's responsibility is displayed.

        for finished projects:
            goal -
                easier, obvious, documentation that's clearly interactable through the machine. allows for clear autocompletion & development.

*/

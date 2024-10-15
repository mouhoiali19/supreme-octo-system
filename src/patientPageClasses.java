import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;


class ConsultPatient{
  public static Scene scene  ;

  public static void display(){

    Button returnButton = new Button("<-") ;
    returnButton.setFont(PageHandler.font);
    returnButton.setOnAction(action -> {

      PageHandler.primaryStage.setTitle("page principale");
      PageHandler.primaryStage.setScene(HomePage.scene);

    });
    returnButton.setAlignment(Pos.TOP_LEFT);

    HBox firstLine = new HBox() ;
    firstLine.getChildren().add(returnButton);

    VBox contenue = new VBox(20) ;
    contenue.setPadding(new Insets(20,20,50,20));

    Label rechpat = new Label("Entrez le numero de dossier de patient ");
    rechpat.setFont(PageHandler.font);
    
    TextField inputnum = new TextField();
    inputnum.setPromptText("numéro");
    inputnum.setFont(PageHandler.font);

    GridPane grid = new GridPane();
    grid.setHgap(20);
    grid.setVgap(10);
    grid.setPadding(new Insets(10,10,10,10));
    grid.add(rechpat , 0 , 0 ) ;
    grid.add(inputnum , 1 , 0 ) ;

    contenue.setAlignment(Pos.TOP_CENTER);

    EventHandler<ActionEvent> searchAction = new EventHandler<ActionEvent>() {

      public void handle(ActionEvent event){

        contenue.getChildren().clear();
        contenue.getChildren().addAll(firstLine , grid );
        
        int intValue = Integer.parseInt(inputnum.getText());
        System.out.println("Integer value entered: " + intValue);

        if(UserData.rechpatient(intValue) ==null){

          Label error = new Label("ce patient n'existe pas");
          error.setFont(PageHandler.font);
          contenue.getChildren().add(error);

        }else{  

          contenue.getChildren().add(UserData.rechpatient(intValue).afficher());
          Button listrdv = new Button("Ses rendez vous ");
          listrdv.setFont(PageHandler.font);

          Button FicheSuiv = new Button("Fiche de Suivi ");
          FicheSuiv.setFont(PageHandler.font);

          Button listBilan = new Button("Ses bilans");
          listBilan.setFont(PageHandler.font);

          GridPane grid2 = new GridPane();
          grid2.setHgap(10);
          grid2.setVgap(10);
          grid2.setPadding(new Insets(10,10,10,10));
          grid2.add(listrdv , 0 , 0 ) ;
          grid2.add(FicheSuiv , 1 , 0 ) ;
          grid2.add(listBilan, 2, 0);
          grid2.setAlignment(Pos.TOP_CENTER);
          contenue.getChildren().add(grid2);

          listrdv.setOnAction(actions ->{
            PageHandler.primaryStage.setTitle("liste des RDV");
            RDVLIST.display(intValue);
            PageHandler.primaryStage.setScene(RDVLIST.scene);
          });

          FicheSuiv.setOnAction( did ->{
            PageHandler.primaryStage.setTitle("Fiche de Suivi");
            FichSuiv.display(intValue);
            PageHandler.primaryStage.setScene(FichSuiv.scene);
          });

          listBilan.setOnAction( something ->{
            PageHandler.primaryStage.setTitle("liste des Bilans");
            LinkBilansList.display(UserData.rechpatient(intValue));
            PageHandler.primaryStage.setScene(LinkBilansList.scene);
          });
      
        }


      }

    };

    inputnum.setOnAction(searchAction);

    Button allPatients = new Button("afficher tous") ;
    allPatients.setFont(PageHandler.font);
    allPatients.setOnAction(action ->{

      ListAllPatients.display();
      PageHandler.primaryStage.setTitle("Liste des patients");
      PageHandler.primaryStage.setScene(ListAllPatients.scene);

    });

    Button search = new Button("Rechercher");
    search.setOnAction(searchAction) ;
    search.setFont(PageHandler.font);

    grid.add(search , 2 , 0);
    grid.add(allPatients, 3, 0);

    contenue.getChildren().addAll(firstLine , grid );
    scene = new Scene(contenue , PageHandler.width , PageHandler.height) ;
  }
}

class ListAllPatients{

  public static Scene scene  ;


  public static void display(){

    Button returnButton = new Button("<-") ;
    returnButton.setFont(PageHandler.font);
    returnButton.setOnAction(action -> {

      ConsultPatient.display();
      PageHandler.primaryStage.setTitle("Mes Patients");
      PageHandler.primaryStage.setScene(ConsultPatient.scene);

    });
    returnButton.setAlignment(Pos.TOP_LEFT);

    HBox firstLine = new HBox() ;

    firstLine.getChildren().add(returnButton);
    VBox content = new VBox(20);
    content.getChildren().add(firstLine);

    GridPane grid = new GridPane();
    grid.setHgap(20);
    grid.setVgap(10);
    grid.setPadding(new Insets(10,10,10,10));
    int cpt = 0 ;

    //add all Hbox affichage des patients , donc parcourir HashMap

    for (Patient patient : UserData.mapPatients.values()) {
      
      int i = cpt % 2 ;

      if(i == 0) grid.add(patient.smallAffichage(), 0 , cpt/2 ) ; 
      else grid.add(patient.smallAffichage(), 1 , cpt/2 ) ;

      cpt ++ ;

    }

    grid.setAlignment(Pos.CENTER);
    content.getChildren().add(grid);

    ScrollPane listView = new ScrollPane(content) ;
    listView.setPadding(new Insets(20 ,40 ,30,40));
    listView.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Vertical scroll bar
    listView.setFitToWidth(true); //to center the textField elements
    listView.setFitToHeight(true);

    scene = new Scene(listView , PageHandler.width , PageHandler.height) ;
  }

}

class LinkBilansList{

  public static Scene scene ;

  public static void display(Patient patient){

    Button returnButton = new Button("<-") ;
    returnButton.setFont(PageHandler.font);
    returnButton.setOnAction(action -> {

      PageHandler.primaryStage.setTitle("Mes Patients");
      PageHandler.primaryStage.setScene(ConsultPatient.scene);

    });
    returnButton.setAlignment(Pos.TOP_LEFT);

    HBox firstLine = new HBox() ;

    firstLine.getChildren().add(returnButton);
    VBox content = new VBox(20) ;
    content.getChildren().add(firstLine);
    int cpt = 1 ;

    for (BilanOrtho bilan : patient.getListBilan() ) {
      
      Label linkBilan = new Label("Bilan N°" + cpt) ;
      linkBilan.setFont(PageHandler.font);
      linkBilan.setTextFill(Color.BLUE) ;
      //add a CSS style to make it look like a hyperlink
      linkBilan.setStyle("-fx-cursor: hand; -fx-underline: true;");

      linkBilan.addEventHandler(MouseEvent.MOUSE_CLICKED, action -> {

        BilanPage.display(bilan);
        PageHandler.primaryStage.setTitle("Bilan");
        PageHandler.primaryStage.setScene(BilanPage.scene);

      });

      content.getChildren().add(linkBilan);

      cpt++ ;
    }

    content.setAlignment(Pos.TOP_CENTER);

    ScrollPane listView = new ScrollPane(content) ;
    listView.setPadding(new Insets(20 ,40 ,30,40));
    listView.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Vertical scroll bar
    listView.setFitToWidth(true); //to center the textField elements
    listView.setFitToHeight(true);

    scene = new Scene(listView , PageHandler.width , PageHandler.height) ;

  }

}

class BilanPage{

  public static Scene scene ;

  public static void display(BilanOrtho bilan){

    Button returnButton = new Button("<-") ;
    returnButton.setFont(PageHandler.font);
    returnButton.setOnAction(action -> {

      PageHandler.primaryStage.setTitle("Liste des bilans");
      PageHandler.primaryStage.setScene(LinkBilansList.scene);

    });
    returnButton.setAlignment(Pos.TOP_LEFT);

    HBox firstLine = new HBox() ;

    firstLine.getChildren().add(returnButton);
    VBox content = new VBox(20);
    content.setStyle("-fx-background-color:white;");
    content.setPadding(new Insets(10));
    content.getChildren().add(firstLine);

    if(bilan.isFirst()){

      Label titreAna = new Label("----------------------- ANAMNESE -----------------------");
      titreAna.setFont(PageHandler.font);

      content.getChildren().addAll(titreAna , ((FirstBilan) bilan).getWidgetAna());

    }

    Label titreDia = new Label("----------------------- DIAGNOSTICS -----------------------");
    titreDia.setFont(PageHandler.font);

    Label titrePrjt = new Label("----------------------- PROJET THERAPEUTIQUE -----------------------");
    titrePrjt.setFont(PageHandler.font);

    content.getChildren().addAll(titreDia , bilan.getWidgetDiagno() , titrePrjt , bilan.getWidgetPrjpt());
    content.setAlignment(Pos.TOP_CENTER);

    ScrollPane listView = new ScrollPane(content) ;
    listView.setPadding(new Insets(20 ,40 ,30,40));
    listView.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Vertical scroll bar
    listView.setFitToWidth(true); //to center the textField elements
    listView.setFitToHeight(true);

    scene = new Scene(listView , PageHandler.width , PageHandler.height) ;

  }

}

class RDVLIST{

  public static Scene scene ;

  public static void display(Integer x){

    Button returnButton = new Button("<-") ;
    returnButton.setFont(PageHandler.font);
    returnButton.setOnAction(action -> {

      ConsultPatient.display();
      PageHandler.primaryStage.setTitle("Mes Patients");
      PageHandler.primaryStage.setScene(ConsultPatient.scene);

    });
    returnButton.setAlignment(Pos.TOP_LEFT);

    HBox firstLine = new HBox() ;

    firstLine.getChildren().add(returnButton);
    VBox content = new VBox(10);
    content.getChildren().add(firstLine);
    content.getChildren().add(UserData.rechpatient(x).afficherRdv());

    ScrollPane listView = new ScrollPane(content) ;
    listView.setPadding(new Insets(20 ,40 ,30,40));
    listView.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Vertical scroll bar
    listView.setFitToWidth(true); //to center the textField elements
    listView.setFitToHeight(true);

    scene = new Scene(listView , PageHandler.width , PageHandler.height) ;

  }

}

class FichSuiv{

  public static Scene scene ;
  
  public static void display(Integer x){

    Button returnButton = new Button("<-") ;

    returnButton.setFont(PageHandler.font);
    returnButton.setOnAction(action -> {

      ConsultPatient.display();
      PageHandler.primaryStage.setTitle("Mes Patients");
      PageHandler.primaryStage.setScene(ConsultPatient.scene);

    });
    
    returnButton.setAlignment(Pos.TOP_LEFT);

    HBox firstLine = new HBox() ;

    firstLine.getChildren().add(returnButton);

    VBox content = new VBox(10);

    content.getChildren().add(firstLine);
    content.getChildren().addAll(UserData.rechpatient(x).afficheFiche()); 

    Button create = new Button("creer un nouveau fiche de suivi ");
    create.setAlignment(Pos.CENTER);
    create.setFont(PageHandler.font);
    create.setOnAction(act ->{
      PageHandler.primaryStage.setTitle("creation-Fiche");
      addFich.display(x);
      PageHandler.primaryStage.setScene(addFich.scene);
    });

    content.getChildren().add(create);
    content.setPadding(new Insets(20,20,20,20));
    content.setAlignment(Pos.TOP_CENTER);

    ScrollPane listView = new ScrollPane(content) ;
    listView.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Vertical scroll bar
    listView.setFitToWidth(true); //to center the textField elements
    listView.setFitToHeight(true);

    scene = new Scene(listView , PageHandler.width , PageHandler.height) ;

  }
 
  
}

class addFich{

  public static Scene scene ;

  public static String typechoix = "Moyen terme ";
  public static StringProperty noteController ;

  public static void display(Integer x){

    VBox contenue  = new VBox(10);
    contenue.setPadding(new Insets(20,20,20,20));

    noteController = new SimpleStringProperty() ;
    Label start = new Label("La liste des objectives : ");
    start.setFont(PageHandler.font);

    Button returnButton = new Button("<-") ;
    returnButton.setFont(PageHandler.font);
    returnButton.setOnAction(action -> {

      PageHandler.primaryStage.setTitle("page des patients");
      PageHandler.primaryStage.setScene(ConsultPatient.scene);

    });
    returnButton.setAlignment(Pos.TOP_LEFT);

    HBox firstLine = new HBox() ;
    firstLine.getChildren().add(returnButton); //pour positionner le retour bouton vers la gauche

   
    List<TextField> listTextFields = new ArrayList<TextField>() ;
    List<ChoiceBox<String>> choiceBoxList = new ArrayList<>();
    ChoiceBox<String> typeobj = new ChoiceBox<>();

    typeobj.getItems().addAll("Court terme " , "Moyen terme " , "Long terme "); 
    typeobj.setStyle("-fx-font-size: 20px;");
    typeobj.setValue(typechoix); //default value
   
    typeobj.setOnAction( act ->{
      typechoix = typeobj.getValue();
    });

    choiceBoxList.add(typeobj);
    TextField textField = new TextField();
    listTextFields.add(textField);
    listTextFields.get(0).setPromptText("Objective N°1");
    listTextFields.get(0).setFont(PageHandler.font);

    
    Button addNum = new Button("+") , removeNum = new Button("-") ; //deux boutons pour ajouter nouveau champ ou bien le supprimer

    addNum.setFont(PageHandler.font);
    removeNum.setFont(PageHandler.font);

    HBox twoButtons = new HBox(5) ;
    twoButtons.getChildren().addAll(addNum , removeNum) ;

    addNum.setStyle("-fx-background-radius: 15; -fx-border-radius: 15;");
    removeNum.setStyle("-fx-background-radius: 15; -fx-border-radius: 15;");

    TextField noteTextFiel = new TextField() ;
    noteTextFiel.setPromptText("note");
    noteTextFiel.setFont(PageHandler.font);
    
    Label listobj = new Label("Entrez la liste des objectives  de ce  patient : ") ;
    Label noting = new Label("Entrez la note de cet Fiche : ") ;
    
    listobj.setFont(PageHandler.font);
    noting.setFont(PageHandler.font);

    GridPane grid = new GridPane() ;
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(10,10,10,10));
    
    grid.add(noting, 0, 0 );
    grid.add(noteTextFiel, 1, 0);
    grid.add(listobj, 0, 1);
    grid.add(listTextFields.get(0) , 1  , 1);
    grid.add(twoButtons, 2, 1);
    grid.add(choiceBoxList.get(0) , 1 , 2 );
    

    addNum.setOnAction(action -> {

      if(listTextFields.size() < 10){ //pour ajouter , on ne doit pas dépasser 10 objectifs
        
        listTextFields.add(new TextField());
        
        int length = listTextFields.size() ;
        TextField lastField = listTextFields.get(length-1);
        lastField.setPromptText("objective " + "N°" + length);
        lastField.setFont(PageHandler.font);

        ChoiceBox<String> typeobj2 = new ChoiceBox<String>();

        typeobj2.getItems().addAll("Court terme" , "Moyen terme" , "Long terme"); 
        typeobj2.setStyle("-fx-font-size: 20px;");
        typeobj2.setValue("Court terme");

        
        typeobj2.setOnAction(act->{
          
          typechoix = typeobj2.getValue();
          System.out.println(typechoix);

        });

        choiceBoxList.add(typeobj2);

        grid.add(lastField , 1  , 2*length - 1);
        grid.add(choiceBoxList.get(length-1) , 1 , 2*length );

        //System.out.println(grid.getChildren().contains(lastField));

      }

    });

    removeNum.setOnAction(action -> {

      if(listTextFields.size() >= 2){ //au moins deux pour supprimer

        grid.getChildren().remove(listTextFields.get(listTextFields.size() - 1));
        grid.getChildren().remove(choiceBoxList.get(choiceBoxList.size() - 1));

        listTextFields.remove(listTextFields.size() - 1) ;
        choiceBoxList.remove(choiceBoxList.size() - 1) ;

      }

    });
    
    grid.setAlignment(Pos.CENTER);
    
    Button ajouter = new Button("créer le Fiche de suive ");
    ajouter.setFont(PageHandler.font);

    ajouter.setOnAction(doing -> {

      FicheSuivie fichiersuivi = new FicheSuivie();
      
      System.out.println(listTextFields.size()-1);

      for(int i=0 ; i<listTextFields.size() ; i++ ){

        String str = listTextFields.get(i).getText();
        System.out.print(str.toString());
        String choi = choiceBoxList.get(i).getValue();
        System.out.println(choi);

        if (choi.equals("Court terme")){
          fichiersuivi.addObj(new Objective(str, TypeObj.courtTerme ));
        }else{
        if(choi.equals("Moyen terme ")){
          fichiersuivi.addObj(new Objective(str, TypeObj.moyenterme ));
        }else{
          fichiersuivi.addObj(new Objective(str, TypeObj.longTerme ));
        }
        }

      }

      fichiersuivi.setnote(Integer.parseInt(noteTextFiel.getText()));
      UserData.rechpatient(x).addfi(fichiersuivi);
      PageHandler.primaryStage.setTitle("Mes patients");
      PageHandler.primaryStage.setScene(ConsultPatient.scene);

    });


    

    contenue.getChildren().addAll(firstLine , grid , ajouter);
    contenue.setAlignment(Pos.TOP_CENTER);

    ScrollPane listView = new ScrollPane(contenue) ;
    listView.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Vertical scroll bar
    listView.setFitToWidth(true); //to center the textField elements
    listView.setFitToHeight(true);

    scene = new Scene(listView , PageHandler.width , PageHandler.height) ;
  }

}
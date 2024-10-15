import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.* ;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


class CreerRDV{

  public static Scene scene ;
  public static VBox dynamicVBox ;

  public static StringProperty timeController = new SimpleStringProperty() ;
  public static LocalDateTime datePicked = LocalDateTime.now()  ;
  public static String typeRDVString = "Consultation" ;

  public static StringProperty ageController , nomController , prenomController , themaController ;
  public static List<StringProperty> numPatientController = new ArrayList<StringProperty>() ; //pour la liste des textfield des duméros de patients , si c'est un suivi , l'indexe 0 est le suel numéro de patient
  public static boolean isPresentiel = false ;

  public static void initControllers(){ //initialiser des controllers

    nomController = new SimpleStringProperty() ;
    prenomController = new SimpleStringProperty() ;
    ageController = new SimpleStringProperty() ;
    themaController = new SimpleStringProperty() ;
    numPatientController.removeAll(numPatientController);
    numPatientController.add(new SimpleStringProperty()) ;

  }

  public static boolean verifyEmptiness(String typeRDV) { //vérifier si tous les entrées sont remplis

    switch (typeRDV) {

      case "Consultation":

        if(ageController.get().isEmpty() || 
        nomController.get().isEmpty() || prenomController.get().isEmpty() ) return false ;
    
      return true ;
        
      

      case "Suivi":

        if(numPatientController.get(0).get().isEmpty() ) return false ;
    
      return true ;
        
      case "Atelier":

        for (StringProperty stringProperty : numPatientController) {
          if (stringProperty.get().isEmpty()) return false ;
        }
        if(themaController.get().isEmpty()) return false ;
    
      return true ;
        
      
    
      default:
        System.out.println("Type de Rendez vous non valide");
      return false ;
      
    }


  }

  public static VBox typeRDVDetailsDisplay(String typRDV){ //créer la partie particulière de rendez vous

    numPatientController.clear();
    numPatientController.add(new SimpleStringProperty()) ;    

    VBox contenue = new VBox(20) ;
    contenue.setAlignment(Pos.CENTER);

    Label title = new Label("--------------  "+typRDV.toUpperCase().toString()+"  --------------");
    title.setFont(new Font(25));
    title.setTextFill(Color.GRAY);

    GridPane grid = new GridPane() ;
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(10,10,10,10));


    switch (typRDV) {

      case "Consultation":

        //adding elements
        TextField nomTextFiel = new TextField() ;
        nomTextFiel.setPromptText("nom");
        nomTextFiel.setFont(PageHandler.font);
        nomController.bindBidirectional(nomTextFiel.textProperty());

        TextField prenomTextField = new TextField() ;
        prenomTextField.setPromptText("prénom");
        prenomTextField.setFont(PageHandler.font);
        prenomController.bindBidirectional(prenomTextField.textProperty());

        TextField ageTextField = new TextField() ;
        ageTextField.setPromptText("age");
        ageTextField.setFont(PageHandler.font);
        ageController.bindBidirectional(ageTextField.textProperty());

        Label textNom= new Label("Entrez le nom de patient : ") ;
        Label textPre = new Label("Entrez son prénom : ") ;
        Label textAge = new Label("Entrez son age : ") ;

        textNom.setFont(PageHandler.font);
        textPre.setFont(PageHandler.font);
        textAge.setFont(PageHandler.font);

        //ranger tous dans un grid organisé
    
        grid.add(textNom, 0, 0);
        grid.add(nomTextFiel, 1, 0);
        grid.add(textPre, 0, 1);
        grid.add(prenomTextField, 1, 1);
        grid.add(textAge, 0, 2);
        grid.add(ageTextField, 1, 2);
    
        grid.setAlignment(Pos.CENTER);

        contenue.getChildren().addAll(title , grid) ;

            
      break;

      case "Suivi":

        TextField numTextFiel = new TextField() ;
        numTextFiel.setPromptText("numéro");
        numTextFiel.setFont(PageHandler.font);
        numPatientController.get(0).bindBidirectional(numTextFiel.textProperty());

        CheckBox isPresence = new CheckBox("oui") ;
        isPresence.setFont(PageHandler.font);
        isPresence.addEventHandler(MouseEvent.MOUSE_CLICKED , e -> isPresentiel = isPresence.isSelected() );

        Label textNum = new Label("Entrez le numéro de patient : ") ;
        Label textOnligne = new Label("Ce suivi est présentiel : ") ;

        textNum.setFont(PageHandler.font);
        textOnligne.setFont(PageHandler.font);

        grid.add(textNum, 0, 0);
        grid.add(numTextFiel, 1, 0);
        grid.add(textOnligne, 0, 1);
        grid.add(isPresence, 1, 1);

        grid.setAlignment(Pos.CENTER);

        contenue.getChildren().addAll(title , grid) ;

      break;

      case "Atelier":

        List<TextField> listTextFields = new ArrayList<TextField>() ;
        listTextFields.add(new TextField()) ; //premier textFiel par défaut
        listTextFields.get(0).setPromptText("numéro");
        listTextFields.get(0).setFont(PageHandler.font);
        numPatientController.get(0).bindBidirectional(listTextFields.get(0).textProperty());

        Button addNum = new Button("+") , removeNum = new Button("-") ; //deux boutons pour ajouter nouveau champ ou bien le supprimer
        addNum.setFont(PageHandler.font);
        removeNum.setFont(PageHandler.font);

        HBox twoButtons = new HBox(5) ;
        twoButtons.getChildren().addAll(addNum , removeNum) ;

        addNum.setStyle("-fx-background-radius: 15; -fx-border-radius: 15;");
        removeNum.setStyle("-fx-background-radius: 15; -fx-border-radius: 15;");

        TextField themaTextFiel = new TextField() ;
        themaTextFiel.setPromptText("nom");
        themaTextFiel.setFont(PageHandler.font);
        themaController.bindBidirectional(themaTextFiel.textProperty());

        Label textNums = new Label("Entrez la liste des numéros des patients : ") ;
        Label textThema = new Label("Entrez la thématique de l'atelier : ") ;

        textNums.setFont(PageHandler.font);
        textThema.setFont(PageHandler.font);

        grid.add(textThema, 0, 0);
        grid.add(themaTextFiel, 1, 0);
        grid.add(textNums, 0, 1);
        grid.add(listTextFields.get(0), 1, 1);
        grid.add(twoButtons, 2, 1);

        addNum.setOnAction(action -> {

          if(listTextFields.size() < 10){ //pour ajouter , on ne doit pas dépasser 10 patients

            listTextFields.add(new TextField());
            numPatientController.add(new SimpleStringProperty()) ;

            int length = listTextFields.size() ;
            TextField lastField = listTextFields.get(length-1);

            lastField.setPromptText("numéro");
            lastField.setFont(PageHandler.font);
            numPatientController.get(length - 1).bindBidirectional(lastField.textProperty());

            System.out.println(grid.getChildren().contains(lastField));

            grid.add(lastField , 1 , length);

          }

        });

        removeNum.setOnAction(action -> {

          if(listTextFields.size() >= 2){ //au moins deux pour supprimer

            grid.getChildren().remove(listTextFields.get(listTextFields.size() - 1));

            listTextFields.remove(listTextFields.size() - 1) ;
            numPatientController.remove(listTextFields.size() - 1) ;

          }

        });

        grid.setAlignment(Pos.CENTER);

        contenue.getChildren().addAll(title , grid) ;

      break;
    
      default:
        System.out.println("Type de Rendez vous non valide");
      break;

    }

    return contenue ;

  }

  public static GridPane gridDisplay(){ //élément complexe dans une méthode
 
    //adding elements
    DatePicker calendar = new DatePicker(LocalDate.now()) ;
    calendar.setOnAction(action -> datePicked = LocalDateTime.of( calendar.getValue() , datePicked.toLocalTime() ));
    calendar.setStyle("-fx-font-size: 20px;");


    TextField timeTextFiel = new TextField() ;
    timeTextFiel.setPromptText("time : XX:XX");
    timeTextFiel.setFont(PageHandler.font);
    timeController.bindBidirectional(timeTextFiel.textProperty());

    ChoiceBox<String> typeRDV = new ChoiceBox<>() ;
    typeRDV.getItems().addAll("Consultation" , "Suivi" , "Atelier") ;
    typeRDV.setValue(typeRDVString); //default value
    typeRDV.setStyle("-fx-font-size: 20px;");

    typeRDV.setOnAction(action -> {

      typeRDVString = typeRDV.getValue();

      dynamicVBox.getChildren().clear();
      dynamicVBox.getChildren().addAll(typeRDVDetailsDisplay(typeRDVString).getChildren());

    });

    Label textCal = new Label("Entrez la date de rendez-vous : ") ;
    Label textTime = new Label("Entrez le temps du jour : ") ;
    Label textType = new Label("Cet rendez-vous est un/une : ") ;

    textCal.setFont(PageHandler.font);
    textType.setFont(PageHandler.font);
    textTime.setFont(PageHandler.font);

    GridPane grid = new GridPane() ;
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(10,10,10,10));

    grid.add(textCal, 0, 0);
    grid.add(calendar, 1, 0);
    grid.add(textTime, 0, 1);
    grid.add(timeTextFiel, 1, 1);
    grid.add(textType, 0, 2);
    grid.add(typeRDV, 1, 2);

    grid.setAlignment(Pos.CENTER);

    return grid ;

  }

  public static void display(){

    initControllers();

    BorderPane mainContent = new BorderPane();

    Button creerButton = new Button("Créer le rendez-vous" ) ;
    creerButton.setFont(PageHandler.font);

    creerButton.setOnAction(action -> {

      List<Patient> patients = new ArrayList<Patient>();

      try {

        int i = 0;
        System.out.println(numPatientController.size());

        switch (typeRDVString) { //vérifier  l'intégrité des numeros de patients

          case "Consultation":

            i = Integer.parseInt(ageController.get()); //il n'y a pas numéro , mais il y a l'age

          break;
        
          default: //sinon

            for (StringProperty stringProperty : numPatientController) {
              System.out.println("go");
              i = Integer.parseInt(stringProperty.get()) ;

              patients.add(UserData.rechpatient(i)) ; //rechercher le patient et l'ajouter , s'il n'existe pas , null est ajouté
            }

          break;
          
        }

        System.out.println(i);

        String[] twoTimes = timeController.get().split(":");

        i = Integer.parseInt(twoTimes[0]) ;
        int j = Integer.parseInt(twoTimes[1]) ; //vérifier bien si entre le ':' il exist bel et bien des numéros 
  
        if(!verifyEmptiness(typeRDVString)){
  
          Label errorMessage = new Label("Création echouée , veuillez remplir tous les informations");
          errorMessage.setFont(new Font(15));
          errorMessage.setTextFill(Color.RED);
  
          mainContent.setBottom(errorMessage);        
  
        }else if(i>23 || i < 0 || j > 59 || j < 0){

          Label errorMessage = new Label("Création echouée , Ce temps n'existe pas");
          errorMessage.setFont(new Font(15));
          errorMessage.setTextFill(Color.RED);
  
          mainContent.setBottom(errorMessage);

        }else if(timeController.get().length() != 5 || twoTimes.length != 2){

          Label errorMessage = new Label("Création echouée , veuillez vérifier le format de temps");
          errorMessage.setFont(new Font(15));
          errorMessage.setTextFill(Color.RED);
  
          mainContent.setBottom(errorMessage);

        }else if(patients.contains(null)){ //numéro qui n'existe pas 

          Label errorMessage = new Label("Création echouée , numéro unique n'existe pas ( position "+ (patients.indexOf(null)+1) + " )");
          errorMessage.setFont(new Font(15));
          errorMessage.setTextFill(Color.RED);

          mainContent.setBottom(errorMessage);

        }else if(patients.size() < 3 && typeRDVString == "Atelier"){

          Label errorMessage = new Label("Création echouée , un Atelier possède au minimum 3 patients");
          errorMessage.setFont(new Font(15));
          errorMessage.setTextFill(Color.RED);
  
          mainContent.setBottom(errorMessage);

        }else { //sinon de tous , on crée le rendez-vous désiré

          if ( ConfirmationBox.display("Confirmer un rendez-vous", "Etes vous sure de créer ce rendez-vous?") ) { // fenètre popUp pour confirmer , doit cliker "oui" pour procéder

            datePicked = LocalDateTime.of(datePicked.toLocalDate(), LocalTime.of( i , j ));

            RendezVous rdv = null ;

            switch (typeRDVString) {

              case "Consultation":

                int age = Integer.parseInt(ageController.get()) ;
                Patient patient = null ;

                if(age >12){ //créer un adulte patient

                  patient = new Adulte(nomController.get(), prenomController.get());

                }else{ //créer un enfant

                  patient = new Enfant(nomController.get(), prenomController.get());

                }

                rdv = new Consulation(datePicked , patient) ; //la consultation se crée

              break;

              case "Suivi":

                Patient pat = patients.get(0); // il existe un seul patient récupéré

                rdv = new Suivi(datePicked, isPresentiel, pat);

              break;

              case "Atelier":

                rdv = new Atelier(datePicked, themaController.get(), patients);
                

              break;
            
              default:
              break;
            }

            UserData.orthophoniste.addRDV(rdv);
  
            PageHandler.primaryStage.setTitle("Page Principale");
            HomePage.display();
            PageHandler.primaryStage.setScene(HomePage.scene);

          }
  
        }
        
      } catch (NumberFormatException e) {
  
        Label errorMessage = new Label("Création echouée , veuillez entrer des données crédibles");
        errorMessage.setFont(new Font(15));
        errorMessage.setTextFill(Color.RED);
  
        mainContent.setBottom(errorMessage); 
      }

    });

    Button returnButton = new Button("<-") ;
    returnButton.setFont(PageHandler.font);
    returnButton.setOnAction(action -> {

      PageHandler.primaryStage.setTitle("page principale");
      PageHandler.primaryStage.setScene(HomePage.scene);

    });
    returnButton.setAlignment(Pos.TOP_LEFT);

    HBox firstLine = new HBox() ;
    firstLine.getChildren().add(returnButton); //pour positionner le retour bouton vers la gauche

    //creating grid Layout

    GridPane grid = gridDisplay() ; //partie générale
    dynamicVBox  = typeRDVDetailsDisplay(typeRDVString); //partie dynamique

    VBox contenue = new VBox(20) ;
    mainContent.setPadding(new Insets(20,20,50,20));

    contenue.getChildren().addAll(firstLine , grid , dynamicVBox , creerButton) ;

    contenue.setAlignment(Pos.TOP_CENTER);

    mainContent.setTop(contenue);

    ScrollPane listView = new ScrollPane(mainContent) ;
    listView.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Vertical scroll bar
    listView.setFitToWidth(true); //to center the textField elements
    listView.setFitToHeight(true);
    

    scene = new Scene(listView , PageHandler.width , PageHandler.height) ;

  }
 

}



import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

class ConsultAgenda{

  public static Scene scene ;

  public static void display(){

    Button returnButton = new Button("<-") ;
    returnButton.setFont(PageHandler.font);
    returnButton.setOnAction(action -> {

      PageHandler.primaryStage.setTitle("page principale");
      PageHandler.primaryStage.setScene(HomePage.scene);

    });
    
    returnButton.setAlignment(Pos.TOP_LEFT);

    HBox firstLine = new HBox() ;
    firstLine.getChildren().add(returnButton); //pour positionner le retour bouton vers la gauche

    VBox contenue = new VBox(20) ;
    contenue.setPadding(new Insets(20,20,50,20));

    contenue.getChildren().add(firstLine);

    if (UserData.orthophoniste.isAgendaEmpty()) {
      
      Label emptyList = new Label("Il n'existe aucun rendez vous pour le moment") ;
      emptyList.setFont(new Font(30));

      contenue.getChildren().add(emptyList) ;

    }else{

      List<VBox> vboxesRDV = new ArrayList<VBox>() ;

      for (RendezVous rdv : UserData.orthophoniste.getListRDV()) vboxesRDV.add(rdv.itsWidget(false)); //pour chaque rendez vous , an affecte à cette liste son affichage

      contenue.getChildren().addAll(vboxesRDV) ; //on ajoute cette liste

    }

    contenue.setAlignment(Pos.TOP_CENTER);

    ScrollPane listView = new ScrollPane(contenue) ;
    listView.setPadding(new Insets(20 ,40 ,30,40));
    listView.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Vertical scroll bar
    listView.setFitToWidth(true); //to center the textField elements
    listView.setFitToHeight(true);

    scene = new Scene(listView , PageHandler.width , PageHandler.height) ;

  }

}

class FinishRDV{ //page pour finir le rendezvous et donner une observation

  public static Scene scene ;
  public static RendezVous rdv ;
  public static VBox dynamicVBox ;
  public static int widthGrid = 800 ;

  public static boolean isPrisEnCharge = false ;

  public static StringProperty lieuController , addresseController ,
   professionController , diplomController , classeController ,
  numeroController , observationController ; //connaissane general

  public static StringProperty noteController , obscliniController ; //epreuve clinique

  public static StringProperty nomTroubleController ; //diagnostic
  public static String typeTroubleStr = "deglutition" ;

  public static StringProperty prjptTheraController ; //projet Thérapeutique

  public static LocalDateTime datePicked = LocalDateTime.now().minusYears(7) ;

  public static void initControllers(){

    lieuController = new SimpleStringProperty() ;
    addresseController = new SimpleStringProperty() ;
    numeroController = new SimpleStringProperty() ;
    professionController = new SimpleStringProperty() ;
    diplomController = new SimpleStringProperty() ;
    classeController = new SimpleStringProperty() ;

    observationController = new SimpleStringProperty() ;

    noteController = new SimpleStringProperty() ;
    obscliniController = new SimpleStringProperty() ;

    nomTroubleController = new SimpleStringProperty() ;

    prjptTheraController =  new SimpleStringProperty() ;

  }

  public static boolean verifyEmptiness(){

    if(rdv.typeRDV.equals("Consultation") && !isPrisEnCharge){
      if(observationController.get().isEmpty()) return false ;
      return true ;
    }

    if(prjptTheraController.get().isEmpty() || nomTroubleController.get().isEmpty() ||
    noteController.get().isEmpty() || obscliniController.get().isEmpty() || observationController.get().isEmpty() ) return false ;

    if(rdv.typeRDV.equals("Consultation")){ //si de plus , c'est une consultation , on vérifie l'anamnese et connaisance

      if( ((Consulation)rdv).isAdult() && UserData.anamneseAdults.isResponsesEmpty()) return false ;
      if( !((Consulation)rdv).isAdult() && UserData.anamneseKids.isResponsesEmpty()) return false ;

      System.out.println("yata");

      boolean specificCase = ((Consulation)rdv).isAdult() ? professionController.get().isEmpty() || diplomController.get().isEmpty() : classeController.get().isEmpty() ;

      if(lieuController.get().isEmpty() || addresseController.get().isEmpty() ||
      numeroController.get().isEmpty() || specificCase  ) return false ;
    }

    return true ;

  }

  public static VBox generalDisplay(){ //toujours s'affiche haut de bilan orthophonique

    VBox contenue = new VBox(10) ;
    contenue.setPadding(new Insets(5,5,5,5));


    //adding elements

    Label title = new Label("---------------------  OBSERVATION GENERALE  ---------------------");
    title.setFont(new Font(25));
    title.setTextFill(Color.GRAY);

    TextArea observTextFiel = new TextArea() ;
    observTextFiel.setPrefRowCount(3);
    observTextFiel.setPrefWidth(400);
    observTextFiel.setPromptText("Observation");
    observTextFiel.setFont(PageHandler.font);
    observationController.bindBidirectional(observTextFiel.textProperty());

    CheckBox isPrisEnCharge = new CheckBox("oui") ;
    isPrisEnCharge.setFont(PageHandler.font);
    isPrisEnCharge.addEventHandler(MouseEvent.MOUSE_CLICKED , e -> {

      dynamicVBox.getChildren().clear();

      FinishRDV.isPrisEnCharge = isPrisEnCharge.isSelected(); 

      if(isPrisEnCharge.isSelected()){

        dynamicVBox.getChildren().addAll(
          connaisanceConsultDisplay() ,
          anamneseDisplay() , 
          epreuveClinicDisplay() ,
          diagnosticDisplay() ,
          projetTherapeutiqueDisplay()
        );

      } 

    });

    Label textobserva = new Label("Votre observation : ") ;
    Text textcheck = new Text("Ce patient sera pris en charge avec un dossier de Suivi : ") ;

    textobserva.setFont(PageHandler.font);
    textcheck.setFont(PageHandler.font);


    GridPane grid = new GridPane() ;
    grid.setHgap(10);
    grid.setVgap(10);

    grid.add( textobserva, 0, 0); //observation
    grid.add( observTextFiel , 1, 0);
    
    if(rdv.getType().equals("Consultation")){
      grid.add(textcheck, 0, 1);//si il sera pris en charge 
      grid.add(isPrisEnCharge, 1, 1);

      GridPane.setHalignment(isPrisEnCharge, HPos.CENTER);
    }

    grid.setAlignment(Pos.CENTER);

    contenue.getChildren().addAll(title , grid);


    contenue.setAlignment(Pos.CENTER);
    return contenue ;

  }

  public static VBox connaisanceConsultDisplay(){

    VBox contenue = new VBox(10) ;
    contenue.setPadding(new Insets(5,5,5,5));

    //adding elements

    Label title = new Label("---------------------  INFORMATIONS GENERALES  ---------------------");
    title.setFont(new Font(25));
    title.setTextFill(Color.GRAY);

    DatePicker calendar = new DatePicker(LocalDate.now()) ;
    calendar.setOnAction(action -> datePicked = LocalDateTime.of( calendar.getValue() , datePicked.toLocalTime() ));
    calendar.setStyle("-fx-font-size: 20px;");

    TextField lieuTextFiel = new TextField() ;
    lieuTextFiel.setPromptText("lieu de naissance");
    lieuTextFiel.setFont(PageHandler.font);
    lieuController.bindBidirectional(lieuTextFiel.textProperty());

    TextField addresseTextFiel = new TextField() ;
    addresseTextFiel.setPromptText("son addresse");
    addresseTextFiel.setFont(PageHandler.font);
    addresseController.bindBidirectional(addresseTextFiel.textProperty());

    TextField numPerTextFiel = new TextField() ;
    numPerTextFiel.setPromptText("numéro téléphone");
    numPerTextFiel.setFont(PageHandler.font);
    numeroController.bindBidirectional(numPerTextFiel.textProperty());

    TextField dipTextFiel = new TextField() ;
    dipTextFiel.setPromptText("Le diplome");
    dipTextFiel.setFont(PageHandler.font);
    diplomController.bindBidirectional(dipTextFiel.textProperty());

    TextField profTextFiel = new TextField() ;
    profTextFiel.setPromptText("proffession");
    profTextFiel.setFont(PageHandler.font);
    professionController.bindBidirectional(profTextFiel.textProperty());

    TextField classeTextFiel = new TextField() ;
    classeTextFiel.setPromptText("classe d'étude");
    classeTextFiel.setFont(PageHandler.font);
    classeController.bindBidirectional(classeTextFiel.textProperty());

    Label textdateNaiss = new Label("Entrez date de naissance de patient : ") ;
    Label textLieuNaiss = new Label("Entrez son lieu de naissance : ") ;
    Label textadr = new Label("Entrez son adresse : ") ;
    Label textnum = new Label( ((Consulation) rdv).isAdult() ? "Entrez son numéro téléphone : " : "Entrez numéro téléphone du parent :") ;
    Label textDip = new Label("Entrez son diplome : ") ;
    Label textProfess = new Label("Entrez son proffession : ") ;
    Label textClasse = new Label("Entrez son classe d'étude : ") ;


    textDip.setFont(PageHandler.font);
    textProfess.setFont(PageHandler.font);
    textLieuNaiss.setFont(PageHandler.font);
    textdateNaiss.setFont(PageHandler.font);
    textadr.setFont(PageHandler.font);
    textnum.setFont(PageHandler.font);
    textClasse.setFont(PageHandler.font);

    GridPane grid = new GridPane() ;
    grid.setHgap(10);
    grid.setVgap(10);

    grid.add(textdateNaiss , 0, 0); //Date de naissance
    grid.add(calendar, 1, 0);

    grid.add( textLieuNaiss, 0, 1); //lieu
    grid.add(  lieuTextFiel , 1, 1);

    grid.add( textadr , 0, 2); //addresse
    grid.add( addresseTextFiel, 1, 2);

    grid.add( textnum , 0, 3); //numéro
    grid.add( numPerTextFiel , 1, 3);

    if(((Consulation)rdv).isAdult()){

      grid.add( textDip , 0, 4); //diplome
      grid.add(dipTextFiel, 1, 4);

      grid.add(textProfess, 0, 5); //proffession
      grid.add(profTextFiel, 1, 5);

    } else {

      grid.add( textClasse , 0, 4); //diplome
      grid.add(classeTextFiel, 1, 4);

    }

    grid.setAlignment(Pos.CENTER);

    contenue.getChildren().addAll(title , grid) ;
    contenue.setAlignment(Pos.CENTER);

    return contenue ;

  }

  public static VBox anamneseDisplay(){

    //adding some elements

    Label title = new Label("---------------------  ANAMNESE  ---------------------");
    title.setFont(new Font(25));
    title.setTextFill(Color.GRAY);

    GridPane grid = ((Consulation)rdv).isAdult() ? UserData.anamneseAdults.itsGrid() : UserData.anamneseKids.itsGrid() ;

    VBox contenue = new VBox(10) ;
    contenue.setPadding(new Insets(5,5,5,5));

    contenue.getChildren().addAll(title , grid) ;
    contenue.setAlignment(Pos.CENTER);

    return contenue;
  }

  public static VBox epreuveClinicDisplay(){

    //adding some elements

    Label title = new Label("---------------------  EPREUVE CLINIQUE  ---------------------");

    title.setFont(new Font(25));
    title.setTextFill(Color.GRAY);

    TextField questTextField1 = new TextField() ;
    questTextField1.setPromptText("question 1");
    questTextField1.setFont(PageHandler.font);

    TextField responseTextField1 = new TextField() ;
    responseTextField1.setPromptText("reponse 1");
    responseTextField1.setFont(PageHandler.font);

    TextField questTextField2 = new TextField() ;
    questTextField2.setPromptText("question 2");
    questTextField2.setFont(PageHandler.font);

    TextField responseTextField2 = new TextField() ;
    responseTextField2.setPromptText("reponse 2");
    responseTextField2.setFont(PageHandler.font);

    TextField questTextField3 = new TextField() ;
    questTextField3.setPromptText("question 3");
    questTextField3.setFont(PageHandler.font);

    TextField responseTextField3 = new TextField() ;
    responseTextField3.setPromptText("reponse 3");
    responseTextField3.setFont(PageHandler.font);

    TextArea obscliniTextField = new TextArea() ;
    obscliniTextField.setPrefRowCount(3);
    obscliniTextField.setPrefWidth(400);
    obscliniTextField.setPromptText("observation");
    obscliniTextField.setFont(PageHandler.font);
    obscliniController.bindBidirectional(obscliniTextField.textProperty());

    TextField noteTextField = new TextField() ;
    noteTextField.setPromptText("note");
    noteTextField.setFont(PageHandler.font);
    noteController.bindBidirectional(noteTextField.textProperty());

    Label textobserva = new Label("Votre observation de son comportement : ") ;

    Label textnote = new Label("Sa note finale (/10) : ") ;

    textnote.setFont(PageHandler.font);
    textobserva.setFont(PageHandler.font);

    GridPane grid = new GridPane() ;
    grid.setHgap(10);
    grid.setVgap(10);

    grid.add(textobserva, 0, 0);
    grid.add(obscliniTextField, 1, 0);

    grid.add(questTextField1, 0, 1);
    grid.add(responseTextField1, 1, 1);
    grid.add(questTextField2, 0, 2);
    grid.add(responseTextField2, 1, 2);
    grid.add(questTextField3, 0, 3);
    grid.add(responseTextField3, 1, 3);


    grid.add(textnote, 0, 4);
    grid.add(noteTextField, 1, 4);

    grid.setAlignment(Pos.CENTER);

    VBox contenue = new VBox(10) ;
    contenue.setPadding(new Insets(5,5,5,5));

    contenue.getChildren().addAll(title , grid) ;
    contenue.setAlignment(Pos.CENTER);

    return contenue;
  }

  public static VBox diagnosticDisplay(){

    //adding some elements

    Label title = new Label("---------------------  DIAGNOSTIC  ---------------------");
    title.setFont(new Font(25));
    title.setTextFill(Color.GRAY);

    TextField nomTroubleTextField = new TextField() ;
    nomTroubleTextField.setPromptText("trouble");
    nomTroubleTextField.setFont(PageHandler.font);
    nomTroubleController.bindBidirectional(nomTroubleTextField.textProperty());

    ChoiceBox<String> typeTrouble = new ChoiceBox<>() ;
    typeTrouble.getItems().addAll("deglutition" , "neuro developpentaux" , "cognitifs") ;
    typeTrouble.setValue(typeTroubleStr); //default value
    typeTrouble.setStyle("-fx-font-size: 20px;");
    typeTrouble.setOnAction(action -> typeTroubleStr = typeTrouble.getValue());

    Label textnomTr = new Label("nom de la trouble : ") ;
    Label texttypeTr = new Label("type de trouble : ") ;

    texttypeTr.setFont(PageHandler.font);
    textnomTr.setFont(PageHandler.font);

    GridPane grid = new GridPane() ;
    grid.setHgap(10);
    grid.setVgap(10);

    grid.add(textnomTr, 0, 0);
    grid.add(nomTroubleTextField, 1, 0);
    grid.add(texttypeTr, 0, 1);
    grid.add(typeTrouble, 1, 1);

    grid.setAlignment(Pos.CENTER);
    //grid.setPrefWidth(widthGrid);
    //grid.setGridLinesVisible(true);

    VBox contenue = new VBox(10) ;
    contenue.setPadding(new Insets(5,5,5,5));

    contenue.getChildren().addAll(title , grid) ;
    contenue.setAlignment(Pos.CENTER);

    return contenue;
  }

  public static VBox projetTherapeutiqueDisplay(){

    //adding some elements

    Label title = new Label("---------------------  PROJET THERAPEUTIQUE  ---------------------");
    title.setFont(new Font(25));
    title.setTextFill(Color.GRAY);

    TextArea prjptTextField = new TextArea() ;
    prjptTextField.setPrefRowCount(3);
    prjptTextField.setPrefWidth(400);
    prjptTextField.setPromptText("description");
    prjptTextField.setFont(PageHandler.font);
    prjptTheraController.bindBidirectional(prjptTextField.textProperty());

    Label textdescript = new Label("description de ses troubles : ") ;

    textdescript.setFont(PageHandler.font);

    GridPane grid = new GridPane() ;
    grid.setHgap(10);
    grid.setVgap(10);

    grid.add(textdescript, 0, 0);
    grid.add(prjptTextField, 1, 0);

    grid.setAlignment(Pos.CENTER);
    //grid.setPrefWidth(widthGrid);
    //grid.setGridLinesVisible(true);

    VBox contenue = new VBox(10) ;
    contenue.setPadding(new Insets(5,5,5,5));

    contenue.getChildren().addAll(title , grid) ;
    contenue.setAlignment(Pos.CENTER);

    return contenue;
  }

  public static void display(RendezVous rendezVous){

    initControllers();

    rdv = rendezVous ;
    System.out.println(rdv.getType().toString());

    BorderPane mainContent = new BorderPane();
    mainContent.setPadding(new Insets(20,20,50,20));

    dynamicVBox = new VBox(20) ;
    dynamicVBox.setAlignment(Pos.CENTER);

    if( !rdv.getType().equals("Consultation") ){

      dynamicVBox.getChildren().clear();
      dynamicVBox.getChildren().addAll(epreuveClinicDisplay() , diagnosticDisplay() , projetTherapeutiqueDisplay());

    }

    Button bilanButton = new Button("créer bilan orthophonique");
    bilanButton.setFont(PageHandler.font);
    bilanButton.setOnAction(action -> {

      try {

        int j = -5 ;

        if(!(rdv.getType().equals("Consultation") && !isPrisEnCharge)){
          System.out.println("1");
          j = Integer.parseInt(noteController.get()) ;
        }
  
        if(rdv.getType().equals("Consultation") && isPrisEnCharge){
          System.out.println("2");
          int i = Integer.parseInt(numeroController.get()) ;
          System.out.println(i);
        }


        if(!verifyEmptiness()){
  
          Label errorMessage = new Label("Création de Bilan echouée , veuillez remplir tous les informations");
          errorMessage.setFont(new Font(15));
          errorMessage.setTextFill(Color.RED);
  
          mainContent.setBottom(errorMessage);        
  
        }else if( !(rdv.getType().equals("Consultation") && !isPrisEnCharge) && ( j < 0 || j > 10)){

          Label errorMessage = new Label("Création de Bilan echouée , note non crédible");
          errorMessage.setFont(new Font(15));
          errorMessage.setTextFill(Color.RED);
  
          mainContent.setBottom(errorMessage); 

        }else if(rdv.getType().equals("Consultation") && isPrisEnCharge && numeroController.get().length() != 10){

          Label errorMessage = new Label("Création de Bilan echouée , numéro de téléphone non crédible");
          errorMessage.setFont(new Font(15));
          errorMessage.setTextFill(Color.RED);
  
          mainContent.setBottom(errorMessage); 

        } else if( LocalDateTime.now().getYear() - datePicked.getYear() < 4){

          Label errorMessage = new Label("Création de Bilan echouée , date de naissance non crédible");
          errorMessage.setFont(new Font(15));
          errorMessage.setTextFill(Color.RED);
  
          mainContent.setBottom(errorMessage); 

        } else {

          if(ConfirmationBox.display("Confirmation Bilan", "Etes vous sur de valider ce bilan et continuer ?")){

            rdv.setObserv(observationController.get());

            BilanOrtho bilan = null ;

            Troubles typtrb = typeTroubleStr == "deglutition" ? Troubles.deglutition : (typeTroubleStr == "cognitifs" ? Troubles.cognitifs : Troubles.neuro_developpentaux) ;

            Projettheraph prjpt = new Projettheraph(prjptTheraController.get()) ;

            Trouble[] troubles = {new Trouble(nomTroubleController.get() , typtrb)} ;
            Diagnostic diagnostic = new Diagnostic(troubles) ;

            Epclinic[] epreuves = {new Epclinic()} ;

            if(rdv.getType().equals("Consultation") && isPrisEnCharge){
              bilan = new FirstBilan(epreuves, diagnostic, prjpt, ((Consulation)rdv).isAdult() ? UserData.anamneseAdults : UserData.anamneseKids) ;
            }else{
              bilan = new BilanOrtho(epreuves, diagnostic, prjpt) ;
            }

            switch (rdv.typeRDV.toString()) {

              case "Atelier":
                
                List<Patient> patients = ((Atelier)rdv).getPatient() ;

                for(Patient patient : patients) patient.ajouBilan(bilan);

              break;

              case "Suivi" :

                Patient patient = ((Suivi)rdv).getPatient() ;
                patient.ajouBilan(bilan);

              break;

              case "Consultation":

                Patient newPatient = ((Consulation)rdv).getPatient() ;

                if(newPatient.isAdult()) ((Adulte)newPatient).setNewInfo(datePicked.toLocalDate(), lieuController.get(), addresseController.get(), diplomController.get(), professionController.get(), Integer.parseInt(numeroController.get()));
                else ((Enfant)newPatient).setNewInfo(datePicked.toLocalDate(), lieuController.get(), addresseController.get(), classeController.get(), Integer.parseInt(numeroController.get()));

                UserData.mapPatients.put(newPatient.getID(), newPatient);

                newPatient.ajouBilan(bilan);
                newPatient.ajouRDV(rdv);

                PopUpNotification.display("Information", "Le numero unique de ce nouveau patient : N°" + newPatient.getID());

              break ;


              default:
                System.out.println("no such type");
              break;
            }

            UserData.orthophoniste.removeRDV(rdv);

            PageHandler.primaryStage.setTitle("Page Principale");
            HomePage.display();
            PageHandler.primaryStage.setScene(HomePage.scene);

          }

        }
  
      } catch (NumberFormatException e) {
        
        Label errorMessage = new Label("Création de Bilan echouée , veuillez entrer des données entiers crédibles");
        errorMessage.setFont(new Font(15));
        errorMessage.setTextFill(Color.RED);
  
        mainContent.setBottom(errorMessage); 

      }

    });

    Button returnButton = new Button("<-") ;
    returnButton.setFont(PageHandler.font);
    returnButton.setOnAction(action -> {

      PageHandler.primaryStage.setTitle("Mon Agenda");
      PageHandler.primaryStage.setScene(ConsultAgenda.scene);

    });
    
    returnButton.setAlignment(Pos.TOP_LEFT);

    HBox firstLine = new HBox() ;
    firstLine.getChildren().add(returnButton); //pour positionner le retour bouton vers la gauche 
    
    

    VBox contenue = new VBox(20) ;
    contenue.getChildren().addAll(firstLine , generalDisplay() , dynamicVBox , bilanButton );
    

    contenue.setAlignment(Pos.TOP_CENTER);
    mainContent.setTop(contenue);
     
    ScrollPane listView = new ScrollPane(mainContent) ;
    listView.setPadding(new Insets(20 ,40 ,30,40));
    listView.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Vertical scroll bar
    listView.setFitToWidth(true); //to center the textField elements
    listView.setFitToHeight(true);
    
    scene = new Scene(listView , PageHandler.width , PageHandler.height) ;



  }

}

class QCUPage{
  public static Scene scene ;

  public void display(){
    Label lab1 = new Label("Question1: Quelle est la nature de votre trouble de la parole ?");

  }
}
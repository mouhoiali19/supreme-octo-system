import javafx.beans.property.* ;
import javafx.geometry.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

class PopUpNotification{ //window notification

  public static void display(String title , String message){

    Stage window = new Stage() ;

    window.initModality(Modality.APPLICATION_MODAL);
    window.setTitle(title);

    VBox contenue = new VBox(20) ; //Vetrtical Box

    contenue.setPadding(new Insets(20,20,20,20)); //padding

    Label etiq = new Label(message) ;
    etiq.setFont(new Font(15));
    contenue.getChildren().add(etiq);

    HBox horizBox = new HBox(100) ;
    horizBox.setAlignment(Pos.CENTER);

    Button okButton = new Button("Bien reçu") ;
    okButton.setFont(new Font(15));

    okButton.setOnAction(action -> {
      window.close();
    });

    horizBox.getChildren().addAll(okButton) ;

    contenue.getChildren().add(horizBox);
    contenue.setAlignment(Pos.CENTER);

    Scene scene=new Scene(contenue,400,200); //Scène est la fenètre elle mème qui va contenir le contenue , yeah tfelsif , 900 et 400 houma toul et l3erd te3 la fenètre en pixels
    window.setScene(scene);
    window.showAndWait(); //affichage

  }

}

class ConfirmationBox{ //window to verify user action

  static boolean answer ;

  public static boolean display(String title , String message){

    Stage window = new Stage() ;

    window.setOnCloseRequest(e -> { //if closed by "X" do this
      answer = false ; 
    });

    window.initModality(Modality.APPLICATION_MODAL);
    window.setTitle(title);

    VBox contenue = new VBox(20) ; //Vetrtical Box

    contenue.setPadding(new Insets(20,20,20,20)); //padding

    Label etiq = new Label(message) ;
    etiq.setFont(new Font(15));
    contenue.getChildren().add(etiq);

    HBox horizBox = new HBox(100) ;
    horizBox.setAlignment(Pos.CENTER);

    Button yesButton = new Button("oui") ;
    yesButton.setFont(new Font(15));
    Button noButton = new Button("non");
    noButton.setFont(new Font(15));

    yesButton.setOnAction(action -> {
      answer = true ;
      window.close();
    });

    noButton.setOnAction(action -> {
      answer = false ;
      window.close();
    });

    horizBox.getChildren().addAll(yesButton,noButton) ;

    contenue.getChildren().add(horizBox);
    contenue.setAlignment(Pos.CENTER);

    Scene scene=new Scene(contenue); //Scène est la fenètre elle mème qui va contenir le contenue , yeah tfelsif , 900 et 400 houma toul et l3erd te3 la fenètre en pixels
    window.setScene(scene);
    window.showAndWait(); //affichage

    return answer ;

  }

}

class FirstPage{ //première page de l'application

  public static Scene scene ; //scene de première page
  
  public static void display(){

    //ImageView image =  new ImageView(new Image("C:/Users/HP/Desktop/Documents/ESI/Programmes/Java/TpJavaFx/Images/OrthoLogo.jpg")) ;

    VBox contenue = new VBox(40) ;
    contenue.setAlignment(Pos.CENTER);

    Label welcomeLabel = new Label("Bienvenue chers orthophoniste dans votre cabinet : ") ; //un texte Bienvenue
    welcomeLabel.setFont(new Font(30));

    Button inscription = new Button("Inscription") ; //bouton inscription
    inscription.setMinSize(100, 50);
    inscription.setFont(PageHandler.font);
    inscription.setOnAction(action -> {

      PageHandler.primaryStage.setTitle("page d'inscription");
      InscriptionPage.display();
      PageHandler.primaryStage.setScene(InscriptionPage.scene);

    });

    Button connexion = new Button("se connecter") ; //bouton connexion
    connexion.setMinSize(100, 50);
    connexion.setFont(PageHandler.font) ;
    connexion.setOnAction(action -> { //en cliquant sur connexion

      PageHandler.primaryStage.setTitle("page de connexion");
      ConnexionPage.display(); //on crée la scène
      PageHandler.primaryStage.setScene(ConnexionPage.scene); //on lance cette scène de connexion

    });

    contenue.getChildren().addAll(welcomeLabel , connexion , inscription) ;

    scene = new Scene(contenue , PageHandler.width , PageHandler.height); 


  }


}

class ConnexionPage{ //page de connexion

  public static Scene scene ;

  public static StringProperty emailController =  new SimpleStringProperty() , passwordController = new SimpleStringProperty()  ;

  public static GridPane gridDisplay(){ //élément complexe dans une méthode

    //adding elements
    TextField emailTextFiel = new TextField() ;
    emailTextFiel.setPromptText("email : XXX@XX.com");
    emailTextFiel.setFont(PageHandler.font);
    emailController.bindBidirectional(emailTextFiel.textProperty());

    PasswordField passwordTextField = new PasswordField() ;
    passwordTextField.setPromptText("mot de passe");
    passwordTextField.setFont(PageHandler.font);
    passwordController.bindBidirectional(passwordTextField.textProperty());

    Label textName = new Label("Entrez votre addresse email : ") ;
    Label textPass = new Label("Entrez votre mot de passe : ") ;

    textName.setFont(PageHandler.font);
    textPass.setFont(PageHandler.font);

    GridPane grid = new GridPane() ;
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(10,10,10,10));

    grid.add(textName, 0, 0);
    grid.add(emailTextFiel, 1, 0);
    grid.add(textPass, 0, 1);
    grid.add(passwordTextField, 1, 1);

    grid.setAlignment(Pos.CENTER);

    return grid ;

  }

  public static void display(){

    BorderPane mainContent = new BorderPane();

    Button connexionButton = new Button("se connecter") ;
    connexionButton.setFont(PageHandler.font);

    connexionButton.setOnAction(action -> {

      if(UserData.checkConnexion(emailController.get() , passwordController.get())){
        
        PageHandler.primaryStage.setTitle("Page Principale");
        HomePage.display();
        PageHandler.primaryStage.setScene(HomePage.scene);

      }else{

        Label errorMessage = new Label("Connexion echouée , veuillez vérifier vos entrées");
        errorMessage.setFont(new Font(15));
        errorMessage.setTextFill(Color.RED);

        mainContent.setBottom(errorMessage);

      }

    });

    Button returnButton = new Button("<-") ;
    returnButton.setFont(PageHandler.font);
    returnButton.setOnAction(action -> {

      PageHandler.primaryStage.setTitle("page d'acceuil");
      PageHandler.primaryStage.setScene(FirstPage.scene);

    });
    returnButton.setAlignment(Pos.TOP_LEFT);

    HBox firstLine = new HBox() ;
    firstLine.getChildren().add(returnButton); //pour positionner le retour bouton vers la gauche

    //creating grid Layout

    GridPane grid = gridDisplay() ;

    VBox contenue = new VBox(20) ;
    mainContent.setPadding(new Insets(20,20,20,20));

    contenue.getChildren().addAll(firstLine , grid , connexionButton) ;

    contenue.setAlignment(Pos.TOP_CENTER);

    mainContent.setTop(contenue);

    scene = new Scene(mainContent , PageHandler.width , PageHandler.height) ;

  }

}

class InscriptionPage{// page d'inscription

  public static Scene scene ;

  public static StringProperty emailController ,
    nomController , prenomController , addresseController , numTelController ,
  passwordController ;

  public static void initControllers(){

    nomController = new SimpleStringProperty() ;
    prenomController = new SimpleStringProperty() ;
    emailController = new SimpleStringProperty() ;
    addresseController = new SimpleStringProperty() ;
    numTelController = new SimpleStringProperty() ;
    passwordController = new SimpleStringProperty() ;
  }

  public static boolean verifyEmptiness() {

    if(emailController.get().isEmpty() || nomController.get().isEmpty() || prenomController.get().isEmpty()
    || addresseController.get().isEmpty() || numTelController.get().isEmpty() || 
    passwordController.get().isEmpty() ) return false ;

    return true ;

  }

  public static GridPane gridDisplay(){

    //adding elements

    TextField emailTextFiel = new TextField() ;
      emailTextFiel.setPromptText("email : XXX@XX.com");
      emailTextFiel.setFont(PageHandler.font);
    emailController.bindBidirectional(emailTextFiel.textProperty());

    TextField nomTextFiel = new TextField() ;
    nomTextFiel.setPromptText("Nom");
    nomTextFiel.setFont(PageHandler.font);
    nomController.bindBidirectional(nomTextFiel.textProperty());

    TextField prenomTextFiel = new TextField() ;
    prenomTextFiel.setPromptText("Prénom");
    prenomTextFiel.setFont(PageHandler.font);
    prenomController.bindBidirectional(prenomTextFiel.textProperty());

    TextField addresseTextFiel = new TextField() ;
    addresseTextFiel.setPromptText("Votre addresse");
    addresseTextFiel.setFont(PageHandler.font);
    addresseController.bindBidirectional(addresseTextFiel.textProperty());

    TextField numTelTextFiel = new TextField() ;
    numTelTextFiel.setPromptText("numéro téléphone");
    numTelTextFiel.setFont(PageHandler.font);
    numTelController.bindBidirectional(numTelTextFiel.textProperty());

    PasswordField passwordTextField = new PasswordField() ;
    passwordTextField.setPromptText("mot de passe");
    passwordTextField.setFont(PageHandler.font);
    passwordController.bindBidirectional(passwordTextField.textProperty());

    Label textemail = new Label("Entrez votre addresse email : ") ;
    Label textPass = new Label("Entrez votre mot de passe : ") ;
    Label textNom = new Label("Entrez votre nom : ") ;
    Label textPre = new Label("Entrez votre prénom : ") ;
    Label textadr = new Label("Entrez votre adresse : ") ;
    Label textnum = new Label("Entrez votre numéro téléphone : ") ;


    textNom.setFont(PageHandler.font);
    textPre.setFont(PageHandler.font);
    textPass.setFont(PageHandler.font);
    textemail.setFont(PageHandler.font);
    textadr.setFont(PageHandler.font);
    textnum.setFont(PageHandler.font);


    GridPane grid = new GridPane() ;
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(10,10,10,10));

    grid.add(textNom, 0, 0); //nom
    grid.add(nomTextFiel, 1, 0);

    grid.add(textPre, 0, 1); //prénom
    grid.add(prenomTextFiel, 1, 1);

    grid.add(textemail, 0, 2); //email
    grid.add(emailTextFiel, 1, 2);

    grid.add(textadr, 0, 3); //addresse
    grid.add(addresseTextFiel, 1, 3);

    grid.add(textnum, 0, 4); //numéro tel
    grid.add(numTelTextFiel, 1, 4);

    grid.add(textPass, 0, 5); //mot de passe
    grid.add(passwordTextField, 1, 5);

    grid.setAlignment(Pos.CENTER);

    return grid ;

  }

  public static void display(){

    initControllers();

    BorderPane mainContent = new BorderPane();

    Button inscripButton = new Button("créer un compte") ;
    inscripButton.setFont(PageHandler.font);

    inscripButton.setOnAction(action -> {

      try {

        int i = Integer.parseInt(numTelController.get());
        System.out.println(i);

        if(!verifyEmptiness()){

          Label errorMessage = new Label("Inscription echouée , veuillez remplir tous les informations");
          errorMessage.setFont(new Font(15));
          errorMessage.setTextFill(Color.RED);
  
          mainContent.setBottom(errorMessage);        
  
        }else if(!emailController.get().contains("@") || !emailController.get().contains(".com")){
  
          Label errorMessage = new Label("Inscription echouée , veuillez entrer un email crédible");
          errorMessage.setFont(new Font(15));
          errorMessage.setTextFill(Color.RED);
  
          mainContent.setBottom(errorMessage); 
  
        }else if(numTelController.get().length() != 10){

          Label errorMessage = new Label("Inscription echouée , la taille de votre numéro n'est pas logique");
          errorMessage.setFont(new Font(15));
          errorMessage.setTextFill(Color.RED);

          mainContent.setBottom(errorMessage); 

        }else {

          Orthophoniste user = new Orthophoniste(nomController.get(), prenomController.get(), addresseController.get(), i , emailController.get(), passwordController.get()) ;
  
          UserData.comptes.add(user) ;

          PageHandler.primaryStage.setTitle("Page Principale");
          HomePage.display();
          PageHandler.primaryStage.setScene(HomePage.scene);
  
        }
        
      } catch (NumberFormatException e) {

        Label errorMessage = new Label("Inscription echouée , veuillez entrer un numéro téléphone crédible");
        errorMessage.setFont(new Font(15));
        errorMessage.setTextFill(Color.RED);

        mainContent.setBottom(errorMessage); 
      }

    });

    Button returnButton = new Button("<-") ;
    returnButton.setFont(PageHandler.font);
    returnButton.setOnAction(action -> {

      PageHandler.primaryStage.setTitle("page d'acceuil");
      PageHandler.primaryStage.setScene(FirstPage.scene);

    });
    returnButton.setAlignment(Pos.TOP_LEFT);

    HBox firstLine = new HBox() ;
    firstLine.getChildren().add(returnButton); //pour positionner le retour bouton vers la gauche

    //creating grid Layout

    GridPane grid = gridDisplay() ;

    VBox contenue = new VBox(20) ;
    mainContent.setPadding(new Insets(20,20,50,20));

    contenue.getChildren().addAll(firstLine , grid , inscripButton) ;

    contenue.setAlignment(Pos.TOP_CENTER);

    mainContent.setTop(contenue);

    ScrollPane listView = new ScrollPane(mainContent) ;
    listView.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Vertical scroll bar
    listView.setFitToWidth(true); //to center the textField elements
    listView.setFitToHeight(true);
    

    scene = new Scene(listView , PageHandler.width , PageHandler.height) ;

  }


}

class HomePage{ //page d'acceuil

  public static Scene scene ; //scene de page d'acceuil

  public static HBox appBar(){

    //add some elements

    Label toDeconnect = new Label("se déconnecter") ;
    toDeconnect.setFont(PageHandler.font);
    toDeconnect.setTextFill(Color.WHITE) ;
    //add a CSS style to make it look like a hyperlink
    toDeconnect.setStyle("-fx-cursor: hand; -fx-underline: true;");

    //add an event handler for mouse click events
    toDeconnect.addEventHandler(MouseEvent.MOUSE_CLICKED, action -> {

      boolean answer = ConfirmationBox.display("Confimer la déconnexion", "Etes vous sure de vous déconnecter ?") ; //une fenètre de confirmation va se montrer , il faut clikquer sur oui pour retourner un true , voir classe ConfirmationBox

      if( answer ){ 
        PageHandler.primaryStage.setTitle("page de connexion");
        PageHandler.primaryStage.setScene(FirstPage.scene);
      }

    });

    Label cabinet = new Label("CABINET D'ORTHOPHONISTE") ;
    cabinet.setFont(PageHandler.font);
    cabinet.setTextFill(Color.WHITE);

    HBox extendedSpace = new HBox() ;

    //some layouts

    HBox appBar = new HBox(10) ;
    appBar.setBackground(new Background(new BackgroundFill(Color.BLUE , CornerRadii.EMPTY , Insets.EMPTY )));
    appBar.setPadding(new Insets(20,30,20,30));

    appBar.getChildren().addAll(cabinet , extendedSpace , toDeconnect);
    HBox.setHgrow(extendedSpace, Priority.ALWAYS);

    return appBar ; 

  }

  public static void display(){

    //add some elements

    Button buttonRDV = new Button("Créer un rendez vous") ;
    Button buttonConsultPatient = new Button("Consulter mes patients") ;
    Button buttonConsultAgenda =  new Button("Mon Agenda") ;

    buttonRDV.setOnAction(action -> {

      CreerRDV.display();
      PageHandler.primaryStage.setTitle("Création d'un rendez-vous");
      PageHandler.primaryStage.setScene(CreerRDV.scene);

    });

    buttonConsultAgenda.setOnAction(action -> {
      ConsultAgenda.display();
      PageHandler.primaryStage.setTitle("Mon agenda");
      PageHandler.primaryStage.setScene(ConsultAgenda.scene);
    });

    buttonConsultPatient.setOnAction(action -> {

      ConsultPatient.display();
      PageHandler.primaryStage.setTitle("Mes patients");
      PageHandler.primaryStage.setScene(ConsultPatient.scene);

    });

    buttonRDV.setFont(PageHandler.font);
    buttonConsultPatient.setFont(PageHandler.font);
    buttonConsultAgenda.setFont(PageHandler.font);

    buttonRDV.setPadding(new Insets(10,10,10,10));
    buttonConsultPatient.setPadding(new Insets(10,10,10,10));
    buttonConsultAgenda.setPadding(new Insets(10,10,10,10));

    buttonConsultAgenda.setAlignment(Pos.CENTER);
    buttonRDV.setAlignment(Pos.CENTER);
    buttonConsultPatient.setAlignment(Pos.CENTER);

    //add some layouts

    BorderPane contenue = new BorderPane() ;

    VBox contenueButton = new VBox(30) ;
    contenueButton.getChildren().addAll(buttonRDV , buttonConsultAgenda , buttonConsultPatient) ;
    contenueButton.setAlignment(Pos.CENTER);

    HBox appBar = appBar() ;

    contenue.setTop(appBar); 
    contenue.setCenter(contenueButton);

    scene = new Scene(contenue , PageHandler.width , PageHandler.height) ;


  }

}

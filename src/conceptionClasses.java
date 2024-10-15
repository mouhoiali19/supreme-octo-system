import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;


abstract class RendezVous implements Serializable , Comparable<RendezVous> {

  protected double duree;
  protected LocalDateTime dateRDV ;
  protected String typeRDV ;
  protected String observations ;
  
  RendezVous(LocalDateTime dateRDV){
    this.dateRDV = dateRDV ;
  }

  public void setObserv(String observations){
    this.observations = observations ;
  }

  public String getType(){

    return typeRDV ;

  }

  public int compareTo(RendezVous autre){

    return dateRDV.compareTo(autre.dateRDV);

  }

  public abstract double setduree();
  public abstract String  settype();
  public abstract VBox itsWidget(boolean isForPatient) ;
  
}

class Consulation extends RendezVous {

  private Patient patient ; 
  
  Consulation(LocalDateTime dateRDV,Patient patient){

    super(dateRDV);
    this.patient= patient ;

    duree = setduree() ;
    typeRDV = settype() ;

  }

  public Patient getPatient(){

    return patient ;

  }

  public boolean isAdult(){
    return patient.isAdult();
  }

  public String settype(){
    return "Consultation" ;
  }
  
  public double setduree(){
    return patient.durreConsult();
  }

  public VBox itsWidget(boolean isForPatient){ //son affichage en agenda

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    VBox contenue = new VBox(10) ;
    contenue.setPadding(new Insets(10,10,10,10));

    Button bilanButton = new Button("Créer le bilan");
    bilanButton.setFont(PageHandler.font);

    bilanButton.setOnAction(action -> {

      FinishRDV.display(this);
      PageHandler.primaryStage.setTitle("Bilan orthophonique");
      PageHandler.primaryStage.setScene(FinishRDV.scene);

    });

    Button removeButton = new Button("Supprimer");
    removeButton.setFont(PageHandler.font);

    removeButton.setOnAction(action ->{

      UserData.orthophoniste.removeRDV(this);
      ConsultAgenda.display();     
      PageHandler.primaryStage.setScene(ConsultAgenda.scene);      


    });

    //add elements
    Label titre = new Label("----------------------- CONSULTATION -----------------------");
    titre.setFont(new Font(30));
    
    Label dateRDV = new Label("Date : "+ super.dateRDV.format(formatter)) ;
    dateRDV.setFont(PageHandler.font);

    Label duree = new Label("Durée : "+ super.duree + "h");
    duree.setFont(PageHandler.font);

    Label nomPre = new Label("nom et prénom :" + patient.nom.toString() + " " + patient.prenom.toString());
    nomPre.setFont(PageHandler.font);

    contenue.getChildren().addAll(titre ,  dateRDV , duree , nomPre ) ;

    if(LocalDateTime.now().compareTo(super.dateRDV) > 0 && !isForPatient) contenue.getChildren().add(bilanButton) ;
    if(!isForPatient) contenue.getChildren().add(removeButton) ;
    
    contenue.setAlignment(Pos.CENTER);
    contenue.setStyle("-fx-background-color:white;");

    return contenue ;
    
 
  }

}

class Suivi extends RendezVous{

  private Patient patient ;
  private boolean isPresentiel ;// 1 presentiel o en ligne 
  //private Objective objective ;

  Suivi(LocalDateTime dateRDV , boolean presentiel , Patient patient ){

    super(dateRDV);
    this.patient = patient ;
    this.isPresentiel = presentiel ; 
    patient.ajouRDV(this);

    setduree();
    settype();

  }

  public double setduree(){
    return duree =1 ;
  }

  public Patient getPatient(){

    return patient ;

  }


  public String settype(){
    return typeRDV = "Suivi" ;
  }

  public VBox itsWidget(boolean isForPatient){ //son affichage en agenda

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    String presence = isPresentiel ? "PRESENTIEL" : "EN LIGNE" ;

    VBox contenue = new VBox(10) ;
    contenue.setPadding(new Insets(10,10,10,10));

    //add elements
    Label titre = new Label("----------------------- Suivi " + presence.toString() + " -----------------------");
    titre.setFont(new Font(30));
    
    Label dateRDV = new Label("Date : "+ super.dateRDV.format(formatter)) ;
    dateRDV.setFont(PageHandler.font);

    Label duree = new Label("Durée : "+ super.duree + "h");
    duree.setFont(PageHandler.font);

    Label nomPre = new Label("Patient N°( " +patient.getID() + " ) :" + patient.nom.toString() + " " + patient.prenom.toString());
    nomPre.setFont(PageHandler.font);

    Label dateNaissance = new Label("Date de naissance : " + patient.dateNaissance);
    dateNaissance.setFont(PageHandler.font);

    Button bilanButton = new Button("Créer le bilan");
    bilanButton.setFont(PageHandler.font);

    bilanButton.setOnAction(action -> {

      FinishRDV.display(this);
      PageHandler.primaryStage.setTitle("Bilan orthophonique");
      PageHandler.primaryStage.setScene(FinishRDV.scene);

    });

    Button removeButton = new Button("Supprimer");
    removeButton.setFont(PageHandler.font);

    removeButton.setOnAction(action ->{

      UserData.orthophoniste.removeRDV(this);
      ConsultAgenda.display();     
      PageHandler.primaryStage.setScene(ConsultAgenda.scene); 

    });

    contenue.getChildren().addAll(titre , dateRDV , duree , nomPre , dateNaissance) ;
    if(LocalDateTime.now().compareTo(super.dateRDV) > 0 && !isForPatient) contenue.getChildren().add(bilanButton) ;
    if(!isForPatient) contenue.getChildren().add(removeButton) ;
    
    contenue.setAlignment(Pos.CENTER);
    contenue.setStyle("-fx-background-color:white;");

    return contenue ;
    
 
  }


  public void afficheInfo(){

    patient.afficheInfo();
    System.out.println("is it presentiel ? " + isPresentiel);

  }
    
}

class Atelier extends RendezVous{

  private String thematique ;
  private List<Patient> listpatient ;

  Atelier(LocalDateTime dateRDV ,String thematique , List<Patient> listpatient){
    super(dateRDV);
    this.thematique =thematique ;
    this.listpatient = new ArrayList<Patient>(listpatient) ;

    for (Patient patient : listpatient) patient.ajouRDV(this);

    setduree();
    settype();
  }

  public double setduree(){
    return duree = 1 ;
  }

  public List<Patient> getPatient(){

    return listpatient ;

  }


  public String settype(){
    return typeRDV = "Atelier" ;
  }

  public VBox itsWidget(boolean isForPatient){ //son affichage en agenda

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    VBox contenue = new VBox(10) ;
    contenue.setPadding(new Insets(10,10,10,10));

    //add elements
    Label titre = new Label("----------------------- ATELIER -----------------------");
    titre.setFont(new Font(30));
    
    Label dateRDV = new Label("Date : "+ super.dateRDV.format(formatter)) ;
    dateRDV.setFont(PageHandler.font);

    Label duree = new Label("Durée : "+ super.duree + "h");
    duree.setFont(PageHandler.font);

    Label thema = new Label("thématique : "+ thematique.toString());
    thema.setFont(PageHandler.font);

    Label number = new Label("groupe de "+listpatient.size()+" patients ");
    number.setFont(PageHandler.font);

    Label titlePAT = new Label("----------------------- PATIENTS -----------------------");
    titlePAT.setFont(PageHandler.font);

    Button bilanButton = new Button("Créer le bilan");
    bilanButton.setFont(PageHandler.font);

    bilanButton.setOnAction(action -> {

      FinishRDV.display(this);
      PageHandler.primaryStage.setTitle("Bilan orthophonique");
      PageHandler.primaryStage.setScene(FinishRDV.scene);

    });

    Button removeButton = new Button("Supprimer");
    removeButton.setFont(PageHandler.font);

    removeButton.setOnAction(action ->{

      UserData.orthophoniste.removeRDV(this);
      ConsultAgenda.display();     
      PageHandler.primaryStage.setScene(ConsultAgenda.scene); 

    });

    contenue.getChildren().addAll(titre , dateRDV , duree , thema , number , titlePAT);

    for (Patient patient : listpatient) {
      
      Label nomPre = new Label("( " +patient.getID() + " ) :"+"Nom et prénom : " + patient.nom.toString() + " " + patient.prenom.toString());
      nomPre.setFont(PageHandler.font);
  
      Label dateNaissance = new Label("Date de naissance : " + patient.dateNaissance);
      dateNaissance.setFont(PageHandler.font);

      Label tray = new Label("--------------------------------------------------------");
      tray.setFont(PageHandler.font);

      contenue.getChildren().addAll(nomPre , dateNaissance , tray);

    }
    
    if(LocalDateTime.now().compareTo(super.dateRDV) > 0 && !isForPatient) contenue.getChildren().add(bilanButton) ;
    if(!isForPatient) contenue.getChildren().add(removeButton) ;

    contenue.setAlignment(Pos.CENTER);
    contenue.setStyle("-fx-background-color:white;");

    return contenue ;
    
 
  }


  public void afficheInfo(){

    System.out.println(thematique.toString());
    for (Patient patient : listpatient) {
      patient.afficheInfo();
    }

  }

}

class Orthophoniste implements Serializable {

  private String nom ;
  private String prenom ;
  private String adress ;
  private  int numTel ;
  private String email;
  private String motPasse;
  private Agenda agenda ; 

  Orthophoniste(String nom , String prenom , String adress , int numTel , String email , String motPasse){
    this.nom = nom ;
    this.prenom = prenom ;
    this.numTel = numTel ;
    this.adress = adress ;
    this.email = email ;
    this.motPasse = motPasse ;
    agenda = new Agenda();
  }

  public void addRDV(RendezVous rdv){

    agenda.addRDV(rdv);

  }

  public void removeRDV(RendezVous rdv){
    agenda.removeRDV(rdv) ;
  }
   
  public boolean isAgendaEmpty(){
    return agenda.isEmpty() ;
  }

  public List<RendezVous> getListRDV(){
    return agenda.getList() ;
  }

  public String getEmail(){
    return email ;
  }

  public String getPassword(){
    return motPasse ;
  }

  public void afficheInfo(){

    System.out.println("name : "+nom.toString() + " "+prenom.toString());
    System.out.println(adress.toString());
    System.out.println(numTel);
    System.out.println(email.toString());
    System.out.println(motPasse.toString());

  }


}

class Agenda implements Serializable {

  private List<RendezVous> listeRDV = new ArrayList<RendezVous>();  ;
  
  Agenda(){
    listeRDV.clear();
  }

  public void addRDV(RendezVous rdv){

    listeRDV.add(rdv);
    Collections.sort(listeRDV);

  }

  public void removeRDV(RendezVous rdv){
    listeRDV.remove(rdv) ;
  }

  public boolean isEmpty(){
    return listeRDV.isEmpty() ;
  }

  public List<RendezVous> getList(){
    return listeRDV ;
  }

}

class Dossier implements Serializable{

  List<RendezVous> rdvPatient = new ArrayList<RendezVous>();
  private int numUnique ;
  private List<BilanOrtho> bilanortho = new ArrayList<BilanOrtho>() ;
  private List<FicheSuivie> fichesuivi = new ArrayList<FicheSuivie>() ;

  Dossier(int numUnique){

    this.numUnique = numUnique ;
    rdvPatient.clear();

  }

  public void addRDV(RendezVous rdv){
    rdvPatient.add(rdv);
    Collections.sort(rdvPatient);
  }

  public void addBilan(BilanOrtho bilan){

    bilanortho.add(bilan) ;

  }

  public void afficheInfo(){

    System.out.println("id : "+numUnique);

    for (BilanOrtho bilan : bilanortho) {
      System.out.println(bilan.toString());
    }

    for (FicheSuivie ficheSuivie : fichesuivi) {
      ficheSuivie.afficheInfo();
    }

  }

  public VBox afficher(){

    VBox content = new VBox(10);

    for(RendezVous rdv : rdvPatient){
      content.getChildren().add(rdv.itsWidget(true));
    }
    
    content.setAlignment(Pos.CENTER);
    return content ;
  }

  public VBox afficheFS(){

    VBox content = new VBox (20) ;

    if(fichesuivi.isEmpty()){
      Label vide = new Label("Votre List de Fiche de Suivi est vide ");
      vide.setFont(PageHandler.font);
      content.getChildren().add(vide);
     }else{
      for(FicheSuivie fiche : fichesuivi){
        content.getChildren().add(fiche.printthis());
      //  content.getChildren().add(separate);
      }
    }
    content.setAlignment(Pos.CENTER);
    return content ;
  }

  public void addFSui(FicheSuivie fich){
    fichesuivi.add(fich);
  }

  public int getID(){
    return numUnique ;
  }

  public List<BilanOrtho> getListBilan(){
    return bilanortho ;
  }

}

class FicheSuivie implements Serializable{

  private List<Objective> listobjective =  new ArrayList<Objective>() ;
  private int note ;

  public void afficheInfo(){

    for (Objective objective : listobjective) {
      objective.afficheInfo();
    }
    System.out.println(note);

  }

  public void addObj(Objective obj1){
    listobjective.add(obj1);
  }

  public void setnote(Integer note){
    this.note=note;
  }

  public VBox printthis(){

    VBox handler = new VBox(10);

    for(Objective obj : listobjective){
      handler.getChildren().add(obj.addobj());
    }
    Label mark = new Label("La note de ce Fiche est : " + note);

    mark.setFont(PageHandler.font);

    handler.getChildren().add(mark);
    handler.setAlignment(Pos.CENTER);
    handler.setStyle("-fx-background-color:white;");
    return handler ;
  }


}

enum TypeObj{ courtTerme , moyenterme , longTerme }

class Objective implements Serializable{

  private String nomObj ;
  private TypeObj typeobj;

  Objective( String nomObj , TypeObj typeobj){
    this.nomObj = nomObj ;
    this.typeobj = typeobj ;
  }

  public void afficheInfo(){
    System.out.println(nomObj);
    System.out.println(typeobj.toString());
  }

  public VBox addobj(){

    VBox handler = new VBox(10);
    Label name = new Label("le nom d'objective est : "+ nomObj.toString());
    name.setFont(PageHandler.font);

    Label type = new Label("le type de cet objective est :" + typeobj.toString());
    type.setFont(PageHandler.font);

    handler.getChildren().addAll(name , type);
    handler.setAlignment(Pos.CENTER);
    handler.setStyle("-fx-background-color:white;");
    return handler ;

  }

}

abstract class Patient implements Serializable {

  protected String nom ;
  protected String prenom ;
  protected LocalDate dateNaissance ;
  protected String lieunaissance ;
  protected String adresse ;
  protected Dossier dossier ;
  
  Patient(String nom , String prenom){
    this.nom = nom ;
    this.prenom = prenom ;

  }

  public void setNewInfo(LocalDate dateNaiss , String lieu , String addresse){

    dateNaissance = dateNaiss ;
    lieunaissance = lieu ;
    this.adresse = addresse ;
    System.out.println("ID of new patient : " + UserData.nextIdSetter);
    dossier = new Dossier(UserData.nextIdSetter);
    UserData.setNextId();

  }

  public void afficheInfo(){

    System.out.println("n and p : "+nom.toString() + " " + prenom.toString());
    System.out.println("lieu de naissance : " + lieunaissance.toString());

  }

  public void ajouRDV(RendezVous rdv){
    dossier.addRDV(rdv);

  }

  public void ajouBilan(BilanOrtho bilan){

    dossier.addBilan(bilan);

  }

  public VBox afficherRdv(){
    return dossier.afficher();
  }

  public VBox afficheFiche(){
    return dossier.afficheFS();
  }

  public void addfi(FicheSuivie fich){
    dossier.addFSui(fich);
  }

  public void setDossier(Dossier dossier){
    this.dossier = dossier ;
  }

  public int getID(){
    return dossier.getID();
  }

  public HBox smallAffichage(){

    HBox contenue = new HBox(30) ;
    contenue.setPadding(new Insets(10,10,10,10));

    Label idtext = new Label("N°"+getID());
    idtext.setFont(PageHandler.font);

    Label nomText = new Label(nom) ;
    nomText.setFont(PageHandler.font);

    Label prenomText = new Label(prenom);
    prenomText.setFont(PageHandler.font);

    contenue.getChildren().addAll(idtext , nomText , prenomText) ;

    contenue.setStyle("-fx-background-color:white;"
    //"-fx-border-radius: 15; " +
    //"-fx-border-color: #000000; " +
    /* "-fx-border-width: 2; "*/ );


    return contenue ;

  }

  public List<BilanOrtho> getListBilan(){
    return dossier.getListBilan() ;
  }

  public abstract VBox afficher();

  public abstract double durreConsult() ;
  public abstract boolean isAdult() ;

}

class Adulte extends Patient {

  private String diplome ;
  private String profession ;
  private int numPersonel ;
  
  Adulte(String nom , String prenom){
    super(nom , prenom) ; 
  }

  public void setNewInfo(LocalDate dateNaiss , String lieu , String addresse , String diplome , String profession , int numPersonel){

    super.setNewInfo(dateNaiss, lieu, addresse);
    this.diplome = diplome ;
    this.profession = profession ;
    this.numPersonel = numPersonel ;

  }

  public boolean isAdult(){
    return true;
  }

  public double durreConsult(){
    return 1.5 ;
  }

  public void afficheInfo(){
    super.afficheInfo(); 
    System.out.println("son diplome : " + diplome.toString());
    System.out.println("proffession : "+profession.toString());
    System.out.println(numPersonel);
  }
  
  public VBox afficher(){
    VBox contenue = new VBox(10) ;
    Label titre = new Label(" Un Adulte  ");
    titre.setFont(PageHandler.font);
    Label name = new Label("le nom de ce patient est : " + nom.toString());
    name.setFont(PageHandler.font);
    Label prenom = new Label("le nom de ce patient est : " + super.prenom.toString());
    prenom.setFont(PageHandler.font);
    Label diplome = new Label("le nom de ce patient est : " + this.diplome.toString());
    diplome.setFont(PageHandler.font);
    Label profession = new Label("le nom de ce patient est : " + this.profession.toString());
    profession.setFont(PageHandler.font);
    Label numPersonel  = new Label("le nom de ce patient est : " + this.numPersonel);
    numPersonel .setFont(PageHandler.font);
    contenue.getChildren().addAll(titre , name , prenom , diplome, profession , numPersonel) ;
    
    contenue.setAlignment(Pos.CENTER);
    contenue.setStyle("-fx-background-color:white;");

    return contenue ;

  }
  

}

class Enfant extends Patient {

  private String classEtude ;
  private int numTel ;
  
  Enfant(String nom , String prenom){

    super(nom , prenom); 

  }

  public boolean isAdult(){
    return false ;
  }

  public void setNewInfo(LocalDate dateNaiss , String lieu , String addresse , String classEtude , int numTel){

    super.setNewInfo(dateNaiss, lieu, addresse);
    this.classEtude = classEtude ;
    this.numTel = numTel ;

  }

  public void afficherInfo(){
    super.afficheInfo(); ;
    System.out.println("classe : "+classEtude);
    System.out.println(numTel);
  }

  public double durreConsult(){
    return 2.5;
  }

  public VBox afficher(){
    
    VBox contenue = new VBox(10) ;

    Label titre = new Label(" Un Enfant  ");
    titre.setFont(PageHandler.font);

    Label name = new Label("le nom de ce patient est : " + nom.toString());
    name.setFont(PageHandler.font);

    Label prenom = new Label("le nom de ce patient est : " + super.prenom.toString());
    prenom.setFont(PageHandler.font);

    Label dateNaissance = new Label("le nom de ce patient est : " + super.dateNaissance.toString());
    dateNaissance.setFont(PageHandler.font);

    Label classEtude= new Label("le nom de ce patient est : " + this.classEtude.toString());
    classEtude.setFont(PageHandler.font);

    Label numTel= new Label("le nom de ce patient est : " + this.numTel);
    numTel.setFont(PageHandler.font);

    contenue.getChildren().addAll(titre , name , prenom , dateNaissance, classEtude, numTel) ;
    contenue.setAlignment(Pos.CENTER);
    contenue.setStyle("-fx-background-color:white;");

    return contenue ;

  }
   
}

class BilanOrtho implements Serializable{

  protected Epclinic[] epclinic ;
  protected Diagnostic diagnostic ;
  protected Projettheraph prjettherap ;

  BilanOrtho(Epclinic[] epclinic , Diagnostic diagnostic , Projettheraph prjettherap ){
    this.diagnostic=diagnostic;
    this.epclinic=epclinic;
    this.prjettherap=prjettherap;
  }

  public boolean isFirst(){

    return false ;

  }

  public GridPane getWidgetDiagno(){
    return diagnostic.itsWidget() ;
  }

  public GridPane getWidgetPrjpt(){
    return prjettherap.itsWidget() ;
  }

}

class FirstBilan extends BilanOrtho{

  protected Anamnese anamnese ;

  FirstBilan(Epclinic[] epclinic , Diagnostic diagnostic , Projettheraph prjettherap , Anamnese anamnese){
    super(epclinic, diagnostic, prjettherap);
    this.anamnese=anamnese;
  }

  public boolean isFirst(){

    return true ;

  }

  public GridPane getWidgetAna(){
    return anamnese.itsWidget() ;
  }
}

class Anamnese implements Serializable{

  protected QuestAna[] questana ;

  Anamnese(QuestAna[] questana){

    this.questana = questana ;

  }

  public boolean isResponsesEmpty(){

    for (QuestAna questAna : questana) {
      System.out.println("YAAAA");
      if(questAna.isResponseEmpty()) return true ;
    }

    System.out.println("TAA");
    return false ;

  }

  public GridPane itsGrid(){ //son affichage de récupération des données et les affecter

    int cpt = 0 ;

    GridPane grid = new GridPane() ;
    grid.setHgap(10);
    grid.setVgap(10);

    for (QuestAna questAna : questana) {
    
      //adding elements
      TextField responseTextField = new TextField() ;
      responseTextField.setPromptText("réponse");
      responseTextField.setFont(PageHandler.font);
      responseTextField.addEventHandler( KeyEvent.ANY ,  action -> questAna.setRep(responseTextField.getText()));

      Label textNom= new Label(questAna.getConsigne()) ;
      textNom.setFont(PageHandler.font);

      grid.add(textNom, 0, cpt);
      grid.add(responseTextField, 1, cpt);
      cpt++ ;

    }

    grid.setAlignment(Pos.CENTER);

    return grid ;

  }

  public GridPane itsWidget(){

    int cpt = 0 ;

    GridPane grid = new GridPane() ;
    grid.setHgap(10);
    grid.setVgap(10);

    for (QuestAna questAna : questana) {
    
      //adding elements
      Label textResponse= new Label(questAna.getRep()) ;
      textResponse.setFont(PageHandler.font);

      Label textNom= new Label(questAna.getConsigne()) ;
      textNom.setFont(PageHandler.font);

      grid.add(textNom, 0, cpt);
      grid.add(textResponse, 1, cpt);
      cpt++ ;

    }

    grid.setAlignment(Pos.CENTER);

    return grid ;

  } 

}

abstract class QuestAna implements Serializable{

  protected String consigne ; 
  protected String replibre = "" ;

  QuestAna(String consigne){
    this.consigne = consigne ;
  }

  public boolean isResponseEmpty(){
    return replibre.isEmpty();
  }

  public String getConsigne(){
    return consigne ;
  }

  public String getRep(){
    return replibre ;
  }

  public void setRep(String rep){
    replibre = rep ;
  }

}

class QuestEnfant extends QuestAna{

  private AnaEnf catenf ; 

  QuestEnfant(String consigne , AnaEnf categorie){
    super(consigne) ;
    catenf = categorie ;
  }

  public void afficherInfo(){
    System.out.println(catenf.toString());
  }
    
}

class QuestAdulte extends QuestAna{

  private AnaAdu catAdu ;

  QuestAdulte(String consigne , AnaAdu categorie){
    super(consigne) ;
    catAdu = categorie ;
  }

  public void afficherInfo(){
    System.out.println(catAdu.toString());
  }
}

enum AnaAdu{histoiremaladie , suivimedical ;

  public String itsName(){

    switch (this) {

      case histoiremaladie: return "Son histoire de maladie : " ;

    
      case suivimedical: return "Son suivi médicale : " ;

    }

    return "NullQuestion" ;

  }

}

enum AnaEnf{structure_familiale , dynamique_familiale , antecedants_familiaux ,
    conditions_natales , developpements_psychomoteur , developpement_engagier , 
  caractere_comportement ;

  public String itsName(){

    switch (this) {

      case structure_familiale: return "Son structure familiale : " ;
      case dynamique_familiale: return "Sa dynamique : " ;
      case antecedants_familiaux: return "Ses antécédents familiales : " ;
      case conditions_natales: return "Ses conditions natales : " ;
      case developpements_psychomoteur: return "Son développement psychomoteur : " ;
      case developpement_engagier: return "Son développement engagier : " ;
      case caractere_comportement: return "Son comportement comme caractère : " ;

    }

    return "NullQuestion" ;

  }

} 

class Epclinic implements Serializable{

  private String obsclin ; //observations cliniques de lorthophoniste 
  private Test test ;

  public void setObs(String observ){
    obsclin = observ  ;
  }

  public void setTest(Test test){
    this.test = test ;
  }

  public void afficherInfo(){
    System.out.println(obsclin);
    test.afficheInfo();
  }
}

abstract class Test implements Serializable{

  protected String nomtest;
  protected CompteRendu compterendu ;
  
  public void setComRendu(CompteRendu compterendu){
    this.compterendu = compterendu ;
  }

  public void afficheInfo(){
    System.out.println(nomtest);
    System.out.println(compterendu.toString());
  }
}

class CompteRendu implements Serializable{

  protected int scoreTotal ;
  protected String conclusion ; //observations finales 

  public void setconclusion(String conc){
    conclusion = conc;
  }

  public void setNote(int note){

    scoreTotal = note ;

  }

}

class Questionnaire extends Test{

  private Set<Question> listquest ;

  Questionnaire(Set<Question> listquest){
    this.listquest = new HashSet<Question>(listquest);
  }

  public void afficheInfo(){

    System.out.println(listquest.toString());

  }

}

abstract class Question implements Serializable{

  protected String ennonce ;
  protected double note ;

  Question(String ennonce , double note ){
    this.ennonce = ennonce ;
    this.note = note ; 
  }

}

class Replibre extends Question{
  private String reponse ;

  Replibre(String ennonce , int note ){
    super(ennonce , note);  
  }

  public void setRepense(String reponse){
    this.reponse = reponse ;
  }

  public void afficherInfo(){

    System.out.println(reponse.toString());

  }

}

class QCU extends Question{

  private String[] listprop ; 
  private String reponse ;

  QCU(String ennonce , int note , String[] listprop ){
    super(ennonce , note);
    this.listprop = listprop ;
  }
  
  public void setRepense(String reponse){
    this.reponse = reponse ;
  }

  public void afficheInfo(){

    System.out.println(listprop.toString());
    System.out.println(reponse);

  }

}

class QCM extends Question{

  private String[] listprop ;
  private String[] reponse ;
  
  QCM(String ennonce , int note  ,  String[] listprop ){
    super(ennonce , note);
    this.listprop = listprop ;
  }

  public void setRepense(String[] reponse){
    this.reponse = reponse ;
  }

  public void afficheInfo(){

    System.out.println(listprop.toString());
    System.out.println(reponse);

  }

}

class SerieExo extends Test{

  private Exercice[] exercice ;
  
  SerieExo(Exercice[] exercice){
    this.exercice = exercice ;
  }

  public void afficheInfo(){

    for (Exercice exo : exercice) {
      exo.afficheInfo();
    }

  }
   
}

class Exercice implements Serializable{

  private String consigne ;
  private double note  ;
  
  Exercice(String consigne , double note){
    this.consigne = consigne ;
    this.note = note ;
  }

  public void afficheInfo(){

    System.out.println(consigne.toString());
    System.out.println(note);

  }


}

class Exospec extends Exercice{

  private String matSpeci ;

  Exospec(String consigne , double note , String matSpeci){
    super(consigne , note);
    this.matSpeci = matSpeci ; 
  }

  public void afficheInfo(){

    System.out.println(matSpeci.toString());
  }

}

class Diagnostic implements Serializable{

  private Trouble[] troubles ;
  
  Diagnostic(Trouble[] trouble){
    this.troubles = trouble ;
  }

  public void afficheInfo(){

    for (Trouble trouble : troubles) {
      trouble.afficherInfo();
    } 

  }

  public GridPane itsWidget(){

    int cpt = 0 ;

    GridPane grid = new GridPane() ;
    grid.setHgap(10);
    grid.setVgap(10);

    for (Trouble trouble : troubles) {
    
      //adding elements

      Label textStart = new Label("Trouble N°"+(cpt+1)+ " : ") ;
      textStart.setFont(PageHandler.font);

      Label textResponse= new Label(trouble.getNomTrouble()) ;
      textResponse.setFont(PageHandler.font);

      Label textType= new Label("( " + trouble.getTroubleType().itsName() + " )") ;
      textType.setFont(PageHandler.font);

      grid.add(textStart, 0, cpt);
      grid.add(textResponse, 1, cpt);
      grid.add(textType, 2, cpt);

      cpt++ ;

    }

    grid.setAlignment(Pos.CENTER);

    return grid ;

  } 



}

class Trouble implements Serializable{

  private String nomTrouble ;
  private Troubles troubleType ; 

  public Trouble(String nomTrouble, Troubles troubleType) {
    this.nomTrouble = nomTrouble;
    this.troubleType = troubleType;
  }

  // Getters and setters
  public String getNomTrouble() {
    return nomTrouble;
  }

  public void setNomTrouble(String nomTrouble) {
    this.nomTrouble = nomTrouble;
  }

  public Troubles getTroubleType() {
    return troubleType;
  }

  public void setTroubleType(Troubles troubleType) {
    this.troubleType = troubleType;
  }

  public void afficherInfo(){

    System.out.println(nomTrouble.toString()+ " " + troubleType.toString());

  }

}

enum Troubles{deglutition , neuro_developpentaux , cognitifs ; 

  public String itsName(){

    switch (this) {
      case deglutition: return "deglutition" ;
      case neuro_developpentaux: return "neuro developpentaux" ;
      case cognitifs: return "cognitifs" ;
    }

    return "nullTrouble" ;

  }

};

class Projettheraph implements Serializable{

  private String texte ;

  Projettheraph(String text){
    texte = text;
  }
  
  public String gettexte(){
    return texte;
  }

  public GridPane itsWidget(){

    GridPane grid = new GridPane() ;
    grid.setHgap(10);
    grid.setVgap(10);

    //adding elements
    Label textDesc= new Label("Description : ") ;
    textDesc.setFont(PageHandler.font);

    Label textNom= new Label(texte) ;
    textNom.setFont(PageHandler.font);

    grid.add(textNom, 1, 0);
    grid.add(textDesc, 0, 0);

    grid.setAlignment(Pos.CENTER);

    return grid ;

  }
}

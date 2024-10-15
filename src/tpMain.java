import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.text.Font;

class UserData{

    public static int nextIdSetter = 10 ;

    public static List<Orthophoniste> comptes = new ArrayList<>() ;

    public static Orthophoniste orthophoniste = new Orthophoniste("Tati", "Youcef", "Alger", 559695180, "youcef.tati04@gmail.com", "youcef123");

    public static Map<Integer , Patient> mapPatients = new HashMap<Integer , Patient>() ;

    public static Anamnese anamneseKids ;
    public static Anamnese anamneseAdults ;

    public static Patient rechpatient(int num ){
        return mapPatients.get(num);
    }

    public static void setComptes(List<Orthophoniste> liste){
        comptes = liste ;
    }

    @SuppressWarnings("unchecked")
    public  static List<Orthophoniste> fetchOrthophonistes() {

        List<Orthophoniste> orthophonistes = new ArrayList<>();

        try {

            ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream("Inscription.dat"))) ;

            orthophonistes = (List<Orthophoniste>) in.readObject();
            setComptes(orthophonistes);

            in.close();

        } catch (FileNotFoundException e) {
            System.out.println("File doesn't exist");
        } catch ( ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

        return orthophonistes;
    }

    @SuppressWarnings("unchecked")
    public  static Map<Integer , Patient> fetchPatients() {

        Map<Integer , Patient> mapPatients = new HashMap<Integer , Patient>();

        try {

            ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream("Patients.dat"))) ;

            mapPatients = (Map<Integer , Patient>) in.readObject();
            UserData.mapPatients = mapPatients;

            UserData.nextIdSetter = 10 + mapPatients.size() ;

            in.close();

        } catch (FileNotFoundException e) {
            System.out.println("File doesn't exist");
        } catch ( ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

        return mapPatients;
    }

    public static void writeFileComptes(){

        try {

            ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("Inscription.dat"))) ;
            out.writeObject(comptes);
            System.out.println("Serielazition done succufully");
            out.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void writeFilePatients(){

        try {

            ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("Patients.dat"))) ;
            out.writeObject(mapPatients);
            System.out.println("Serielazition done succufully by patients");
            out.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static boolean checkConnexion(String email , String password){

        for (Orthophoniste orthophoniste : comptes) {
            
            if(email.equals(orthophoniste.getEmail()) && password.equals(orthophoniste.getPassword()) ){
                UserData.orthophoniste = orthophoniste ;
                return true ;
            }

        }

        return false ;

    }

    public static void setMap(Map<Integer , Patient> mappingPatients){

        mapPatients = mappingPatients ;

    }

    public static void setNextId(){

        nextIdSetter++ ;

    }

    public static void initAnamnese(){

        int cpt = 0 ;

        QuestAna[] questionsAdultes = new QuestAdulte[2] ;
        QuestAna[] questionsEnfants = new QuestEnfant[7] ;

        for (QuestAna questAna : questionsEnfants) {

            AnaEnf categorie = AnaEnf.values()[cpt] ;
            questAna = new QuestEnfant(categorie.itsName() , categorie);


            questionsEnfants[cpt] = questAna ;
            cpt++ ;
        }

        cpt = 0 ;

        for (QuestAna questAna : questionsAdultes) {

            AnaAdu categorie = AnaAdu.values()[cpt] ;
            questAna = new QuestAdulte(categorie.itsName() , categorie);


            questionsAdultes[cpt] = questAna ;
            cpt++ ;  

        }

        anamneseKids = new Anamnese(questionsEnfants);
        anamneseAdults = new Anamnese(questionsAdultes);

    }

}

class PageHandler{ //classe qui va gérer tous l'application

    public static Stage primaryStage ;
    public static Scene scene ;

    public static int width = 1000 , height = 500 ;

    public static Font font = new Font(20) ;

    public static void display(){

        primaryStage.setOnCloseRequest(action -> {

            if(ConfirmationBox.display("Exit confirmation", "Etes vous Sure de quitter ? Vos données seront automatiquement sauvegardés")){
                UserData.writeFileComptes() ;
                UserData.writeFilePatients();
            }else{
                action.consume();
            }
            
        });

        primaryStage.setTitle("Page d'acceuil");
        primaryStage.setScene(scene);
        primaryStage.show(); //affichage

    }

}

public class tpMain extends Application{

    public void start(Stage primaryStage){

        PageHandler.primaryStage = primaryStage ;
        PageHandler.scene = FirstPage.scene ;
        PageHandler.display() ;

    }

    public static void main(String[] args){

        UserData.initAnamnese();

        //initBackup() ;

        UserData.fetchOrthophonistes() ;
        UserData.fetchPatients() ;

        FirstPage.display(); //initialiser Scene de page d'accueil
        launch(args);

    }

    @SuppressWarnings("unused")
    public static void initBackup(){

        Trouble trouble1 = new Trouble("Trauma", Troubles.neuro_developpentaux) ;
        Trouble trouble2 = new Trouble("Hallucination", Troubles.cognitifs) ;
        //Trouble trouble3 = new Trouble("Ptsd", Troubles.deglutition) ;

        Patient patient1 = new Adulte("Hamroun", "Ali") ;
        ((Adulte)patient1).setNewInfo(LocalDate.now().minusYears(20) ,"Sidi Mhammed" , "Banannier", "Ingénieur d'état", "Informatique", 52365479 );

        Patient patient2 = new Enfant("Tati", "Youcef") ;
        ((Enfant) patient2).setNewInfo( LocalDate.now().minusYears(10) , "El-Kouds" , "BirKhadem", "Cm2", 559547113 );

        Patient patient3 = new Adulte("Seddak", "Houssine");
        ((Adulte)patient3).setNewInfo( LocalDate.now().minusYears(37) ,  "Hydra", "Mouhammadia", "Mécanicien", "Bricolage", 59847123);

        Patient[] patients = {patient1,patient2};
        Trouble[] troubles = {trouble1 , trouble2};
        //Trouble[] trou = {trouble3} ;

        Anamnese anam = UserData.anamneseAdults ;
        String[] chaines = {"Alérgie des grands espaces" , "accident impacteux au cerveau"} ;
        int cpt = 0 ;

        for (QuestAna quest : anam.questana) {
            quest.setRep(chaines[cpt]);
            cpt++ ;
        }

        Consulation firstRdv1 = new Consulation(LocalDateTime.now().minusWeeks(5), patient1) ;
        Consulation firstRdv2 = new Consulation(LocalDateTime.now().minusWeeks(5), patient2) ;
        Consulation firstRdv3 = new Consulation(LocalDateTime.now().minusWeeks(5), patient3) ;

        FirstBilan bilan = new FirstBilan(new Epclinic[1], new Diagnostic( troubles ) , new Projettheraph("Il faut une continuation de long term avec le patient"), anam) ;

        patient1.ajouBilan(bilan);
        patient2.ajouBilan(bilan);
        patient3.ajouBilan(bilan);

        patient1.ajouRDV(firstRdv1);
        patient2.ajouRDV(firstRdv2);
        patient3.ajouRDV(firstRdv3);

        Suivi suivi = new Suivi(LocalDateTime.now().minusWeeks(4) , false, patient2) ;

        
        Atelier atelier = new Atelier(LocalDateTime.now().minusWeeks(3), "trauma des cfs", Arrays.asList(patients)) ;

        UserData.mapPatients.put(patient1.getID(),patient1) ;
        UserData.mapPatients.put(patient2.getID(), patient2) ;
        UserData.mapPatients.put(patient3.getID(), patient3) ;

        Orthophoniste orthophoniste = new Orthophoniste("Tati", "Youcef", "Birkhadem", 559695180, "youcef@gmail.com", "youcef123");
        UserData.orthophoniste = orthophoniste ;
        UserData.comptes.add(orthophoniste);

        Patient patNew = new Adulte("Malika", "Souhila") ;

        RendezVous rdv1 = new Consulation(LocalDateTime.now().minusDays(3) , patNew) ;
        RendezVous rdv2 = new Suivi(LocalDateTime.now().minusDays(10), false, patient2);
        RendezVous rdv3 = new Atelier(LocalDateTime.now().plusDays(7), "thérapie énergitique", Arrays.stream(patients).collect(Collectors.toList()));

        UserData.orthophoniste.addRDV(rdv1);
        UserData.orthophoniste.addRDV(rdv2);
        UserData.orthophoniste.addRDV(rdv3);

    }

}

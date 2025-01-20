import java.util.ArrayList;

public class Main {

    public static void main (String[] fidioTahina) throws MyException {

//        Ensemble domaineHommeNom = new Ensemble();
//        domaineHommeNom.addObject(String.class);
//        Ensemble domaineHommePrenom = new Ensemble();
//        domaineHommePrenom.addObject(String.class);
//        Ensemble domaineHommeAge = new Ensemble();
//        domaineHommeAge.addObject(Integer.class);
//
//        ArrayList<Attribut> attributHomme = new ArrayList<>();
//        attributHomme.add(new Attribut("Nom", domaineHommeNom));
//        attributHomme.add(new Attribut("Prenom", domaineHommePrenom));
//        attributHomme.add(new Attribut("Age", domaineHommeAge));
//
//        Relation homme = new Relation(attributHomme);
//        homme.addNuplet("Dupont", "Pierre", 20);
//        homme.addNuplet("Durant", "Jean", 30);
//        homme.addNuplet("Martin", "Georges", 40);
//
//
//
//        Ensemble domaineVoitureMarque = new Ensemble();
//        domaineVoitureMarque.addObject(String.class);
//        Ensemble domaineVoitureType = new Ensemble();
//        domaineVoitureType.addObject(String.class);
//        Ensemble domaineVoitureProprietaire = new Ensemble();
//        domaineVoitureProprietaire.addObject(String.class);
//        Ensemble domaineVoitureChevaux = new Ensemble();
//        domaineVoitureChevaux.addObject(Integer.class);
//
//        ArrayList<Attribut> attributVoiture = new ArrayList<>();
//        attributVoiture.add(new Attribut("Marque", domaineVoitureMarque));
//        attributVoiture.add(new Attribut("Type", domaineVoitureType));
//        attributVoiture.add(new Attribut("Proprietaire", domaineVoitureProprietaire));
//        attributVoiture.add(new Attribut("Chevaux", domaineVoitureChevaux));
//
//        Relation voiture = new Relation(attributVoiture);
//        voiture.addNuplet("Tesla", "Model X", "Dupont", 300);
//        voiture.addNuplet("Citroen", "2 CV", "Durant", 2);
//        voiture.addNuplet("Citroen", "3 CV", "null", 3);
//
//
//        homme.jointureExterne(voiture, "droite", "Nom", "=", "Proprietaire").showNuplets();

        Ensemble domaineHommeNom = new Ensemble();
        domaineHommeNom.addObject(String.class);
        Ensemble domaineHommePrenom = new Ensemble();
        domaineHommePrenom.addObject(String.class);
        Ensemble domaineHommeNum = new Ensemble();
        domaineHommeNum.addObject(Integer.class);

        ArrayList<Attribut> listeAttributsHomme = new ArrayList<Attribut>();
        listeAttributsHomme.add(new Attribut("NumHomme", domaineHommeNum));
        listeAttributsHomme.add(new Attribut("Nom", domaineHommeNom));
        listeAttributsHomme.add(new Attribut("Prenom", domaineHommePrenom));

        Relation homme = new Relation(listeAttributsHomme);
        homme.addNuplet(1, "Dupont", "Pierre");
        homme.addNuplet(2, "Durand", "Jean");
        homme.addNuplet(3, "Martin", "Georges");


        Ensemble domaineLivreNum = new Ensemble();
        domaineLivreNum.addObject(Integer.class);

        ArrayList<Attribut> listeAttributsLivre = new ArrayList<Attribut>();
        listeAttributsLivre.add(new Attribut("NumLivre", domaineLivreNum));

        Relation livre = new Relation(listeAttributsLivre);
        livre.addNuplet(1);
        livre.addNuplet(2);


        Ensemble domaineNumHommePret = new Ensemble();
        domaineNumHommePret.addObject(Integer.class);
        Ensemble domaineNumLivrePret = new Ensemble();
        domaineNumLivrePret.addObject(Integer.class);

        ArrayList<Attribut> listeAttributsPret = new ArrayList<Attribut>();
        listeAttributsPret.add(new Attribut("NumHomme", domaineNumHommePret));
        listeAttributsPret.add(new Attribut("NumLivre", domaineNumLivrePret));

        Relation pret = new Relation(listeAttributsPret);
        pret.addNuplet(1, 1);
        pret.addNuplet(1, 2);
        pret.addNuplet(2, 1);

        pret.showNuplets();
        livre.showNuplets();
        pret.division(livre).showNuplets();


    }

}

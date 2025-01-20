import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public class Relation implements Cloneable {

    ArrayList<Attribut> atributs;

    public Relation() {
        atributs = new ArrayList<Attribut>();
    }

    public Relation (ArrayList<Attribut> atributs) {
        setAtributs(atributs);
    }

    public ArrayList<Attribut> getAtributs() {
        return atributs;
    }

    public void setAtributs(ArrayList<Attribut> atributs) {
        this.atributs = atributs;
    }

    public void setAtributs(Relation r2) {
        for (int i = 0; i < r2.atributs.size(); i++) {
            this.atributs.add(new Attribut(r2.atributs.get(i).name, r2.atributs.get(i).domaine));
        }
    }

    public Relation cloned () {
        Relation result = new Relation();
        for (int i = 0; i < this.atributs.size(); i++) {
            result.atributs.add(new Attribut(this.atributs.get(i)));
        }
        return result;
    }

    public Relation unionAvecDoublon (Relation r2) throws MyException {
        if (this.getAtributs().size() != r2.getAtributs().size()) {
            throw new MyException("Les relations ne sont pas compatibles pour union");
        }
        Relation rel = this.cloned();
        ArrayList<Object> listeValues = new ArrayList<>();
        for (int i = 0; i < rel.getAtributs().size(); i++) {
            rel.getAtributs().get(i).setDomaine(this.getAtributs().get(i).getDomaine().union(r2.getAtributs().get(i).getDomaine()));
        }

        for (int i = 0; i < r2.atributs.get(0).getValeurs().getElements().size(); i++) {
            for (int j = 0; j < r2.atributs.size(); j++) {
                listeValues.add(r2.atributs.get(j).getValeurs().getElements().get(i));
            }
            rel.addNuplet(listeValues);
            listeValues = new ArrayList<Object>();
        }


        return rel;
    }

    public Relation union (Relation r2) {
        if (r2.atributs.size() != this.atributs.size()) {
            System.out.println("Nombre d'attributs incompatibles");
            return this;
        }
        try {
            Relation r1 = this.cloned();
            Relation r2Clone = r2.cloned();
            Relation rel = r1.unionAvecDoublon(r2Clone);
            rel.removeNupletDoublon();

            return rel;
        } catch (MyException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public Relation intersection (Relation r2) {
        if (this.atributs.size() != r2.atributs.size()) {
            System.out.println("Nombre d'attributs incompatible");
            return this;
        }
        try {
            Relation r1 = this.cloned();
            Relation r2Clone = r2.cloned();
            r1.removeNupletDoublon();
            r2Clone.removeNupletDoublon();
            Relation union = r1.unionAvecDoublon(r2Clone);
            Relation rel = new Relation();
            rel.setAtributs(union);
            ArrayList<Integer> indexDoublons = union.getNupletDoublonIndex();

            for (int i = 0; i < indexDoublons.size(); i++) {
                rel.addNuplet(union.getNupletValues(indexDoublons.get(0)));
            }
            return rel;
        } catch (MyException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public Relation difference (Relation r2) {
        if (this.atributs.size() != r2.atributs.size()) {
            System.out.println("Nombre d'attributs incompatible");
            return this;
        }
        try {
            Relation r1 = this.cloned();
            Relation r2Clone = r2.cloned();
            r1.removeNupletDoublon();
            r2Clone.removeNupletDoublon();
            Relation union = r1.unionAvecDoublon(r2Clone);
            Relation rel = new Relation();
            rel.setAtributs(union);
            ArrayList<Integer> indexDoublons = union.getNupletNonDoublonIndex();

            for (int i = 0; i < indexDoublons.size(); i++) {
                rel.addNuplet(union.getNupletValues(indexDoublons.get(i)));
            }

            return rel;
        } catch (MyException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public Relation projection(String ... attributeNames) throws MyException {
        Relation rel = this.cloned();
        ArrayList<Attribut> listAttributs = new ArrayList<Attribut>();
        for (int i = 0; i < attributeNames.length; i++) {
            if (isValidAttribute(attributeNames[i])) {
                Attribut attribut = getAttributByName(attributeNames[i]);
                listAttributs.add(attribut);
            } else {
                System.out.println("Attribute "+ attributeNames[i] +" does not exist");
            }
        }

        rel.setAtributs(listAttributs);
        rel.removeNupletDoublon();
        return rel;
    }

    public Relation selectionMultiple(String... conditionFactors) {
        Relation result;
        ArrayList<Relation> listTemp = new ArrayList<Relation>();
        Relation checker = this.cloned();
        if (isValidFactors(conditionFactors)) {
            for (int i = 0; i < conditionFactors.length; i++) {
                String attribute1 = conditionFactors[i];
                String operator = conditionFactors[i+1];
                String attribute2 = conditionFactors[i+2];
                checker = checker.selection(attribute1, operator, attribute2);
                if (i+3 < conditionFactors.length) {
                    if (conditionFactors[i+3].equalsIgnoreCase("or")) {
                        listTemp.add(checker);
                        checker = this.cloned();
                    }
                } else {
                    listTemp.add(checker);
                }
                i += 3;
            }
            if (listTemp.isEmpty()) {
                return checker;
            } else if (listTemp.size() == 1) {
                return listTemp.get(0);
            }

            result = listTemp.get(0);
            for (int i = 1; i < listTemp.size(); i++) {
                result = result.union(listTemp.get(i));
            }
            return result;
        }
        return null;
    }

    public Relation selection (String attributeName, String operator, Object value) {
        Relation rel = new Relation();

        ArrayList<Attribut> listeAttribut = new ArrayList<Attribut>();
        for (Attribut attribut : this.atributs) {
            Attribut newAttribut = new Attribut(attribut.getName(), attribut.getDomaine());
            listeAttribut.add(newAttribut);
        }
        rel.setAtributs(listeAttribut);

        if (isValidAttribute(attributeName)) {
            Attribut attribut = getAttributByName(attributeName);
            for (int i = 0; i < attribut.getValeurs().getElements().size(); i++) {
                Object attributeValue = attribut.getValeurs().getElements().get(i).toString();
                Object compareValue = value.toString();
                if (isConditionValid(operator, attributeValue, compareValue)) {
                    for (int j = 0; j < atributs.size(); j++) {
                        rel.getAtributs().get(j).getValeurs().getElements().add(this.atributs.get(j).getValeurs().getElements().get(i));
                    }
                }

            }
        }

        return rel;
    }

    public Relation selection (String attributeName, String operator, String attributeName2) {
        Relation temp = this.cloned();
        Attribut attribut1 = new Attribut();
        Attribut attribut2 = new Attribut();

        if (isValidAttribute(attributeName) && isValidAttribute(attributeName2)) {
            if (attributeName.equals(attributeName2)) {
                ArrayList<Attribut> listeAtributDoublon = temp.getAttributDoublons();
                for (int i = 0; i < listeAtributDoublon.size(); i += 2) {
                    if (listeAtributDoublon.get(i).getName().equals(attributeName)) {
                        attribut1 = listeAtributDoublon.get(i);
                        attribut2 = listeAtributDoublon.get(i+1);
                    }
                }
            } else {
                attribut1 = temp.getAttributByName(attributeName);
                attribut2 = temp.getAttributByName(attributeName2);
            }
            for (int i = 0; i < attribut1.getValeurs().getElements().size(); i++) {
                Object o1 = attribut1.getValeurs().getElements().get(i);
                Object o2 = attribut2.getValeurs().getElements().get(i);
                if (!isConditionValid(operator, o1, o2)) {
                    temp.removeNuplet(i);
                    i--;
                }
            }
            return temp;
        } else {
            if (!isValidAttribute(attributeName) && !isValidAttribute(attributeName2)) {
                System.out.println("Condition male formulee");
                return temp;
            }
            if (!isValidAttribute(attributeName)) {
                Object value = attributeName;
                return temp.selection(attributeName2, operator, value);
            }
            if (!isValidAttribute(attributeName2)) {
                Object value = attributeName2;
                return temp.selection(attributeName, operator, value);
            }
        }
        return temp;
    }

    public Relation produitCartesien (Relation r2) {
        Relation result = new Relation();
        ArrayList<Attribut> listeAttribut = new ArrayList<>();
        for (Attribut attribut : this.atributs) {
            Attribut newAttribut = new Attribut(attribut.getName(), attribut.getDomaine());
            listeAttribut.add(newAttribut);
        }
        for (Attribut attribut : r2.atributs) {
            Attribut newAttribut = new Attribut(attribut.getName(), attribut.getDomaine());
            listeAttribut.add(newAttribut);
        }
        result.setAtributs(listeAttribut);

        ArrayList<Object> listValues = new ArrayList<Object>();
        for (int i = 0; i < this.atributs.get(0).getValeurs().getElements().size(); i++) {
            for (int j = 0; j < r2.atributs.get(0).getValeurs().getElements().size(); j++) {
                for (int k = 0; k < this.atributs.size(); k++) {
                    listValues.add(this.atributs.get(k).getValeurs().getElements().get(i));
                }
                for (int k = 0; k < r2.atributs.size(); k++) {
                    listValues.add(r2.atributs.get(k).getValeurs().getElements().get(j));
                }
                result.addNuplet(listValues);
                listValues = new ArrayList<>();
            }
        }

        return result;
    }

    public Relation jointureNaturelle (Relation r2) {
        Relation r1 = this.cloned();
        Relation r2Clone = r2.cloned();
        Relation produit = r1.produitCartesien(r2Clone);
        ArrayList<Attribut> listeAttribut = produit.getAttributDoublons();

        for (int i = 0; i < listeAttribut.size(); i += 2) {
            for (int j = 0; j < listeAttribut.get(i).getValeurs().getElements().size(); j++) {
                Object o1 = listeAttribut.get(i).getValeurs().getElements().get(j);
                Object o2 = listeAttribut.get(i+1).getValeurs().getElements().get(j);
                if (!o1.equals(o2)) {
                    produit.removeNuplet(j);
                    j--;
                }
            }
        }

        produit.removeAttributDoublon();
        return produit;
    }

    public Relation tetaJointure (Relation r2, String ... attribute) {
        Relation r1 = this.cloned();
        Relation r2Clone = r2.cloned();
        Relation produit = r1.produitCartesien(r2Clone);
        Relation result = new Relation();

        try {
            result = produit.selectionMultiple(attribute);
            return result;
        } catch (Exception e) {
            System.out.println("tetajointure invalid: "+ e.getMessage());
        }
        return result;
    }

    public Relation jointureExterne (Relation r2, String type, String ... attribute) {
        Relation r1 = this.cloned();
        Relation r2Clone = r2.cloned();
        Relation produit = r1.produitCartesien(r2Clone);
        Relation result = new Relation();

        try {
            result = produit.selectionMultiple(attribute);
            switch (type) {
                case "full" -> {
                    return r1.jointureExterne(r2, "gauche", attribute).union(r1.jointureExterne(r2, "droite", attribute));
                }
                case "gauche" -> {
                    String[] listNomAttributGauche = new String[r1.atributs.size()];
                    for (int i = 0; i < r1.atributs.size(); i++) {
                        listNomAttributGauche[i] = r1.atributs.get(i).getName();
                    }
                    Relation temp = result.projection(listNomAttributGauche);
                    temp.showNuplets();
                    for (int i = 0; i < r1.atributs.get(0).getValeurs().getElements().size(); i++) {
                        if (!temp.containsNuplet(r1.getNupletValues(i))) {
                            int resultAttributeIndex = 0;
                            for (int j = 0; j < r1.atributs.size(); j++) {
                                result.getAtributs().get(resultAttributeIndex).addNewValue(r1.atributs.get(j).getValeurs().getElements().get(i));
                                resultAttributeIndex++;
                            }
                            for (int j = 0; j < r2.atributs.size(); j++) {
                                result.getAtributs().get(resultAttributeIndex).addNewValue("null");
                                resultAttributeIndex++;
                            }
                        }
                    }
                }
                case "droite" -> {
                    String[] listNomAttributDroite = new String[r2.atributs.size()];
                    for (int i = 0; i < r2.atributs.size(); i++) {
                        listNomAttributDroite[i] = r2.atributs.get(i).getName();
                    }
                    Relation temp = result.projection(listNomAttributDroite);
                    for (int i = 0; i < r2.atributs.get(0).getValeurs().getElements().size(); i++) {
                        if (!temp.containsNuplet(r2.getNupletValues(i))) {
                            int resultAttributeIndex = 0;
                            for (int j = 0; j < r1.atributs.size(); j++) {
                                result.getAtributs().get(resultAttributeIndex).addNewValue("null");
                                resultAttributeIndex++;
                            }
                            for (int j = 0; j < r2.atributs.size(); j++) {
                                result.getAtributs().get(resultAttributeIndex).addNewValue(r2.atributs.get(j).getValeurs().getElements().get(i));
                                resultAttributeIndex++;
                            }
                        }
                    }
                }
                default -> {
                    return r1.jointureExterne(r2, "full", attribute);
                }
            }
        } catch (Exception e) {
            System.out.println("jointure externe invalid: "+ e.getMessage());
        }

        return result;
    }

    public Relation division(Relation r2) {
        Relation r1 = this.cloned();
        Relation r2Clone = r2.cloned();
        Relation result = new Relation();

        ArrayList<String> attributesDividende = r1.getAttributeNames();
        ArrayList<String> attributesReste = new ArrayList<>();
        for (int i = 0; i < attributesDividende.size(); i++) {
            if (!r2Clone.isValidAttribute(attributesDividende.get(i))) {
                attributesReste.add(attributesDividende.get(i));
            }
        }
        try {
            Relation r1Clone = r1.cloned();
            Relation r1Projection = r1Clone.projection(attributesReste.toArray(new String[attributesReste.size()]));
            r2Clone.removeNupletDoublon();
            Relation produit = r1Projection.produitCartesien(r2Clone);
            Relation difference = produit.difference(r1).projection(attributesReste.toArray(new String[attributesReste.size()]));
            result.setAtributs(r1Projection);
            for (int i = 0; i < r1Projection.atributs.get(0).getValeurs().getElements().size(); i++) {
                ArrayList<Object> nupletValues = r1Projection.getNupletValues(i); // a l'indice i
                if(!difference.containsNuplet(nupletValues)) {
                    result.addNuplet(nupletValues);
                }
            }
        } catch (MyException e) {
            System.out.println("Erreur de division : "+ e.getMessage());
            return null;
        }

        return result;

    }

    //
    public static Relation division(Relation R, Relation S) {
        // Création de la relation résultat
        Relation resultat = new Relation();
        resultat.atributs = new ArrayList<>();

        // On ne garde que les attributs de R qui ne sont pas dans S
        for (Attribut attrR : R.atributs) {
            if (!S.isValidAttribute(attrR.getName())) {
                resultat.atributs.add(attrR);
            }
        }

        // Pour chaque valeur possible des attributs résultants
        Ensemble valeursResultat = new Ensemble();

        for (Object valeurR : getValeursUniques(R, resultat.atributs).getElements()) {
            boolean aToutesLesValeurs = true;

            // Pour chaque combinaison de valeurs dans S
            for (Object valeurS : getValeursUniques(S, S.atributs).getElements()) {
                // Vérifie si la combinaison (valeurR, valeurS) existe dans R
                if (!existeDansRelation(R, valeurR, valeurS)) {
                    aToutesLesValeurs = false;
                    break;
                }
            }

            // Si valeurR est associée à toutes les valeurs de S
            if (aToutesLesValeurs) {
                valeursResultat.elements.add(valeurR);
            }
        }

        // Assigne les valeurs trouvées aux attributs du résultat
        for (Attribut attr : resultat.atributs) {
            attr.valeurs = valeursResultat;
        }

        return resultat;
    }

    /**
     * Récupère les valeurs uniques pour les attributs donnés dans une relation
     */
    private static Ensemble getValeursUniques(Relation relation, ArrayList<Attribut> attributs) {
        Ensemble valeurs = new Ensemble();
        valeurs.elements = new ArrayList<>();

        for (Attribut attr : attributs) {
            if (attr.valeurs != null && attr.valeurs.elements != null) {
                for (Object val : attr.valeurs.elements) {
                    if (!valeurs.elements.contains(val)) {
                        valeurs.elements.add(val);
                    }
                }
            }
        }
        return valeurs;
    }

    /**
     * Vérifie si une combinaison de valeurs existe dans la relation
     */
    private static boolean existeDansRelation(Relation relation, Object valeur1, Object valeur2) {
        // Pour chaque "ligne" de la relation
        for (int i = 0; i < relation.atributs.get(0).valeurs.elements.size(); i++) {
            boolean match = true;
            // Vérifie si les valeurs correspondent
            for (Attribut attr : relation.atributs) {
                Object valeurCourante = attr.valeurs.elements.get(i);
                if (!valeurCourante.equals(valeur1) && !valeurCourante.equals(valeur2)) {
                    match = false;
                    break;
                }
            }
            if (match) return true;
        }
        return false;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private ArrayList<String> getAttributeNames() {
        ArrayList<String> result = new ArrayList<>();
        for (Attribut attr : this.atributs) {
            result.add(attr.getName());
        }
        return result;
    }

    private boolean isValidFactors(String[] conditionFactors) {
        ArrayList<String> tab = new ArrayList<>(Arrays.asList(conditionFactors));
        for (int i = 0; i < tab.size(); i++) {
            if (tab.get(i).equalsIgnoreCase("and") || tab.get(i).equalsIgnoreCase("or")) {
                tab.remove(i);
                i--;
            }
        }
        if (tab.size() % 3 != 0)
            return false;
        return true;
    }

    private boolean containsNuplet(ArrayList<Object> listValues) {
        if (listValues.size() != atributs.size()) {
            return false;
        }
        for (int i = 0; i < atributs.get(0).getValeurs().getElements().size(); i++) {
            int counter;
            for (counter = 0; counter < listValues.size(); counter++) {
                if (!atributs.get(counter).getValeurs().getElements().get(i).equals(listValues.get(counter))) {
                    break;
                }
            }
            if (counter == listValues.size()) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<Object> getNupletValues (int index) {
        ArrayList<Object> result = new ArrayList<>();
        for (int i = 0; i < atributs.size(); i++) {
            result.add(atributs.get(i).getValeurs().getElements().get(index));
        }

        return result;
    }

    private ArrayList<Attribut> getAttributDoublons () {
        ArrayList<Attribut> result = new ArrayList<Attribut>();
        ArrayList<Attribut> listeAttributTemp = new ArrayList<Attribut>();
        listeAttributTemp = this.getAtributs();

        for (int i = 0; i < listeAttributTemp.size(); i++) {
            for (int j = i+1; j < listeAttributTemp.size(); j++) {
                if (i != j && listeAttributTemp.get(i).getName().equals(listeAttributTemp.get(j).getName())) {
                    result.add(listeAttributTemp.get(i));
                    result.add(listeAttributTemp.get(j));
                    break;
                }
            }
        }

        return result;
    }

    private void removeAttributDoublon () {
        for (int i = 0; i < atributs.size(); i++) {
            for (int j = i+1; j < atributs.size(); j++) {
                if (atributs.get(i).getName().equals(atributs.get(j).getName())) {
                    atributs.remove(j);
                    j--;
                }
            }
        }
    }

    private void removeNupletDoublon () {
        for (int i = 0; i < atributs.get(0).getValeurs().getElements().size(); i++) {
            for (int j = i+1; j < atributs.get(0).getValeurs().getElements().size(); j++) {
                int k;
                for (k = 0; k < atributs.size(); k++) {
                    Object o1 = atributs.get(k).getValeurs().getElements().get(i);
                    Object o2 = atributs.get(k).getValeurs().getElements().get(j);
                    if (!o1.getClass().getSimpleName().equals(o2.getClass().getSimpleName()) || isConditionValid("<>", o1, o2)) {
                        break;
                    }
                }
                if (k == atributs.size()) {
                    this.removeNuplet(j);
                    j--;
                }
            }
        }

    }

    private ArrayList<Integer> getNupletDoublonIndex () {
        ArrayList<Integer> listIndex = new ArrayList<>();
        for (int i = 0; i < atributs.get(0).getValeurs().getElements().size(); i++) {
            for (int j = i+1; j < atributs.get(0).getValeurs().getElements().size(); j++) {
                int k;
                for (k = 0; k < atributs.size(); k++) {
                    Object o1 = atributs.get(k).getValeurs().getElements().get(i);
                    Object o2 = atributs.get(k).getValeurs().getElements().get(j);
                    if (!o1.getClass().getSimpleName().equals(o2.getClass().getSimpleName()) || isConditionValid("<>", o1, o2)) {
                        break;
                    }
                }
                if (k == atributs.size()) {
                    listIndex.add(j);
                }
            }
        }

        return listIndex;
    }

    private ArrayList<Integer> getNupletNonDoublonIndex () {
        ArrayList<Integer> listIndex = new ArrayList<>();
        for (int i = 0; i < atributs.get(0).getValeurs().getElements().size(); i++) {
            int j;
            int k;
            for (j = i+1; j < atributs.get(0).getValeurs().getElements().size(); j++) {
                for (k = 0; k < atributs.size(); k++) {
                    Object o1 = atributs.get(k).getValeurs().getElements().get(i);
                    Object o2 = atributs.get(k).getValeurs().getElements().get(j);
                    if (!o1.getClass().getSimpleName().equals(o2.getClass().getSimpleName()) || isConditionValid("<>", o1, o2)) {
                        break;
                    }
                }
                if (k == atributs.size()) {
                    break;
                }
            }
            if (j == atributs.get(0).getValeurs().getElements().size()) {
                listIndex.add(i);
            } else {
                this.removeNuplet(j);
            }
        }

        return listIndex;
    }

    private Attribut getAttributByName (String name) {
        for (Attribut atribut : atributs) {
            if (name.equals(atribut.getName())) {
                return atribut;
            }
        }
        return null;
    }

    private int verifyAtributs (ArrayList<String> name) {
        int nbFalse = 0;
        for (int i = 0; i < atributs.size(); i++) {
            Attribut atribut = getAttributByName(name.get(i));
            if (atribut == null) {
                System.out.println("Attribute "+ name +" does not exist");
                nbFalse++;
            }
        }
        return nbFalse;
    }

    private boolean isValidAttribute(String attributeName) {
        for (Attribut atribut : atributs) {
            if (atribut.getName().equals(attributeName))
                return true;
        }
        return false;
    }

    public void addNuplet(ArrayList<String> nomAttribut, ArrayList<Object> listValues) {
        int nbFalse = verifyAtributs(nomAttribut);
        if (nbFalse > 0)
            return;

        try {
            for (int i = 0; i < nomAttribut.size(); i++) {
                Attribut atribut = getAttributByName(nomAttribut.get(i));
                atribut.checkValidObject(listValues.get(i));
            }
            for (int i = 0; i < nomAttribut.size(); i++) {
                Attribut attribut = getAttributByName(nomAttribut.get(i));
                attribut.addNewValue(listValues.get(i));
            }
        } catch (MyException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addNuplet(ArrayList<Object> listValues) {
        if (listValues.size() != atributs.size()) {
            System.out.println("Nombre d'argument non correct pour l'insertion");
            return;
        }
        try {
            for (int i = 0; i < atributs.size(); i++) {
                if (listValues.get(i) != null) {
                    Attribut atribut = atributs.get(i);
                    atribut.checkValidObject(listValues.get(i));
                }
            }
            for (int i = 0; i < atributs.size(); i++) {
                if (listValues.get(i) == null) {
                    Attribut atribut = atributs.get(i);
                    atribut.addNewValue("null");
                } else {
                    Attribut atribut = atributs.get(i);
                    atribut.addNewValue(listValues.get(i));
                }
            }
        } catch (MyException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addNuplet(Object ... listValues) {
        ArrayList<Object> newList = new ArrayList<>();
        Collections.addAll(newList, listValues);
        addNuplet(newList);
    }

    private void removeNuplet (int index) {
        for (int i = 0; i < atributs.size(); i++) {
            atributs.get(i).getValeurs().getElements().remove(index);
        }
    }

    private boolean isNumericConditionTrue (String operator, Double value1, Double value2) {
        return switch (operator) {
            case "=" -> Double.compare(value1, value2) == 0;
            case "<>" -> Double.compare(value1, value2) != 0;
            case ">" -> Double.compare(value1, value2) > 0;
            case ">=" -> Double.compare(value1, value2) >= 0;
            case "<" -> Double.compare(value1, value2) < 0;
            case "<=" -> Double.compare(value1, value2) <= 0;
            default -> false;
        };
    }

    private boolean isStringConditionTrue (String operator, String value1, String value2) {
        return switch (operator) {
            case "=" -> value1.equals(value2);
            case "<>" ->!value1.equals(value2);
            case ">" -> value1.compareToIgnoreCase(value2) > 0;
            case ">=" -> value1.compareToIgnoreCase(value2) >= 0;
            case "<" -> value1.compareToIgnoreCase(value2) < 0;
            case "<=" -> value1.compareToIgnoreCase(value2) <= 0;
            default -> false;
        };
    }

    private boolean isConditionValid (String operateur, Object o1, Object o2) {

        if (o1.getClass().getSimpleName().equals(o2.getClass().getSimpleName())) {
            String className = o1.getClass().getSimpleName();
            switch (className) {
                case "Integer", "Double", "Float":
                    double i1 = Double.parseDouble(o1.toString());
                    double i2 = Double.parseDouble(o2.toString());
                    return isNumericConditionTrue(operateur, i1, i2);
                case "String", "Boolean":
                    String s1 = o1.toString();
                    String s2 = o2.toString();
                    return isStringConditionTrue(operateur, s1, s2);
                default:
                    if (operateur.equals("=") && o1.equals(o2))
                        return true;
                    if (operateur.equals("<>") && !o1.equals(o2))
                        return true;
            }
        }

        return false;
    }

    private String getNumericOperator(String condition) {
        if (condition.contains("<=")) {
            return "<=";
        } else if (condition.contains("<")) {
            return "<";
        } else if (condition.contains(">=")) {
            return ">=";
        } else if (condition.contains(">")) {
            return ">";
        } else {
            return "=";
        }
    }

    private boolean isValidOperator(String operator) {
        if (operator.equals("=") || operator.equals("<>") || operator.equals(">") || operator.equals("<") || operator.equals("<=") || operator.equals(">=")) {
            return true;
        }
        return false;
    }

    public void showNuplets () {
        Object[][] tabToShow = new Object[atributs.get(0).getValeurs().getElements().size()+1][atributs.size()];
        int lineIndex = 0;
        for (int i = 0; i < atributs.size(); i++) {
            tabToShow[lineIndex][i] = atributs.get(i).getName();
        }
        lineIndex++;
        for (int i = 0; i < atributs.get(0).getValeurs().getElements().size(); i++) {
            for (int j = 0; j < atributs.size(); j++) {
                tabToShow[lineIndex][j] = atributs.get(j).getValeurs().getElements().get(i);

            }
            lineIndex++;
        }

        affichage.Affichage.afficherTable(tabToShow);
    }

}

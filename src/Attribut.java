import java.util.ArrayList;

public class Attribut {

    Ensemble domaine;
    String name;
    Ensemble valeurs;

    public Attribut () {
        valeurs = new Ensemble();
    }

    public Attribut(String nom, Ensemble domaine) {
        setDomaine(domaine);
        setName(nom);
        valeurs = new Ensemble();
    }

    public Attribut(Attribut autre){
        Ensemble domaine = new Ensemble(autre.domaine.elements);
        ArrayList<Object> values = new ArrayList<>(autre.valeurs.elements);
        this.setName(autre.getName());
        this.setDomaine(domaine);
        this.setValeurs(new Ensemble(values));
    }

    public Ensemble getDomaine() {
        return domaine;
    }

    public void setDomaine(Ensemble domaine) {
        this.domaine = domaine;
    }

    public String getName() {
        return name;
    }

    public void setName(String nom) {
        this.name = nom;
    }

    public Ensemble getValeurs() {
        return valeurs;
    }

    public void setValeurs(Ensemble valeurs) {
        this.valeurs = valeurs;
    }

    public void addNewValue (Object obj) {
        valeurs.getElements().add(obj);
    }

    public boolean containsValue (Object obj) {
        for (int i = 0; i < valeurs.getElements().size(); i++) {
            if (valeurs.getElements().get(i).equals(obj))
                return true;
        }
        return false;
    }

    public void checkValidObject (Object obj) throws MyException {
        if (obj == null || obj.equals("null")) {
            return;
        }
        for (int i = 0; i < domaine.getElements().size(); i++) {
            if (domaine.getElements().get(i) instanceof Class<?>) {
                if (obj.getClass().getSimpleName().equals(((Class<?>) domaine.getElements().get(i)).getSimpleName())) {
                    return;
                }
            } else if (domaine.getElements().get(i).getClass().getSimpleName().equals(obj.getClass().getSimpleName())) {
                if (isConditionValid("=", domaine.getElements().get(i), obj)) {
                    return;
                }
            }
        }

        throw new MyException("Invalid object");
    }


    public boolean isNumericConditionTrue (String operator, Double value1, Double value2) {
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

    public boolean isStringConditionTrue (String operator, String value1, String value2) {
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

    public boolean isConditionValid (String operateur, Object o1, Object o2) {

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


}

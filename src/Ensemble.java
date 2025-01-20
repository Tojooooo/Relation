import java.util.ArrayList;

public class Ensemble {

    ArrayList<Object> elements;

    public Ensemble () {
        elements = new ArrayList<Object>();
    }

    public Ensemble (ArrayList<Object> elements) {
        this.elements = elements;
    }

    public ArrayList<Object> getElements() {
        return elements;
    }

    public void setElements(ArrayList<Object> elements) {
        this.elements = elements;
    }

    public boolean contains (Object obj) {
        for (int i = 0; i < elements.size(); i++) {
            if (obj.equals(elements.get(i))) {
                return true;
            }
        }
        return false;
    }

    public void removeDoublon () {
        for (int i = 0; i < elements.size(); i++) {
            for (int j = i+1; j < elements.size(); j++) {
                if (elements.get(i).equals(elements.get(j))) {
                    elements.remove(j);
                    j--;
                    break;
                }
            }
        }
    }

    public ArrayList<Object> getDoublon () {
        ArrayList<Object> result = new ArrayList<Object>();
        for (int i = 0; i < elements.size(); i++) {
            for (int j = i+1; j < elements.size(); j++) {
                if (elements.get(i).equals(elements.get(j))) {
                    result.add(elements.get(i));
                    break;
                }
            }
        }

        return result;
    }

    public Ensemble getNonDoublon() {
        Ensemble result = new Ensemble();
        for (int i = 0; i < elements.size(); i++) {
            boolean check = true;
            int j;
            for (j = i+1; j < elements.size(); j++) {
                if (elements.get(i).equals(elements.get(j))) {
                    check = false;
                    elements.remove(j);
                    break;
                }
            }
            if (check) {
                result.elements.add(elements.get(i));
            }
        }
        return result;
    }

    public void addObject (Object object) {
        this.elements.add(object);
    }

    public int card (Ensemble e) {
        return e.elements.size();
    }

    public Ensemble union (Ensemble e) {
        Ensemble result = new Ensemble ();

        this.removeDoublon();
        e.removeDoublon();

        result.elements.addAll(elements);
        result.elements.addAll(e.elements);
        result.removeDoublon();

        System.out.println(result.elements);

        return result;
    }

    public Ensemble intersection (Ensemble e) {
        Ensemble result = new Ensemble ();

        this.removeDoublon();
        e.removeDoublon();

        result.elements.addAll(elements);
        result.elements.addAll(e.elements);
        result.elements = result.getDoublon();

        System.out.println(result.elements);

//        for (int i = 0; i < result.elements.size(); i++) {
//            if(!isSameObject(result.elements.get(i), result.elements.get(i+1))) {
//                result.elements.remove(i);
//                i--;
//            } else {
//                result.elements.remove(i);
//            }
//        }


        return result;
    }

    public Ensemble diff (Ensemble e) {
        Ensemble result = new Ensemble ();

        this.removeDoublon();
        e.removeDoublon();

        result.elements.addAll(elements);
        result.elements.addAll(e.elements);

        result = result.getNonDoublon();

        System.out.println(result.elements);

//        for (int i = 0; i < result.elements.size(); i++) {
//            if (isSameObject(result.elements.get(i), result.elements.get(i+1))) {
//                result.elements.remove(i);
//                result.elements.remove(i+1);
//                i -= 2;
//            }
//        }

        return result;
    }

}

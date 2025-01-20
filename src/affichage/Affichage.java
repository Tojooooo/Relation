package affichage;

public class Affichage {

    public static void afficherTable (Object[][] tab) {
        if (tab == null)
            return;
        int[] forTabulation = getMaxCharacterPerColumn(tab);
        for (int j = 0; j < tab[0].length; j++) {
            for (int k = 0; k < (forTabulation[j] + 4 - forTabulation[j]%4); k++) {
                System.out.print("-");
            }
            System.out.print("+");
        }
        System.out.println();
        for (int i = 0; i < tab.length; i++) {
            for (int j = 0; j < tab[i].length; j++) {
                System.out.print(tab[i][j]);
                printTabulation(tab[i][j], forTabulation[j]);
                System.out.print("|");
            }
            if (i == 0) {
                System.out.println();
                for (int j = 0; j < tab[i].length; j++) {
                    for (int k = 0; k < (forTabulation[j] + 4 - forTabulation[j]%4); k++) {
                        System.out.print("-");
                    }
                    System.out.print("+");
                }
            }
            System.out.println();
        }
        for (int j = 0; j < tab[0].length; j++) {
            for (int k = 0; k < (forTabulation[j] + 4 - forTabulation[j]%4); k++) {
                System.out.print("-");
            }
            System.out.print("+");
        }
        System.out.println("\n");
    }

    public static int[] getMaxCharacterPerColumn(Object[][] tab) {
        int[] max = new int[tab[0].length];
        for(int i = 0; i < tab[0].length; i++) {
            max[i] = 0;
            for(int j = 0; j < tab.length; j++) {
                if (tab[j][i] != null) {
                    if(tab[j][i].toString().length() > max[i]) {
                        max[i] = tab[j][i].toString().length();
                    }
                }
            }
        }

        return max;
    }

    public static void printTabulation (Object obj, int nbChar) {
        StringBuilder str;
        if (obj == null) {
            str = new StringBuilder("null");
        } else {
            str = new StringBuilder(obj.toString());
        }
        nbChar += 4 - nbChar%4;
        while (str.length() < nbChar) {
            str.append(" ");
            System.out.print(" ");
        }
    }

}

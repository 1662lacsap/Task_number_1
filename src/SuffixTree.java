/*
Zadanie 1
Majac dany zbior Z złozony z k lancuchow, chcemy sprawdzic ile z tych lancuchow jest podlanuchem jakiegos
innego lanucha ze zbioru Z.

Zakladajac ze suma dlugosci wszystkich lanuchow wynosi m, zaprojektuj i zaimplementuj algorytm rozwiazujacy
to zadanie w czasie O(m)
*/

//Definicje:
//s - tekst, ciąg symboli s = s1,s2,...sn nalezacych do alfabetu
//n - Dlugosc tekstu (liczba jego elementow)
//p -pattern (wzorzec)

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class SuffixTree {

    private static CharSequence s = "";

    public static class Node {

        int begin;
        int end;
        int depth; //distance in characters from root to this node
        Node parent;
        Node suffixLink;

        Map<Character, Node> children;  //zamiast Node[] children

        //Sluzy do wyznaczenia liczby osiagalnych wierzcholkow reprezentujacych sufiksy dla kazdego wezla
        // drzewa sufiksowego
        int numberOfLeaves;             //zliczamy liscie

        Node(int begin, int end, int depth, int noleaf, Node parent) {

            this.begin = begin;
            this.end = end;
            this.depth = depth;
            this.parent = parent;

            children = new HashMap<>();
            numberOfLeaves = noleaf;


        }
    }

    private static Node buildSuffixTree(CharSequence s) {

        SuffixTree.s = s;

        int n = s.length();

        Node root = new Node(0, 0, 0, 0, null);
        Node node = root;

        for (int i = 0, tail = 0; i < n; i++, tail++) {

            //ustaw ostatni stworzony węzeł wewnętrzny na null przed rozpoczeciem kazdej fazy.
            Node last = null;

            while (tail >= 0) {
                Node ch = node.children.get(s.charAt(i - tail));
                while (ch != null && tail >= ch.end - ch.begin) {

                    //liscie
                    node.numberOfLeaves++;

                    tail -= ch.end - ch.begin;
                    node = ch;
                    ch = ch.children.get(s.charAt(i - tail));
                }

                if (ch == null) {
                    // utworz nowy Node z biezacym znakiem
                    node.children.put(s.charAt(i),
                            new Node(i, n, node.depth + node.end - node.begin, 1, node));

                    //liscie
                    node.numberOfLeaves++;

                    if (last != null) {
                        last.suffixLink = node;
                    }
                    last = null;
                } else {
                    char t = s.charAt(ch.begin + tail);
                    if (t == s.charAt(i)) {
                        if (last != null) {
                            last.suffixLink = node;
                        }
                        break;
                    } else {
                        Node splitNode = new Node(ch.begin, ch.begin + tail,
                                node.depth + node.end - node.begin, 0, node);
                        splitNode.children.put(s.charAt(i),
                                new Node(i, n, ch.depth + tail, 1, splitNode));

                        //liscie
                        splitNode.numberOfLeaves++;

                        splitNode.children.put(t, ch);

                        //liscie
                        splitNode.numberOfLeaves += ch.numberOfLeaves;

                        ch.begin += tail;
                        ch.depth += tail;
                        ch.parent = splitNode;
                        node.children.put(s.charAt(i - tail), splitNode);

                        //liscie
                        node.numberOfLeaves++;

                        if (last != null) {
                            last.suffixLink = splitNode;
                        }
                        last = splitNode;
                    }
                }
                if (node == root) {
                    --tail;
                } else {
                    node = node.suffixLink;
                }
            }
        }
        return root;
    }


    private static void print(CharSequence s, int i, int j) {
        for (int k = i; k < j; k++) {
            System.out.print(s.charAt(k));
        }
    }

    // Jesli chcemy wydrukowac drzewo nalezy odkomentowac w main
    private static void printTree(Node n, CharSequence s, int spaces) {
        int i;
        for (i = 0; i < spaces; i++) {
            System.out.print("␣");
        }
        print(s, n.begin, n.end);
        System.out.println("␣" + (n.depth + n.end - n.begin));

        for (Node child : n.children.values()) {
            if (child != null) {
                printTree(child, s, spaces + 4);
            }
        }

    }

    /*##########################################################################################*/
    //Budujemy drzewo sufiksowe dla calego zbioru lancuchow zamiast jednego lancucha,
    //tzw. uogolnione drzewo sufiksowe. Lączymy wszystkie lancuchy w jeden lancuch za pomoca wybranych znaków separatora
    //i konstrujemy drzewo sufiksowe dla takiego dlugiego lancucha - liscie zliczamy wczesniej w czasie tworzenia
    //tego drzewa.

    // w uogolnionym drzewie sufiksowym
    // szukamy gdzie ilosc lisci dla wyszukiwanego wzorca jest wieksza od 1
    private static boolean isContiguousSequenceCharactersString(Node root, CharSequence p) {

        Node actualNode = root;
        int index_p = 0;
        int index_s;
        int length_p = p.length();

        while (index_p < length_p) {
            actualNode = actualNode.children.get(p.charAt(index_p));

            if (actualNode == null) {
                return false;
            }

            index_s = actualNode.begin;


            do {

                if (p.charAt(index_p++) != s.charAt(index_s++))

                    return false;

            } while
            (index_s < actualNode.end && index_p < length_p);

        }

        return actualNode.numberOfLeaves > 1;
    }


    //Suma True z wczesniejszej funkcji - liczymy liczbe podlancuchow wiekszych niz 1.
    private static int numberOfSubstrings(Node root, Set<CharSequence> chains) {

        int numberOfSubstrings = 0;

        for (CharSequence p : chains) {
            if (isContiguousSequenceCharactersString(root, p))
                numberOfSubstrings++;
        }

        return numberOfSubstrings;
    }

    /*##########################################################################################*/

    // main - Test
    public static void main(String[] args) {

        //Test1 - przyklad 1

        Set<CharSequence> chains = new HashSet<>();

        //Zbior Z przykladowe lancuchy, w tym przykladzie tylko "szkola" i "samochod"
        //jest podlancuchem w innym lancuchu - zatem wynik 2

        chains.add("uczelniaszkola");
        chains.add("szkola");
        chains.add("szkolasamochodszkola");
        chains.add("samochod");
        chains.add("babcia");
        chains.add("kanarek");
        chains.add("zegarek");
        chains.add("muzeum");
        chains.add("punto");
        chains.add("miasto");

        StringBuilder stringBuilder = new StringBuilder();

        for (CharSequence chain : chains) {
            stringBuilder.append(chain).append('$');
        }
        String k_chain = stringBuilder.toString();

        //lub
        /*
        String k_chain = "";
        for (CharSequence chain : chains) {
            k_chain += chain + "$";
        }
        */

        //wyswietla zląnczony lancuch za pomoca wybranego znaku separatora
        System.out.println(" ");
        System.out.println("Zlączony jeden dlugi lancuch za pomoca wybranego znaku separatora z badanego zbioru Z.\n" +
                "W tym przykladzie tylko \"szkola\" i \"samochod\"" +
                "jest podlancuchem w innym lancuchu - zatem wynik 2."+"\nOto jeden dlugi lancuch:" );
        System.out.println(k_chain);
        System.out.println(" ");

        // String s z k lancuchow
        String s = k_chain;

        //Uogolnione drzewo sufiksowe
        Node root = buildSuffixTree(s);

        //Jesli chcemy wydrukowac drzewo nalezy odkomentowac
        //printTree(root, s, 0);

        System.out.println(" ");
        System.out.println("Wynik: ");
        System.out.println(numberOfSubstrings(root, chains) + " lancuch(y) są podlanuchami jakiegos innego lancucha " +
                "z badanego zbioru Z");

        //koniec Test-u 1



        //Test2 - przyklad 2
        Set<CharSequence> chains2 = new HashSet<>();

        //Zbior Z przykladowe lancuchy, w tym przykladzie tylko "ala" i "c"
        //jest podlancuchem w innym lancuchu - zatem wynik 2

        chains2.add("ala");
        chains2.add("alama");
        chains2.add("xc");
        chains2.add("c");

        String k_chain2 = "ala$alama%xc&c#";


        //wyswietla zląnczony lancuch za pomoca wybranych znaków separatora
        System.out.println(" ");
        System.out.println("Zlączony jeden dlugi lancuch za pomoca wybranych znaków separatora z badanego zbioru Z.\n" +
                "W tym przykladzie tylko \"ala\" i \"c\"" +
                "jest podlancuchem w innym lancuchu - zatem wynik 2."+"\nOto jeden dlugi lancuch:" );
        System.out.println(k_chain2);
        System.out.println(" ");

        // String s z k lancuchow
        String s2 = k_chain2;

        //Uogolnione drzewo sufiksowe
        Node root2 = buildSuffixTree(s2);

        //Jesli chcemy wydrukowac drzewo nalezy odkomentowac
        //printTree(root2, s2, 0);

        System.out.println(" ");
        System.out.println("Wynik: ");
        System.out.println(numberOfSubstrings(root2, chains2) + " lancuch(y) są podlanuchami jakiegos innego lancucha " +
                "z badanego zbioru Z");

        //koniec Test-u 2

    }

}

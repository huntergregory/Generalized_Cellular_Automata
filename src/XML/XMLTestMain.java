package XML;

public class XMLTestMain {
    static XMLParser myParser;

    XMLTestMain() {
        myParser = new XMLParser();
    }

    public static void main(String[] args) {
        XMLTestMain tester = new XMLTestMain();
        myParser.parseFile("automata/fire-test-config.xml");
        System.out.println("here");
        System.out.println(myParser.getCAType());
        /*System.out.println(myParser.getConfiguration());
        System.out.println(myParser.getGridSize());
        System.out.println(myParser.getIsRandom());
        System.out.println(myParser.getParameters());
        System.out.println(myParser.getRandomMakeup());*/
    }
}

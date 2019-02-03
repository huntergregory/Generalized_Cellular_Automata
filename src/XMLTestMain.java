
public class XMLTestMain {
    XMLParser myParser;

    XMLTestMain() {
        myParser = new XMLParser();
    }

    public static void main(String[] args) {
        myParser.parseFile();
    }
}

import java.util.List;

public class Main {

    public static void main(String[] args) {
        IWebCrawler webCrawler = new WebCrawler();
        List<String> links = webCrawler.getTopLibraries("java");
        links.forEach(System.out::println);
    }
}

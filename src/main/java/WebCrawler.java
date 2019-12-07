import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.stream.Collectors;

public class WebCrawler implements IWebCrawler {

    private static final String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36";
    private static final String googleSearchQuery =  "https://www.google.com/search?q=";

    WebCrawler() {
    }

    /**
     * Get top 5 most used js libraries
     *
     * @param query
     * @return
     */
    public List<String> getTopLibraries(String query) {
        return getTopLibraries(query, 5);
    }

    /**
     * Get the most used libraries
     *
     * @param query
     * @param count
     * @return
     */
    public List<String> getTopLibraries(String query, int count) {
        Map<String, Integer> libsFrequency = new HashMap<>();
        String url = googleSearchQuery + query;
        try {
            String htmlPage = parseResponse(url);
            List<String> links = getLinksList(htmlPage);

            for (String link : links) {
                List<String> libs = getUsedJsLibraries(parseResponse(link));
                for (String lib : libs) {
                    if (libsFrequency.containsKey(lib)) {
                        libsFrequency.put(lib, libsFrequency.get(lib) + 1);
                    } else {
                        libsFrequency.put(lib, 1);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error occurred: " + e.getLocalizedMessage());
        }

        return getTopPopularLibraries(count, libsFrequency);
    }

    /**
     * Connect to given url and return response page as html string
     *
     * @param url
     * @return
     * @throws IOException
     */
    private String parseResponse(String url) throws IOException {
        InputStream inputStream = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            URLConnection openConnection = new URL(url).openConnection();
            openConnection.addRequestProperty("User-Agent", userAgent);
            inputStream = openConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            System.out.println("Error occurred: " + e.getLocalizedMessage());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return stringBuilder.toString();
    }

    /**
     * Get list of links provided by google search
     * @param html
     * @return
     */
    private List<String> getLinksList(String html) {
        // hardcoded for google search result page
        Elements searchRes  = Jsoup.parse(html).select("#search");
        Elements linksObjects = searchRes.select("a[href]:not([class])");
        List<String> links = new ArrayList<>();
        for (Element el : linksObjects) {
            links.add(el.attr("href"));
        }

        return links;
    }

    /**
     * Retrieve sources from provided html page
     *
     * @param html
     * @return
     */
    private List<String> getSourcesFromPage(String html) {
        return Jsoup.parse(html)
                .select("script")
                .stream()
                .map(element -> element.attr("src"))
                .filter(src -> !StringUtil.isBlank(src))
                .collect(Collectors.toList());
    }

    /**
     * Get list of names of used js libraries
     * @param html
     * @return
     */
    private List<String> getUsedJsLibraries(String html) {
        // return only correct js sources
        return getSourcesFromPage(html)
                .stream()
                .map(source -> source.substring(getStartOfName(source),
                        getEndOfLibName(getStartOfName(source), source)))
                .filter(source -> source.contains(".js"))
                .collect(Collectors.toList());
    }

    /**
     * Get start point of library's name
     *
     * @param lib
     * @return
     */
    private int getStartOfName(String lib) {
        return lib.lastIndexOf('/') + 1;
    }

    /**
     * Get end point of library's name
     *
     * @param start
     * @param lib
     * @return
     */
    private int getEndOfLibName(int start, String lib) {
        int paramsStart = lib.indexOf('?');
        return paramsStart == -1 || paramsStart < start ?
                lib.length() : paramsStart;
    }

    /**
     * Sort given map
     * (where key is library's name and value is number of occurrences)
     * and return top used names
     *
     * @param count
     * @param libsFrequency
     * @return
     */
    private List<String> getTopPopularLibraries(int count,
                                                Map<String, Integer> libsFrequency) {
        Map<String, Integer> result = libsFrequency
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue()
                        .reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));
        return new ArrayList<>(result.keySet())
                .subList(0, Math.min(count, result.keySet().size()));
    }
}

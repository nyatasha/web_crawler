import java.util.List;

public interface IWebCrawler {

    /**
     * Top 5 most used js libraries taking into account result
     * returned by google by given query
     *
     * @param query
     * @return
     */
    List<String> getTopLibraries(String query);

    /**
     * Top "count"(by default 5) most used js libraries taking into account result
     * returned by google by given query
     *
     * @param query
     * @param count
     * @return
     */
    List<String> getTopLibraries(String query, int count);
}

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class WebCrawlerIntegrationTest {

    @Test
    public void testTopDefaultLibraries() {
        IWebCrawler webCrawler = new WebCrawler();
        int count = 3;
        List<String> libs = webCrawler.getTopLibraries("test", count);

        Assert.assertEquals(count, libs.size());
        Assert.assertEquals("adsbygoogle.js", libs.get(0));
    }

    @Test
    public void testTop5Libraries() {
        IWebCrawler webCrawler = new WebCrawler();
        List<String> libs = webCrawler.getTopLibraries("angular");

        Assert.assertEquals(5, libs.size());
        Assert.assertEquals("base.js", libs.get(0));
    }
}

package nl.insomnia247.nailbiter.eldrinkopubbot.util;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import org.apache.log4j.Logger;


/**
 * @author Alex Leontiev
 */
public class DownloadCache {
    protected final static Map<String,String> _DATA 
        = Collections.synchronizedMap(new HashMap<String,String>());
    protected final static String _PREFIX = "f0bc74ce18191c931410";
    private String _ext = null;
    private final static Logger _Log = Logger.getLogger(DownloadCache.class);
    public DownloadCache(String ext) {
        _ext = ext;
    }
    public String get(URL u) {
        String url = u.toString();

        if(_DATA.contains(url)) {
            String res = _DATA.get(url);
            _Log.info(String.format("cache hit for \"%s\" -> \"%s\"",url,res));
            return res;
        } else {
            _Log.info(String.format("cache miss for \"%s\"",u));
            String fileName = String.format("/tmp/%s_%d%s"
                    ,_PREFIX
                    ,url.hashCode()
                    ,_ext
                    );
            ReadableByteChannel readableByteChannel = Channels.newChannel(u.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            FileChannel fileChannel = fileOutputStream.getChannel();
            fileOutputStream.getChannel()
              .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            _Log.info(String.format("saved to %s",fileName));
            _DATA.put(url,fileName);
            return fileName;
        }
    }
}

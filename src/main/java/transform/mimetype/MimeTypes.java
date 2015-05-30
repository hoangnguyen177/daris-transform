package transform.mimetype;

import java.util.ArrayList;
import java.util.List;

import arc.dtype.Fuzzy;
import arc.mime.MimeType;
import arc.mime.NamedMimeType;

public class MimeTypes {

    public static final MimeType CONTENT_UNKNOWN = new NamedMimeType(
            "content/unknown");

    public static final MimeType TEXT_XML = new NamedMimeType("text/xml") {

        @Override
        public Fuzzy compressable() {
            return Fuzzy.YES;
        }

        @Override
        public String defaultExtension() {
            return "xml";
        }

        @Override
        public String description() {
            return "text XML.";
        }

        @Override
        public List<String> extensions() {
            List<String> l = new ArrayList<String>();
            l.add("xml");
            l.add("XML");
            return l;
        }
    };

    public static final MimeType APPLICATION_ZIP = new NamedMimeType(
            "application/zip") {

        @Override
        public Fuzzy compressable() {
            return Fuzzy.NO;
        }

        @Override
        public String defaultExtension() {
            return "zip";
        }

        @Override
        public String description() {
            return "zip archive.";
        }

        @Override
        public List<String> extensions() {
            List<String> l = new ArrayList<String>();
            l.add("zip");
            l.add("ZIP");
            return l;
        }
    };

    public static final MimeType APPLICATION_KEPLER_KAR = new NamedMimeType(
            "application/kepler-kar") {

        @Override
        public Fuzzy compressable() {
            return Fuzzy.NO;
        }

        @Override
        public String defaultExtension() {
            return "kar";
        }

        @Override
        public String description() {
            return "Kepler workflow kar archive.";
        }

        @Override
        public List<String> extensions() {
            List<String> l = new ArrayList<String>();
            l.add("kar");
            l.add("KAR");
            return l;
        }

        @Override
        public boolean isA(MimeType t) {
            if (APPLICATION_ZIP.name().equals(t.name())) {
                return true;
            }
            return super.isA(t);
        }

        @Override
        public MimeType superType() {
            return APPLICATION_ZIP;
        }
    };
}

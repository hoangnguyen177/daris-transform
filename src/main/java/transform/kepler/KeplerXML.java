package transform.kepler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import transform.ParameterDefinition;
import transform.mimetype.MimeTypes;
import transform.util.ZipUtil;
import arc.mime.MimeType;
import arc.xml.XmlDoc;

public class KeplerXML {

    public static final String DTD = "<?xml version=\"1.0\"?>\n"
            + "<!DOCTYPE entity PUBLIC \"-//UC Berkeley//DTD MoML 1//EN\"\n"
            + "    \"http://ptolemy.eecs.berkeley.edu/xml/dtd/MoML_1.dtd\">\n";

    public static final int BUFFER_SIZE = 8192;

    private XmlDoc.Element _de;
    private MimeType _srcMimeType;

    public KeplerXML(InputStream in) throws Throwable {

        if (!in.markSupported()) {
            in = new BufferedInputStream(in, BUFFER_SIZE);
        }
        _srcMimeType = MimeTypes.CONTENT_UNKNOWN;
        if (ZipUtil.isZipStream(in)) {
            ZipInputStream zis = new ZipInputStream(in);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(".xml") || entry.getName().endsWith(".XML")) {
                    int count;
                    byte buffer[] = new byte[BUFFER_SIZE];
                    BufferedOutputStream bos = new BufferedOutputStream(baos, BUFFER_SIZE);
                    while ((count = zis.read(buffer, 0, BUFFER_SIZE)) != -1) {
                        bos.write(buffer, 0, count);
                    }
                    bos.flush();
                    bos.close();
                    _srcMimeType = MimeTypes.APPLICATION_KEPLER_KAR;
                }

            }
            zis.close();
            in.close();
            in = new ByteArrayInputStream(baos.toByteArray());
        }
        try {
            XmlDoc.setValidateDTD(false);
            _de = new XmlDoc().parse(new InputStreamReader(in));
        } finally {
            in.close();
        }
        if (_de == null) {
            throw new Exception("Failed to parse Kepler transform definition.");
        }
        if (_srcMimeType.equals(MimeTypes.CONTENT_UNKNOWN)) {
            _srcMimeType = MimeTypes.TEXT_XML;
        }
    }

    public KeplerXML(File f) throws Throwable {
        this(new BufferedInputStream(new FileInputStream(f)));
    }

    public MimeType mimeType() {
        return MimeTypes.TEXT_XML;
    }

    public MimeType sourceMimeType() {
        return _srcMimeType;
    }

    public XmlDoc.Element root() {
        return _de;
    }

    public void write(OutputStream out) throws Throwable {
        Writer w = new BufferedWriter(new OutputStreamWriter(out), BUFFER_SIZE);
        try {
            w.write(DTD);
            w.write(_de.toString());
        } finally {
            w.flush();
            w.close();
        }
    }

    public static void main(String[] args) throws Throwable {

        File karFile = new File("/Users/slavisa/ParameterTest.xml");
        KeplerXML kxml = new KeplerXML(karFile);
        //System.out.println(kxml.mimeType());
        //System.out.println(kxml.sourceMimeType());
        //System.out.println(kxml.root());
        for (String name : KeplerTransformDefinition.parseParameterDefinitions(kxml.root(), false).keySet()) {
        	ParameterDefinition parDef = KeplerTransformDefinition.parseParameterDefinitions(kxml.root(), false).get(name);
        	System.out.println(parDef.minOccurs());
        }
    }
}

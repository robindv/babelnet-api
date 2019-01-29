package nl.celp;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import fi.iki.elonen.NanoHTTPD;
// NOTE: If you're using NanoHTTPD >= 3.0.0 the namespace is different,
//       instead of the above import use the following:
// import org.nanohttpd.NanoHTTPD;
import it.uniroma1.lcl.babelnet.BabelNet;
import it.uniroma1.lcl.babelnet.BabelSense;
import it.uniroma1.lcl.babelnet.BabelSynsetID;
import it.uniroma1.lcl.babelnet.BabelSynsetRelation;
import it.uniroma1.lcl.babelnet.WordNetSense;
import it.uniroma1.lcl.babelnet.WordNetSynsetID;
import it.uniroma1.lcl.babelnet.data.BabelGloss;
import it.uniroma1.lcl.babelnet.data.BabelImage;
import it.uniroma1.lcl.babelnet.data.BabelLemma;
import it.uniroma1.lcl.babelnet.data.BabelPointer;
import it.uniroma1.lcl.jlt.util.Language;
import it.uniroma1.lcl.babelnet.BabelSynset;


public class App extends NanoHTTPD {

    public App() throws IOException {
        super(8080);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("\nRunning! Point your browsers to http://localhost:8080/ \n");
    }

    static BabelNet bn;

    public static void main(String[] args) {

        bn = BabelNet.getInstance();
         
        try {
            new App();
        } catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
        }
    }

    int i = 0;

    public Response getOutgoingEdges(IHTTPSession session) {

        Map<String, String> parms = session.getParms();
        String synsetid = parms.get("id");
        BabelSynset synset = bn.getSynset(new BabelSynsetID(synsetid));

        JSONArray array = new JSONArray();
        for(BabelSynsetRelation edge : synset.getOutgoingEdges()) {
            JSONObject relation = new JSONObject();

            relation.put("language", edge.getLanguage());
            relation.put("target", edge.getTarget());
            relation.put("weight", edge.getWeight());
            relation.put("normalizedWeight", edge.getNormalizedWeight());

            BabelPointer ptr = edge.getPointer();
            JSONObject pointer = new  JSONObject();
            pointer.put("fSymbol", ptr.getSymbol());
            pointer.put("name", ptr.getName());
            pointer.put("shortName", ptr.getShortName());
            pointer.put("relationGroup", ptr.getRelationGroup());
            pointer.put("isAutomatic", ptr.isAutomatic());
            relation.put("pointer", pointer);
            array.put(relation);
        }

        return newFixedLengthResponse(Response.Status.OK, "application/json", array.toString());
    }

    public Response getSynset(IHTTPSession session)
    {
        Map<String, String> parms = session.getParms();
        String synsetid = parms.get("id");

        if(synsetid == null)
            return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", ":-(");

        BabelSynset synset;
        if(synsetid.startsWith("bn:"))
            synset = bn.getSynset(new BabelSynsetID(synsetid));
        else if(synsetid.startsWith("wn:"))
            synset = bn.getSynset(new WordNetSynsetID(synsetid));
        else
            return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", ":-(");

        JSONObject jobject = new JSONObject();

        JSONArray senses = new JSONArray();
        for(BabelSense sense : synset.getSenses(Language.EN))
        {
            JSONObject jsense = new JSONObject();
            JSONObject properties = new JSONObject();

            if(sense instanceof WordNetSense)
            {
                jsense.put("type","WordNetSense");
                properties.put("wordNetSenseNumber", sense.getSenseNumber());
                properties.put("wordNetOffset", sense.getWordNetOffset());
                properties.put("wordNetSynsetPosition", sense.getPosition());
            }
            else
                jsense.put("type","BabelSense");
            properties.put("fullLemma", sense.getFullLemma());
            properties.put("simpleLemma", sense.getSimpleLemma());
            properties.put("source", sense.getSource());
            properties.put("senseKey", sense.getSensekey());
            properties.put("frequency", sense.getFrequency());
            properties.put("language", sense.getLanguage());
            properties.put("pos", sense.getPOS());

            JSONObject jsynsetid = new JSONObject();
            jsynsetid.put("id", sense.getSynsetID().getID());
            jsynsetid.put("pos", sense.getSynsetID().getPOS());
            jsynsetid.put("source", sense.getSynsetID().getSource());
            properties.put("synsetID", jsynsetid);

            properties.put("translationInfo", "");
            properties.put("bKeySense", sense.isKeySense());
            properties.put("idSense", sense.getID());

            jsense.put("properties", properties);
            senses.put(jsense);
        }

        jobject.put("senses", senses);
        jobject.put("mainSense", synset.getMainSense().get());

        JSONArray jimages = new JSONArray();
        for(BabelImage img : synset.getImages()) {
            JSONObject jimage = new JSONObject();
            jimage.put("name", img.getName());
            jimage.put("languages", img.getLanguages());
            jimage.put("urlSource", img.getSource());
            jimage.put("licence", img.getLicense());
            jimage.put("thumbUrl", img.getThumbURL());
            jimage.put("url", img.getURL());
            jimage.put("badImage", img.isBadImage());

            jimages.put(jimage);
        }
        jobject.put("images", jimages);

        JSONArray jglosses = new JSONArray();
        for(BabelGloss gloss : synset.getGlosses(Language.EN)) {
            JSONObject jgloss = new JSONObject();
            jgloss.put("source", gloss.getSource());
            jgloss.put("gloss", gloss.getGloss());
            jgloss.put("sourceSense", gloss.getSourceSense());

            jglosses.put(jgloss);
        }
        jobject.put("glosses", jglosses);

        return newFixedLengthResponse(Response.Status.OK, "application/json", jobject.toString());
    }

    @Override
    public Response serve(IHTTPSession session) {
      
        String uri = session.getUri();

        try {
            if(uri.equals("/getOutgoingEdges"))
                return getOutgoingEdges(session);

            if(uri.equals("/getSynset"))
                return getSynset(session);
            }
        catch(Exception exception)
        {
            exception.printStackTrace();
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", exception.toString());
        }

        return newFixedLengthResponse("Geef een url op!");
    }
}
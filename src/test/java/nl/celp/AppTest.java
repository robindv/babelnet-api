package nl.celp;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import it.uniroma1.lcl.babelnet.BabelNet;
import it.uniroma1.lcl.babelnet.BabelSynset;
import it.uniroma1.lcl.babelnet.BabelSynsetID;


/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        BabelNet bn = BabelNet.getInstance();
        BabelSynset synset = bn.getSynset(new BabelSynsetID("bn:00049246n"));

        assertEquals("kit_fox", synset.getMainSense().get().getFullLemma());
        assertEquals("Small grey fox of the plains of western North America", synset.getMainGloss().get().getGloss());
    }
}

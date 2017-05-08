package SomeGame.GatewayService;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

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
        assertTrue( true );
    }
    
	public void testLogin() {
//		CredentialValues creds = new CredentialValues();
//		creds.setUsername("");
//		creds.setPassword("");
//		
//		Request r = new Request();
//		r.getParameters().set(ParameterCode.USER_REQUEST, "mn://account/login");
//		r.setData(Serialization.serialize(creds));
//		
//		clientRequest(context, "connID42x", r);
	}
}

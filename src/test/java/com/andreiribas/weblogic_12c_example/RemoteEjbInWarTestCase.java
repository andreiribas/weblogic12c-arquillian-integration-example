/**
 * 
 */
package com.andreiribas.weblogic_12c_example;

/* 
The MIT License (MIT)

Copyright (c) 2014 Andrei Gonçalves Ribas <andrei.g.ribas@gmail.com>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/


import java.util.Properties;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import junit.framework.TestCase;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author Andrei Gonçalves Ribas <andrei.g.ribas@gmail.com>
 * 
 */
@RunWith(Arquillian.class)
public class RemoteEjbInWarTestCase {
	
	@EJB
	private HelloWorldRemote helloWorldRemote;
	
	@Inject
	private HelloWorldRemote helloWorldRemoteUsingInjectAnnotation;
	
	@EJB(lookup = "ejb/HelloWorldRemote")
	private HelloWorldRemote helloWorldRemoteUsingLookup;
	
	@EJB(lookup = "HelloWorldRemote")
	private HelloWorldRemote helloWorldRemoteUsingLookupV2;
	
	@EJB(lookup = "java:comp/env/ejb/HelloWorldRemote")
	private HelloWorldRemote helloWorldRemoteUsingLookupV3;
	
	@EJB(name = "HelloWorldRemote")
	private HelloWorldRemote helloWorldRemoteUsingName;
	
	@EJB(mappedName = "HelloWorldRemote")
	private HelloWorldRemote helloWorldRemoteUsingMappedName;
	
	/**
	 * Deployment of the ear file for the test
	 * 
	 * @return
	 */
	@Deployment
	public static Archive<?> getTestArchive() {

		final WebArchive war = ShrinkWrap
				.create(WebArchive.class,
						"test_weblogic_arquillian_remote_ejb_in_war_test_case.war")
				.addClasses(HelloWorldRemoteBeanProducer.class, HelloWorldRemote.class, HelloWorldRemoteBean.class, RemoteEjbInWarTestCase.class)
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

		System.out.println(war.toString(true));
		
		final EnterpriseArchive ear = ShrinkWrap
				.create(EnterpriseArchive.class, "test_weblogic_arquillian_remote_ejb_in_war_test_case.ear")
				.addAsModule(war);

		System.out.println(ear.toString(true));
		
		//ear.as(ZipExporter.class).exportTo(new File("C:\\Users\\adm\\Desktop\\test_weblogic_arquillian_remote_ejb_in_war_test_case.ear"));  
		
		return ear;

	}
	
	@Test
	public void testRemoteEjbInvocationInsideContainer() throws Exception {

		Context context = null;
		
		try {
				 
			context = new InitialContext();
			
			Object obj = context.lookup("java:global.test_weblogic_arquillian_remote_ejb_in_war_test_case.test_weblogic_arquillian_remote_ejb_in_war_test_case.HelloWorldRemote!com.andreiribas.weblogic_12c_example.HelloWorldRemote");
			
			TestCase.assertNotNull(obj);
			
			System.out.println(obj.getClass().getName());
			
			HelloWorldRemote helloWorldRemote = (HelloWorldRemote) obj;
			
			TestCase.assertNotNull(helloWorldRemote);
	
			TestCase.assertEquals("Hello World Remote!", helloWorldRemote.say());
		
		} finally {
			
			if(context != null) {
				context.close();	
			}
			
		}

	}
	
	@Test
	public void testRemoteEjbInvocationOutsideContainer() throws NamingException {

		Context context = null;
		
		try {

			Properties jndiProperties = new Properties();
			jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY,
					"weblogic.jndi.WLInitialContextFactory");
			jndiProperties.put(Context.PROVIDER_URL, "t3://localhost:7001");

			context = new InitialContext(jndiProperties);

			Object obj = context
					.lookup("java:global.test_weblogic_arquillian_remote_ejb_in_war_test_case.test_weblogic_arquillian_remote_ejb_in_war_test_case.HelloWorldRemote");

			TestCase.assertNotNull(obj);

			HelloWorldRemote helloWorldRemote = (HelloWorldRemote) obj;

			TestCase.assertEquals("Hello World Remote!", helloWorldRemote.say());

			System.out.println("helloWorldRemote.say() is: "
					+ helloWorldRemote.say());

		} finally {
			
			if(context != null) {
				context.close();	
			}
			
		}

	}
	
	@Test
	public void testRemoteEjbWhenInjectingRemoteBeanInTestVariablesShouldOnlyInjectInTheOneMappedUsingName() throws NamingException {
		
		TestCase.assertNull(this.helloWorldRemoteUsingLookup);
		
		TestCase.assertNull(this.helloWorldRemoteUsingLookupV2);
		
		TestCase.assertNull(this.helloWorldRemoteUsingLookupV3);
		
		TestCase.assertNull(this.helloWorldRemoteUsingMappedName);
		
		testRemoteBeanValue(this.helloWorldRemoteUsingInjectAnnotation);
		
		testRemoteBeanValue(this.helloWorldRemote);
		
		testRemoteBeanValue(this.helloWorldRemoteUsingName);
		
	}
	
	public void testRemoteBeanValue(HelloWorldRemote helloWorldRemoteReference) {
		
		TestCase.assertNotNull(helloWorldRemoteReference);
		
		TestCase.assertEquals("Hello World Remote!", helloWorldRemoteReference.say());
		
	}
	
}
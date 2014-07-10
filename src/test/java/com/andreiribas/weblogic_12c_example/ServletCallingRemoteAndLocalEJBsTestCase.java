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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import junit.framework.TestCase;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author Andrei Gonçalves Ribas <andrei.g.ribas@gmail.com>
 * 
 */
@RunWith(Arquillian.class)
public class ServletCallingRemoteAndLocalEJBsTestCase {

	/**
	 * Deployment of the ear file for the test
	 * 
	 * @return
	 */
	@Deployment
	public static Archive<?> getTestArchive() {

		final WebArchive war = ShrinkWrap
				.create(WebArchive.class,
						"test_weblogic_arquillian_mixed_local_and_remote_ejbs_in_servlet_test_case.war")
				.addClasses(HelloWorldMixedEJBsServlet.class)
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

		System.out.println(war.toString(true));

		final JavaArchive ejb = ShrinkWrap
				.create(JavaArchive.class,
						"test_weblogic_arquillian_mixed_local_and_remote_ejbs_in_servlet_test_case.jar")
				.addClasses(HelloWorldLocal.class, HelloWorldLocalBean.class, 
						HelloWorldRemote.class, HelloWorldRemoteBean.class,
						HelloWorldRemoteBeanProducer.class,
						ServletCallingRemoteAndLocalEJBsTestCase.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");

		System.out.println(ejb.toString(true));

		final EnterpriseArchive ear = ShrinkWrap
				.create(EnterpriseArchive.class,
						"test_weblogic_arquillian_mixed_local_and_remote_ejbs_in_servlet_test_case.ear")
				.addAsModule(ejb).addAsModule(war);

		System.out.println(ear.toString(true));

		//ear.as(ZipExporter.class)
		//		.exportTo(
		//				new File(
		//						"C:\\Users\\adm\\Desktop\\test_weblogic_arquillian_mixed_local_and_remote_ejbs_in_servlet_test_case.ear"));

		return ear;

	}

	@Test
	public void shouldBeAbleToCallServlet()	throws Exception {
		
		URL servletUrl = new URL("http://localhost:7001/test_weblogic_arquillian_mixed_local_and_remote_ejbs_in_servlet_test_case/hello-mixed-ejbs");
		
		String servletResponse = readAllAndClose(servletUrl.openStream());
		
		TestCase.assertEquals("helloWorldLocal EJB call: Hello World Local!, helloWorldRemote EJB call: Hello World Remote!.", servletResponse);
		
	}

	public static String readAllAndClose(InputStream is) throws Exception {

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
			int read;
			while ((read = is.read()) != -1) {
				out.write(read);
			}
		} finally {
			try {
				is.close();
			} catch (Exception e) {
			}
		}
		return out.toString();
	}

}
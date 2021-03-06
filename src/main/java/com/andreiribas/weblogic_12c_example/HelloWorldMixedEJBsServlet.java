package com.andreiribas.weblogic_12c_example;

/* 
The MIT License (MIT)

Copyright (c) 2014 Andrei Gon�alves Ribas <andrei.g.ribas@gmail.com>

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

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author Andrei Gon�alves Ribas <andrei.g.ribas@gmail.com>
 *
 */
@WebServlet(value="/hello-mixed-ejbs", name="hello-mixed-ejbs-servlet")
public class HelloWorldMixedEJBsServlet extends HttpServlet {
	
   private static final long serialVersionUID = 8249673615048070666L;

   @Inject
   private HelloWorldLocal helloWorldLocal;
   
   @Inject
   private HelloWorldRemote helloWorldRemote;

   @Override
   protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      resp.getWriter().append(String.format("helloWorldLocal EJB call: %s, helloWorldRemote EJB call: %s.", this.helloWorldLocal.say(), this.helloWorldRemote.say()));
   }
   
}
/*
 * Copyright 2005-2016 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package java.org.pegadaian.dev;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Service;

@Service("greetService")
public class GreetServiceImpl implements GreetService {

    @Override
    public Greeting greetUser(Exchange exchange) {
    	Greeting greeting = new Greeting();
    	greeting.setRegard("Hello " + exchange.getIn().getHeader("name"));
    	greeting.setDate(java.time.LocalDateTime.now().toString());
        return greeting;
    }

}

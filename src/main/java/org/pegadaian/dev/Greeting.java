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
package org.pegadaian.dev;

import org.apache.commons.lang3.builder.ToStringBuilder;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * User entity
 *
 */
@JsonAutoDetect
@ApiModel(description = "Represents an user of the system")
public class Greeting {

    @ApiModelProperty(value = "The name of the user", required = true)
    private String regard;
    @ApiModelProperty(value = "The current time")
    private String date;
    
    public Greeting() {
    }

    @JsonCreator
    public Greeting(@JsonProperty("regard") String regard,
    		@JsonProperty("date") String date) {
        this.regard = regard;
        this.date = date;
    }
    public String getRegard() {
        return regard;
    }

    public void setRegard(String regard) {
        this.regard = regard;
    }
    
    public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}

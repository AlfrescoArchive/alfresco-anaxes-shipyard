/*
 * Copyright 2017 Alfresco Software, Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.alfresco.deployment.sample;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


@Configuration
public class CorsConfiguration
{
    private static final Log logger = LogFactory.getLog(CorsConfiguration.class);
    private static final String ALLOW_ALL = "*";

    @Value("#{'${cors.allowedOrigins}'.split(',')}")
    private List<String> allowedOrigins = new ArrayList<String>();

    public CorsConfiguration()
    {
        //NOOP
    }


    @Bean
    public WebMvcConfigurer corsConfigurer()
    {
        return new WebMvcConfigurerAdapter()
        {
            @Override
            public void addCorsMappings(CorsRegistry registry)
            {
                //Build a String Array so that it can be passed to the allowedOrigins method
                String[] originsArray = buildAllowedOriginsArray(allowedOrigins);

                //Do we have any origins to work with?
                if (originsArray.length > 0)
                {
                    logger.debug("Allowed Origins: " + Arrays.toString(originsArray));
                }
                else
                {
                    logger.info("No origins provided. Please configure at least one origin");
                }

                //GET, HEAD, and POST are allowed by default. Any other methods must be declared
                //Mapping should be the exact path or an Ant-style path patterns
                //All headers and credentials are allowed
                //MaxAge is using the default of 30 minutes.
                registry.addMapping("/hello").allowedOrigins(originsArray);
                registry.addMapping("/hello/*").allowedOrigins(originsArray)
                        .allowedMethods("GET", "PUT", "DELETE");
            }
        };
    }


    /**
     * Build a string array of the allowed origins.  Remove blank entries. Remove invalid URLs
     *
     * @param allowedOrigins List of possible allowed origins use by CORS
     * @return String array of valid allowed origins
     */
    public String[] buildAllowedOriginsArray(List<String> allowedOrigins)
    {
        if (allowedOrigins != null && !allowedOrigins.isEmpty())
        {
            //validate origin format.
            String[] schemes = {"http", "https"};
            UrlValidator urlValidator = new UrlValidator(schemes, UrlValidator.ALLOW_LOCAL_URLS);

            for(Iterator<String> iter = allowedOrigins.listIterator(); iter.hasNext();)
            {
                String origin = iter.next();

                if (StringUtils.isBlank(origin))
                {
                    logger.info("Removing empty origin from list.");
                    iter.remove();
                }
                else if (origin.equals(ALLOW_ALL))
                {
                    logger.info("All origins will be allowed.");
                    return new String[]{"*"};
                }
                else if (!urlValidator.isValid(origin))
                {
                    logger.info("Invalid origin: " + origin + ". Removing from list of allowed origins.");
                    iter.remove();
                }
            }

            //Convert the list to a String array
            String[] array = new String[allowedOrigins.size()];
            array = allowedOrigins.toArray(array);

            return array;
        }
        else
        {
            return new String[0];
        }
    }
}

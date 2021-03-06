/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import geb.site.GebishClientErrorHandler
import geb.site.Manuals
import geb.site.Model
import ratpack.error.ClientErrorHandler
import ratpack.groovy.template.TextTemplateModule
import ratpack.handling.Context

import static ratpack.groovy.Groovy.groovyTemplate
import static ratpack.groovy.Groovy.ratpack

ratpack {
    serverConfig {
        props(getClass().getResource("/ratpack.properties"))
        require("/manuals", Manuals)
    }
    bindings {
        module(TextTemplateModule) {
            it.staticallyCompile = true
        }
        bind(ClientErrorHandler, GebishClientErrorHandler)
        add Date, new Date()
    }
    handlers {
        files {
            dir("public")
            indexFiles("index.html")
        }

        get(':page?') { Context context, Date startupTime, Manuals manuals ->
            lastModified(startupTime) {
                def pageToken = pathTokens.page ?: 'index'

                String path = context.request.path
                if (path.endsWith('/')) {
                    String redirectTo = "/${path.replaceFirst(/\/$/, "")}"
                    context.redirect redirectTo
                } else {
                    if (pageToken in (Model.HIGHLIGHT_PAGES.keySet() + ['index', 'lists'])) {
                        render groovyTemplate(Model.get(manuals, pageToken), 'main.html')
                    } else {
                        context.notFound()
                    }
                }
            }
        }
    }
}


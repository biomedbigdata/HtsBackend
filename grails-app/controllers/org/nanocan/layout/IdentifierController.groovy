package org.nanocan.layout

import grails.plugins.springsecurity.Secured

@Secured(['ROLE_USER'])
class IdentifierController {

    def scaffold = true
}

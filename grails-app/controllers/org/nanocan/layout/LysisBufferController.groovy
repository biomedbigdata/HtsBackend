package org.nanocan.layout

import grails.plugins.springsecurity.Secured

@Secured(['ROLE_USER'])
class LysisBufferController {

    def scaffold = true
}
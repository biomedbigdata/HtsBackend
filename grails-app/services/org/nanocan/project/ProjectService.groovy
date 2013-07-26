package org.nanocan.project

import org.nanocan.layout.SlideLayout

class ProjectService {

    def findProject(doInstance) {
        return Project.where {
            if(doInstance instanceof SlideLayout) {
                layouts
                {
                    id == doInstance.id
                }
            }
            /*else if(doInstance instanceof Slide)
            {
                slides
                {
                    id == doInstance.id
                }
            }   */
        }.list()

    }

    def addToProject(doInstance, projectsSelected) {
        projectsSelected.each {
            def project
            if(it instanceof Project) project = it
            else project = Project.get(it as Long)

            if(doInstance instanceof SlideLayout) project.addToLayouts(doInstance).save(flush: true)
            //else if(doInstance instanceof Slide) project.addToSlides(doInstance).save(flush: true)
        }
    }

    def removeFromProject(doInstance, projectsToRemoveFrom)
    {
        projectsToRemoveFrom.each {
            def project
            if(it instanceof Project) project = it
            else project = Project.get(it as Long)

            if(doInstance instanceof SlideLayout) project.removeFromLayouts(doInstance).save(flush: true)
            //else if(doInstance instanceof Slide) project.removeFromSlides(doInstance).save(flush: true)
        }
    }

    def updateProjects(doInstance, projectsSelected) {

        def projects = findProject(doInstance)

        if(projectsSelected == null && projects == null) return

        else if(projectsSelected == null && projects.size() > 0)
        {
            removeFromProject(doInstance, projects)
        }

        else if(projectsSelected != null && projects.size() == 0)
        {
            addToProject(doInstance, projectsSelected)
        }

        else{
            projectsSelected = projectsSelected.collect{Project.get(it as Long)}

            def projectsToRemoveFrom = projects
            projectsToRemoveFrom.removeAll(projectsSelected)

            def projectsToAddTo = projectsSelected
            projectsToAddTo.removeAll(projects)

            if(projectsToAddTo.size() > 0) addToProject(doInstance, projectsToAddTo)
            if(projectsToRemoveFrom.size() >0) removeFromProject(doInstance, projectsToRemoveFrom)
        }
        return
    }
}
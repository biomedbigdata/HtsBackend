/*
 * Copyright (C) 2014
 * Center for Excellence in Nanomedicine (NanoCAN)
 * Molecular Oncology
 * University of Southern Denmark
 * ###############################################
 * Written by:	Markus List
 * Contact: 	mlist'at'health'.'sdu'.'dk
 * Web:			http://www.nanocan.org/miracle/
 * ###########################################################################
 *	
 *	This file is part of MIRACLE.
 *
 *  MIRACLE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *   along with this program. It can be found at the root of the project page.
 *	If not, see <http://www.gnu.org/licenses/>.
 *
 * ############################################################################
 */
package org.nanocan.project

import org.nanocan.security.Person
import org.nanocan.layout.SlideLayout
import org.nanocan.layout.PlateLayout

class Experiment implements Serializable{

    String title
    String description

    Date dateCreated
    Date lastUpdated
    Date firstDayOfTheExperiment

    Person createdBy
    Person lastUpdatedBy

    static searchable = true

    static hasMany = [slideLayouts: SlideLayout, plateLayouts: PlateLayout]
    static belongsTo = [project: Project]

    static constraints = {

        title unique:true
        firstDayOfTheExperiment nullable: true
    }

    String toString()
    {
        /*if(firstDayOfTheExperiment)
            return ("${firstDayOfTheExperiment.toString()} - ${title}")
        else */return title
    }
}

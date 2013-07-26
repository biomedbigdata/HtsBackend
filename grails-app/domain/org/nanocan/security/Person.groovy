package org.nanocan.security

class Person {

	transient springSecurityService
    transient hasBeforeUpdate = false
    transient hasBeforeInsert = false

	String username
	String password
	boolean enabled
	boolean accountExpired
	boolean accountLocked
	boolean passwordExpired

    static searchable = [only: ['username']]

	static constraints = {
		username blank: false, unique: true
		password blank: false
	}

	static mapping = {
		password column: '`password`'
	}

	Set<Role> getAuthorities() {
		PersonRole.findAllByPerson(this).collect { it.role } as Set
	}

    def beforeInsert() {
        if (!hasBeforeInsert)
        {
            hasBeforeInsert = true
            encodePassword ()
        }
    }

    def beforeUpdate() {
        if (isDirty('password') && !hasBeforeUpdate) {
            hasBeforeUpdate = true
            encodePassword()
        }
    }

	protected void encodePassword() {
		password = springSecurityService.encodePassword(password)
	}

    String toString()
    {
        username
    }
}
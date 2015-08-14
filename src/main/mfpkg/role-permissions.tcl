
# roles: transform.user transform.developer transform.admin
authorization.role.create :ifexists ignore :role transform.admin :description "Transform: admin role."
authorization.role.create :ifexists ignore :role transform.developer  :description "Transform: developer role."
authorization.role.create :ifexists ignore :role transform.user  :description "Transform: user role."

# permissions for role: transform.user
actor.grant :type role :name transform.user :perm < :resource -type document transform:transform-definition :access ACCESS >
actor.grant :type role :name transform.user :perm < :resource -type document transform:transform :access ACCESS >
actor.grant :type role :name transform.user :perm < :resource -type document transform:transform-definition :access PUBLISH >
actor.grant :type role :name transform.user :perm < :resource -type document transform:transform :access PUBLISH >
actor.grant :type role :name transform.user :perm < :resource -type service transform.* :access ACCESS >
actor.grant :type role :name transform.user :perm < :resource -type service transform.* :access MODIFY >
#if { [xvalue exists [authorization.role.namespace.exists :namespace daris]] == "true" } {
#    actor.grant :type role :name transform.user :perm < :resource -type role:namespace daris: :access ADMINISTER >
#}

# permissions for role: transform.developer
actor.grant :type role :name transform.user :perm < :resource -type document transform:transform-definition :access PUBLISH >
actor.grant :type role :name transform.user :perm < :resource -type document transform:transform :access PUBLISH >
actor.grant :type role :name transform.developer :perm < :resource -type service transform.* :access ACCESS >
actor.grant :type role :name transform.developer :perm < :resource -type service transform.* :access MODIFY >

# permissions for role: transform.admin
actor.grant :type role :name transform.admin :perm < :resource -type service transform.* :access * >
actor.grant :type role :name transform.admin :role -type role transform.user
actor.grant :type role :name transform.admin :role -type role transform.developer


actor.grant :type plugin:service :role -type role service-user :name transform.create
actor.grant :type plugin:service :role -type role service-user :name transform.definition.create
actor.grant :type plugin:service :role -type role service-user :name transform.definition.describe
actor.grant :type plugin:service :role -type role service-user :name transform.definition.destroy
actor.grant :type plugin:service :role -type role service-user :name transform.definition.list
actor.grant :type plugin:service :role -type role service-user :name transform.definition.update
actor.grant :type plugin:service :role -type role service-user :name transform.describe
actor.grant :type plugin:service :role -type role service-user :name transform.destroy
actor.grant :type plugin:service :role -type role service-user :name transform.execute
actor.grant :type plugin:service :role -type role service-user :name transform.list
actor.grant :type plugin:service :role -type role service-user :name transform.log
actor.grant :type plugin:service :role -type role service-user :name transform.log.get
actor.grant :type plugin:service :role -type role service-user :name transform.output.add
actor.grant :type plugin:service :role -type role service-user :name transform.output.list
actor.grant :type plugin:service :role -type role service-user :name transform.output.remove
actor.grant :type plugin:service :role -type role service-user :name transform.progress-detail.get
actor.grant :type plugin:service :role -type role service-user :name transform.progress-detail.set
actor.grant :type plugin:service :role -type role service-user :name transform.progress.get
actor.grant :type plugin:service :role -type role service-user :name transform.progress.set
actor.grant :type plugin:service :role -type role service-user :name transform.provider.user.settings.definition.get
actor.grant :type plugin:service :role -type role service-user :name transform.provider.user.settings.get
actor.grant :type plugin:service :role -type role service-user :name transform.provider.user.settings.set
actor.grant :type plugin:service :role -type role service-user :name transform.reset
actor.grant :type plugin:service :role -type role service-user :name transform.resume
actor.grant :type plugin:service :role -type role service-user :name transform.runtime.property.get
actor.grant :type plugin:service :role -type role service-user :name transform.runtime.property.remove
actor.grant :type plugin:service :role -type role service-user :name transform.runtime.property.set
actor.grant :type plugin:service :role -type role service-user :name transform.status.get
actor.grant :type plugin:service :role -type role service-user :name transform.status.set
actor.grant :type plugin:service :role -type role service-user :name transform.suspend
actor.grant :type plugin:service :role -type role service-user :name transform.terminate
actor.grant :type plugin:service :role -type role service-user :name transform.type.list
actor.grant :type plugin:service :role -type role service-user :name transform.update

#if { [xvalue exists [authorization.role.namespace.exists :namespace daris]] == "true" } {
#    actor.grant :type plugin:service :name transform.create :perm < :resource -type role:namespace daris: :access ADMINISTER >
#    actor.grant :type plugin:service :name transform.execute :perm < :resource -type role:namespace daris: :access ADMINISTER >
#}

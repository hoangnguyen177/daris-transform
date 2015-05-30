# remove old version package
if { [xvalue exists [package.exists :package Transform]] == "true" } {
    if { [xvalue package/vendorurl [package.describe :package Transform]] == "http://www.neuroimaging.org.au" } {
	package.uninstall :package Transform
    }
}


set plugin_label      [string toupper PACKAGE_$package]
set plugin_namespace  mflux/plugins
set plugin_zip        transform-plugin.zip
set plugin_jar        transform-plugin.jar
set module_class      transform.TransformPluginModule

# prune the asset
#if { [xvalue exists [asset.exists :id path=${plugin_namespace}/${plugin_jar}]] == "true" } {
#	asset.prune :id path=${plugin_namespace}/${plugin_jar}
#}

# extract transform-plugin.jar to /mflux/plugins
asset.import :url archive:${plugin_zip} \
		:namespace -create yes ${plugin_namespace} \
		:label -create yes ${plugin_label} :label PUBLISHED \
        :update true

# install the plugin module
if { [xvalue exists [plugin.module.exists :path ${plugin_namespace}/${plugin_jar} :class ${module_class}]] == "false" } {
		plugin.module.add :path ${plugin_namespace}/${plugin_jar} :class ${module_class}
}

# reload the services     
system.service.reload

# refresh the enclosing shell
srefresh

# set the kepler home
set app transform
if { [info exists kepler.home] } {
    application.property.create :ifexists ignore :property -app ${app} -name kepler.home
    application.property.set :property -app ${app} -name kepler.home ${kepler.home}
} else {
    if { [xvalue exists [application.property.exists :property -app ${app} kepler.home ]] != "true" } {
        puts "Warning: kepler.home is not set. You can run 'transform.kepler.home.set :path /path/to/kepler' command after installation. Or re-install the transform package with following command: 'package.install :in file:/path/to/mfpkg-transform-xxx.zip :arg -name kepler.home /path/to/kepler'"
    }
}

# Create/Update Doc Types
source doc-types.tcl

# Create/Update Relationship Types
source relationships.tcl

# Register mime types
source mime-types.tcl

# Grant service permissions
source service-permissions.tcl

# Grant role permissions
source role-permissions.tcl


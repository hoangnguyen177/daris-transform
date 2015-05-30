set plugin_label      [string toupper PACKAGE_$package]
set plugin_namespace  mflux/plugins
set plugin_zip        daris-transform-plugin.zip
set plugin_jar        daris-transform-plugin.jar
set module_class      transform.TransformPluginModule

if { [xvalue exists [plugin.module.exists :path ${plugin_namespace}/${plugin_jar} :class ${module_class}]] == "true" } {
	plugin.module.remove :path ${plugin_namespace}/${plugin_jar} :class ${module_class}
}

if { [xvalue exists [asset.exists :id path=${plugin_namespace}/${plugin_jar}]] == "true" } {
   	asset.hard.destroy :id path=${plugin_namespace}/${plugin_jar}
}

system.service.reload

srefresh

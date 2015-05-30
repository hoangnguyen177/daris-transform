# remove the predeccessor (old release): Transform
if { [xvalue exists [package.exists :package Transform]] == "true" } {
    if { [xvalue package/vendorurl [package.describe :package Transform]] == "http://www.neuroimaging.org.au" } {
        package.uninstall :package Transform
    }
}
if { [xvalue exists [asset.exists :id path=/mflux/plugins/transform-plugin.jar]] == "true" } {
    asset.hard.destroy :id path=/mflux/plugins/transform-plugin.jar
}


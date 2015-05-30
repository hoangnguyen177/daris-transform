if { [xvalue exists [asset.relationship.type.exists :type transform-output-of]] == "false" } {
    asset.relationship.type.create :type -maximum 1 transform-output-of
}
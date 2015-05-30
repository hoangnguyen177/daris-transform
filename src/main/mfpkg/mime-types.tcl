if { [xvalue exists [type.exists :type application/kepler-kar]] != "true" } {
    type.create :type application/kepler-kar :extension kar :compressable no :description "Kepler workflow file in zip format."
}
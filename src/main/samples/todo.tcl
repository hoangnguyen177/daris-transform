proc setMethod { project method } {
    foreach subject [xvalues cid [asset.query :where cid in '$project']] {
        om.pssd.subject.method.replace :id $subject :method $method :recursive true
    }
}

setMethod 1.5.95 1.4.10


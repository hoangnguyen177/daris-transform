# secure.wallet.set :key host-credentials:ssh://<your host> :xvalue  < :user <your username> :password <your-password> >
# setup kepler server in user settings
transform.provider.user.self.settings.set :type kepler \
                                     :settings < \
                                         :kepler.server < \
                                             :host edward.hpc.unimelb.edu.au \
                                             :launcher-service -name secure.shell.execute < \
                                                 :args < \
                                                     :use-wallet-entry true \
                                                     :host edward.hpc.unimelb.edu.au \
                                                     :command "/data/project2/pMelb0036/GWAS/Kepler/applications/kepler-2.4/keplernk.sh --single" \
                                                 > \
                                                 :port-xpath stdout \
                                             > \
                                         > \
                                     >

# 
#set duid1 [xvalue uid [transform.definition.create :name name1 :description description1 :in file:/Users/wilson/git/Transform/scripts/Sample1.kar :type kepler]]
#set duid2 [xvalue uid [transform.definition.create :name name2 :description description2 :in file:/Users/wilson/git/Transform/scripts/Sample2.kar :type kepler]]

#user.settings.set :domain nig :user jvilsten :app transform :settings < :transform.provider.kepler < :kepler.server < :host edward.hpc.unimelb.edu.au :launcher-service -name secure.shell.execute :args < :host edward.hpc.unimelb.edu.au :user < :name jvilsten :password change_me :command  "/data/project2/pMelb0036/GWAS/Kepler/applications/kepler-2.4/keplernk.sh --single" > > :port-xpath stdout > > > 

package transform;

import java.util.Collection;
import java.util.Vector;

import transform.event.TransformEvent;
import transform.event.TransformEventFilterFactory;
import transform.exec.ExecTransformProvider;
import transform.kepler.KeplerTransformProvider;
import transform.services.SvcTransformCreate;
import transform.services.SvcTransformDefinitionCreate;
import transform.services.SvcTransformDefinitionDescribe;
import transform.services.SvcTransformDefinitionDestroy;
import transform.services.SvcTransformDefinitionList;
import transform.services.SvcTransformDefinitionUpdate;
import transform.services.SvcTransformDescribe;
import transform.services.SvcTransformDestroy;
import transform.services.SvcTransformDestroyAll;
import transform.services.SvcTransformExecute;
import transform.services.SvcTransformList;
import transform.services.SvcTransformLog;
import transform.services.SvcTransformLogGet;
import transform.services.SvcTransformOutputAdd;
import transform.services.SvcTransformOutputList;
import transform.services.SvcTransformOutputRemove;
import transform.services.SvcTransformProgressDetailGet;
import transform.services.SvcTransformProgressDetailSet;
import transform.services.SvcTransformProgressGet;
import transform.services.SvcTransformProgressSet;
import transform.services.SvcTransformProviderUserSelfSettingsGet;
import transform.services.SvcTransformProviderUserSelfSettingsSet;
import transform.services.SvcTransformProviderUserSettingsDefinitionGet;
import transform.services.SvcTransformProviderUserSettingsGet;
import transform.services.SvcTransformProviderUserSettingsSet;
import transform.services.SvcTransformReset;
import transform.services.SvcTransformResume;
import transform.services.SvcTransformRuntimePropertyGet;
import transform.services.SvcTransformRuntimePropertyRemove;
import transform.services.SvcTransformRuntimePropertySet;
import transform.services.SvcTransformStatusGet;
import transform.services.SvcTransformStatusSet;
import transform.services.SvcTransformSuspend;
import transform.services.SvcTransformTerminate;
import transform.services.SvcTransformTypeList;
import transform.services.SvcTransformUpdate;
import arc.mf.plugin.ConfigurationResolver;
import arc.mf.plugin.PluginModule;
import arc.mf.plugin.PluginService;
import arc.mf.plugin.event.FilterRegistry;

public class TransformPluginModule implements PluginModule {

    public static final String CONF_KEPLER_HOME = "kepler.home";

    private Collection<PluginService> _services;

    @Override
    public String description() {
        return "The transform framework.";
    }

    @Override
    public void initialize(ConfigurationResolver config) throws Throwable {

        /*
         * Register transform providers
         */
        TransformProviderRegistry.register(KeplerTransformProvider.TYPE, KeplerTransformProvider.class);
        TransformProviderRegistry.register(ExecTransformProvider.TYPE, ExecTransformProvider.class);

        /*
         * Register services
         */
        _services = new Vector<PluginService>();
        _services.add(new SvcTransformCreate());
        _services.add(new SvcTransformDefinitionCreate());
        _services.add(new SvcTransformDefinitionDescribe());
        _services.add(new SvcTransformDefinitionDestroy());
        _services.add(new SvcTransformDefinitionList());
        _services.add(new SvcTransformDefinitionUpdate());
        _services.add(new SvcTransformDescribe());
        _services.add(new SvcTransformDestroy());
        _services.add(new SvcTransformDestroyAll());
        _services.add(new SvcTransformExecute());
        _services.add(new SvcTransformProgressDetailGet());
        _services.add(new SvcTransformProgressDetailSet());
        _services.add(new SvcTransformList());
        _services.add(new SvcTransformLog());
        _services.add(new SvcTransformLogGet());
        _services.add(new SvcTransformOutputList());
        _services.add(new SvcTransformOutputAdd());
        _services.add(new SvcTransformOutputRemove());
        _services.add(new SvcTransformProgressGet());
        _services.add(new SvcTransformProgressSet());
        _services.add(new SvcTransformProviderUserSettingsDefinitionGet());
        _services.add(new SvcTransformProviderUserSelfSettingsGet());
        _services.add(new SvcTransformProviderUserSelfSettingsSet());
        _services.add(new SvcTransformProviderUserSettingsGet());
        _services.add(new SvcTransformProviderUserSettingsSet());
        _services.add(new SvcTransformReset());
        _services.add(new SvcTransformResume());
        _services.add(new SvcTransformRuntimePropertyGet());
        _services.add(new SvcTransformRuntimePropertyRemove());
        _services.add(new SvcTransformRuntimePropertySet());
        _services.add(new SvcTransformStatusGet());
        _services.add(new SvcTransformStatusSet());
        _services.add(new SvcTransformSuspend());
        _services.add(new SvcTransformTerminate());
        _services.add(new SvcTransformTypeList());
        _services.add(new SvcTransformUpdate());

        // register system events
        registerSystemEvents();
    }

    @Override
    public Collection<PluginService> services() {
        return _services;
    }

    @Override
    public void shutdown(ConfigurationResolver config) throws Throwable {
        // Unregister system events
        unregisterSystemEvents();
    }

    @Override
    public String vendor() {
        return "ARC Linkage Project 2012";
    }

    @Override
    public String version() {
        return "1.0";
    }

    protected void registerSystemEvents() throws Throwable {

        FilterRegistry.remove(TransformEvent.EVENT_TYPE);
        FilterRegistry.add(TransformEvent.EVENT_TYPE, TransformEventFilterFactory.INSTANCE);
    }

    protected void unregisterSystemEvents() throws Throwable {

        FilterRegistry.remove(TransformEvent.EVENT_TYPE);
    }

}

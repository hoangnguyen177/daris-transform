package transform;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import arc.mf.plugin.ServiceExecutor;

/**
 * The transform provider registry (singleton class).
 * 
 * @author Wei Liu (wliu1976@gmail.com)
 * 
 */
public class TransformProviderRegistry {

    public static Map<String, Class<?>> _providerClasses;

    /**
     * The transform types, which are also the keys for identifying the
     * transform providers, have registered.
     * 
     * @return
     */
    public static Set<String> providerTypes() {
        if (_providerClasses != null) {
            return _providerClasses.keySet();
        } else {
            return null;
        }
    }

    /**
     * Register the given transform provider.
     * 
     * @param transformProvider
     */
    public static <T extends TransformProvider> void register(String type, Class<T> providerClass) {
        if (_providerClasses == null) {
            _providerClasses = new HashMap<String, Class<?>>();
        }
        _providerClasses.put(type, providerClass);
    }

    /**
     * Unregister the given transform provider.
     */
    public static void unregister(String type) {
        if (_providerClasses == null) {
            return;
        }
        _providerClasses.remove(type);
    }

    /**
     * Gets the transform provider class for the given transform type(key).
     * 
     * @param type
     *            the transform type.
     * @return
     */
    public static Class<?> getTransformProviderClass(String type) {
        if (_providerClasses == null) {
            return null;
        } else {
            return _providerClasses.get(type);
        }
    }

    /**
     * Gets the transform provider for given transform type(key).
     * 
     * @param type
     *            the transform type.
     * @return null if the provider for the type is not registered.
     */
    public static TransformProvider getTransformProviderInstance(String type, ServiceExecutor executor)
            throws Throwable {
        if (_providerClasses == null) {
            throw new Exception("No transform provider for type " + type + ".");
        } else {
            Class<?> providerClass = _providerClasses.get(type);
            if (providerClass == null) {
                throw new Exception("No transform provider for type " + type + ".");
            }
            return (TransformProvider) providerClass.getConstructor(ServiceExecutor.class).newInstance(executor);
        }
    }
}

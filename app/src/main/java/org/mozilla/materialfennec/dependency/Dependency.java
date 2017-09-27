package org.mozilla.materialfennec.dependency;

import android.content.Context;

//TODO: try to remove imports
import org.mozilla.materialfennec.ViewController;
import org.mozilla.materialfennec.search.SearchSuggestionPresenter;
import org.mozilla.materialfennec.search.SearchSuggestionPresenterImpl;
import org.mozilla.materialfennec.search.SuggestionIdlingResource;

import java.util.HashMap;

/**
 * Created by nineg on 2017/9/17.
 */

public class Dependency {
    private final HashMap<Object, DependencyProvider> mProviders = new HashMap<>();
    //TODO: handle object life cycle, such as 'destroy'
    private final HashMap<Object, Object> mDependencies = new HashMap<>();
    //The instance
    private static Dependency sDependency;

    public interface DependencyProvider<T> {
        T createDependency();
    }

    public void init(final Context context) {
        sDependency = this;
        mProviders.put(SearchSuggestionPresenter.class, new DependencyProvider() {
                    @Override
                    public SearchSuggestionPresenter createDependency() {
                        return new SearchSuggestionPresenterImpl(context);
                    }
                }
        );

        mProviders.put(ViewController.class, new DependencyProvider() {

            @Override
            public ViewController createDependency() {
                return new ViewController();
            }
        });
    }

    public static void terminate() {
        sDependency = null;
    }

    private <T> T getDependency(Class<T> cls) {
        return getDependencyInner(cls);
    }

    private synchronized <T> T getDependencyInner(Object key) {
        @SuppressWarnings("unchecked")
        T obj = (T) mDependencies.get(key);
        if (obj == null) {
            obj = createDependency(key);
            mDependencies.put(key, obj);
        }
        return obj;
    }

    private <T> T createDependency(Object cls) {
        DependencyProvider<T> provider = mProviders.get(cls);
        if (provider == null) {
            return null;
        }
        return provider.createDependency();
    }


    public static void provideDependency(Class cls, DependencyProvider provider) {
        sDependency.mProviders.put(cls, provider);
    }

    public static <T> T get(Class<T> cls) {
        return sDependency.getDependency(cls);
    }
}

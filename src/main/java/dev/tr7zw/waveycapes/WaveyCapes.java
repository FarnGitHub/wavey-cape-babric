package dev.tr7zw.waveycapes;

import com.google.common.collect.ImmutableMap;
import dev.tr7zw.waveycapes.config.Config;
import dev.tr7zw.waveycapes.config.enums.CapeStyle;
import dev.tr7zw.waveycapes.config.enums.WindMode;
import net.glasslauncher.mods.gcapi3.api.ConfigEntry;
import net.glasslauncher.mods.gcapi3.api.ConfigFactoryProvider;
import net.glasslauncher.mods.gcapi3.api.ConfigRoot;
import net.glasslauncher.mods.gcapi3.impl.SeptFunction;
import net.glasslauncher.mods.gcapi3.impl.object.ConfigEntryHandler;
import net.glasslauncher.mods.gcapi3.impl.object.entry.EnumConfigEntryHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.function.Function;

public class WaveyCapes implements ConfigFactoryProvider {

    @ConfigRoot(value = "waveycapes", visibleName = "Wavey Capes Configuration")
    public static Config config = new Config();

    @Override
    public void provideLoadFactories(ImmutableMap.Builder<Type, SeptFunction<String, ConfigEntry, Field, Object, Boolean, Object, Object, ConfigEntryHandler<?>>> immutableBuilder) {
        immutableBuilder.put(WindMode.class, factoryOf(WindMode.class));
        immutableBuilder.put(CapeStyle.class, factoryOf(CapeStyle.class));
    }

    @Override
    public void provideSaveFactories(ImmutableMap.Builder<Type, Function<Object, Object>> immutableBuilder) {
        immutableBuilder.put(WindMode.class, enumEntry -> enumEntry);
        immutableBuilder.put(CapeStyle.class, enumEntry -> enumEntry);
    }

    public <T extends Enum<T>> SeptFunction<String, ConfigEntry, Field, Object, Boolean, Object, Object, ConfigEntryHandler<?>> factoryOf(Class<T> clazz) {
        return ((id, configEntry, parentField, parentObject, isMultiplayerSynced, enumOrOrdinal, defaultEnum) ->
        {
            int enumOrdinal = 0;
            if (enumOrOrdinal instanceof Integer ordinal) {
                enumOrdinal = ordinal;
            } else if(enumOrOrdinal instanceof Enum<?> enumer){
                enumOrdinal = enumer.ordinal();
            }
            return new EnumConfigEntryHandler<>(id, configEntry, parentField, parentObject, isMultiplayerSynced, enumOrdinal, clazz.cast(defaultEnum).ordinal(), clazz);
        });
    }
    
}

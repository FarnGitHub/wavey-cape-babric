package dev.tr7zw.waveycapes;

import com.google.common.collect.ImmutableMap;
import dev.tr7zw.waveycapes.config.Config;
import dev.tr7zw.waveycapes.config.enums.CapeMovement;
import dev.tr7zw.waveycapes.renderer.CapeRendererImpl;
import net.glasslauncher.mods.gcapi3.api.ConfigEntry;
import net.glasslauncher.mods.gcapi3.api.ConfigFactoryProvider;
import net.glasslauncher.mods.gcapi3.api.ConfigRoot;
import net.glasslauncher.mods.gcapi3.api.PreConfigSavedListener;
import net.glasslauncher.mods.gcapi3.impl.GlassYamlFile;
import net.glasslauncher.mods.gcapi3.impl.SeptFunction;
import net.glasslauncher.mods.gcapi3.impl.object.ConfigEntryHandler;
import net.glasslauncher.mods.gcapi3.impl.object.entry.EnumConfigEntryHandler;
import net.minecraft.client.resource.language.TranslationStorage;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.function.Function;

public class WaveyCapes implements ConfigFactoryProvider, PreConfigSavedListener {

    @ConfigRoot(value = "waveycapes", visibleName = "WaveyCapes Settings")
    public static Config config = new Config();

    @Override
    public void provideLoadFactories(ImmutableMap.Builder<Type, SeptFunction<String, ConfigEntry, Field, Object, Boolean, Object, Object, ConfigEntryHandler<?>>> immutableBuilder) {
        immutableBuilder.put(CapeMovement.class, ((id, configEntry, parentField, parentObject, isMultiplayerSynced, enumOrOrdinal, defaultEnum) ->
        {
            int enumOrdinal = 0;
            if (enumOrOrdinal instanceof Integer ordinal) {
                enumOrdinal = ordinal;
            } else if(enumOrOrdinal instanceof Enum<?> enumer){
                enumOrdinal = enumer.ordinal();
            }
            return new EnumConfigEntryHandler<>(id, configEntry, parentField, parentObject, isMultiplayerSynced, enumOrdinal, ((CapeMovement) defaultEnum).ordinal(), CapeMovement.class) {
                public String getButtonText() {
                    return ((CapeMovement)parentEnumArray[value]).displayName;
                }
            };
        }));
    }

    @Override
    public void provideSaveFactories(ImmutableMap.Builder<Type, Function<Object, Object>> immutableBuilder) {
        immutableBuilder.put(CapeMovement.class, enumEntry -> enumEntry);
    }

    @Override
    public void onPreConfigSaved(int source, GlassYamlFile oldValues, GlassYamlFile newValues) {
        boolean oldStyle = oldValues.getBoolean("blockyCape");
        boolean newStyle = newValues.getBoolean("blockyCape");
        if(oldStyle != newStyle) {
            CapeRendererImpl.setCapeRenderer(newStyle);
        }
    }
}

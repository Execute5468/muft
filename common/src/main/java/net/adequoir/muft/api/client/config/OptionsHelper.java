package net.adequoir.muft.api.client.config;

import net.adequoir.muft.Muft;
import net.adequoir.muft.client.config.MuftConfigManager;
import net.adequoir.muft.util.StringUtil;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Consumer;

import static net.adequoir.muft.Muft.MOD_ID;

public final class OptionsHelper {
    public static @Nullable OptionInstance<?> of(Field field) {
        var type = field.getType();

        if (type == boolean.class) return ofBool(field);
        if (type == int.class) return ofInt(field);
        if (Enum.class.isAssignableFrom(type)) return ofEnum(field);
        return null;
    }

    public static OptionInstance<Boolean> ofBool(Field field) {
        var fieldName = nameOf(field);
        return OptionInstance.createBoolean(fieldName, tooltipOf(field, fieldName), getField(field, fieldName), consumerOf(field, fieldName));
    }

    public static OptionInstance<Integer> ofInt(Field field) {
        var fieldName = nameOf(field);

        var data = field.getAnnotation(Option.Int.class);
        if (data == null) throw new IllegalStateException("Annotation @Option.Int required for int-based options");
        var unitName = prefix(data.unit());

        OptionInstance.CaptionBasedToString<Integer> valueTextGetter = (optionText, value) -> Options.genericValueLabel(
            optionText, Component.translatable(unitName, value)
        );
        var slider = new OptionInstance.IntRange(data.min(), data.max(), false);

        return new OptionInstance<>(
            fieldName,
            tooltipOf(field, fieldName),
            valueTextGetter,
            slider,
            getField(field, fieldName),
                consumerOf(field, fieldName)
        );
    }

    private static OptionInstance<?> ofEnumSliderImpl(Field field) {
        var fieldName = nameOf(field);
        var type = field.getType();
        var enumName = StringUtil.lowercaseFirst(type.getSimpleName());

        var values = List.of(type.getEnumConstants());
        OptionInstance.CaptionBasedToString<Integer> valueTextGetter = (optionText, value) -> Component.translatable(enumKeyOf(enumName, values.get(value)));
        var slider = new OptionInstance.IntRange(0, values.size() - 1, false);
        Consumer<Integer> consumer = i -> setField(field, fieldName, values.get(i));

        return new OptionInstance<>(
            fieldName,
            tooltipOf(field, fieldName),
            valueTextGetter,
            slider,
            ((Enum<?>) getField(field, fieldName)).ordinal(),
            consumer
        );
    }

    public static OptionInstance<?> ofEnum(Field field) {
        var enumOption = field.getAnnotation(Option.Enum.class);
        if (enumOption == null) throw new IllegalStateException("Annotation @Option.Enum required for enum-based options");
        return enumOption.type() == OptionType.CYCLIC ? ofEnumCycleImpl(field) : ofEnumSliderImpl(field);
    }

    @SuppressWarnings("unchecked")
    private static <E extends Enum<E>> OptionInstance<E> ofEnumCycleImpl(Field field) {
        var fieldName = nameOf(field);
        var type = (Class<E>) field.getType();
        var enumName = StringUtil.lowercaseFirst(type.getSimpleName());

        OptionInstance.CaptionBasedToString<E> valueTextGetter = (optionText, value) -> Component.translatable(enumKeyOf(enumName, value));
        var values = List.of(type.getEnumConstants());
        var callback = new OptionInstance.Enum<>(values, null);

        return new OptionInstance<>(
            fieldName,
            tooltipOf(field, fieldName),
            valueTextGetter,
            callback,
            getField(field, fieldName),
            consumerOf(field, fieldName)
        );
    }

    private static <E> Consumer<E> consumerOf(Field field, String fieldName) { return e -> setField(field, fieldName, e); }

    private static String enumKeyOf(String enumName, Object enumValue) {
        return MOD_ID + ".options." + enumName + "." + enumValue.toString().toLowerCase();
    }

    private static String prefix(String string) { return MOD_ID + ".options." + string; }
    private static String nameOf(Field field) { return MOD_ID + ".options." + field.getName(); }

    private static <T> OptionInstance.TooltipSupplier<T> tooltipOf(Field field, String fieldName) {
        var hasTooltip = field.getAnnotation(HasTooltip.class);
        if (hasTooltip == null) return OptionInstance.noTooltip();
        if (!hasTooltip.valueBased()) {
            var tooltip = Tooltip.create(Component.translatable(fieldName + ".tooltip"));
            return v -> tooltip;
        }
        return v -> Tooltip.create(Component.translatable(fieldName + ".tooltip." + v.toString().toLowerCase()));
    }

    private static void setField(Field field, String name, Object value) {
        try {
            field.set(MuftConfigManager.config(), value);
        } catch (IllegalAccessException e) {
            Muft.LOGGER.warn("unable to set option {}", name);
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T getField(Field field, String name) {
        field.setAccessible(true);
        try {
            return (T) field.get(MuftConfigManager.config());
        } catch (IllegalAccessException e) {
            Muft.LOGGER.warn("Unable to load config option '{}'", name);
            throw new IllegalStateException(e);
        }
    }

    private OptionsHelper() {}
}

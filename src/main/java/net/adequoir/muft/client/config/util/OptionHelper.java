package net.adequoir.muft.client.config.util;

import net.adequoir.muft.MusicalFeature;
import net.adequoir.muft.client.config.MuftConfigManager;
import net.adequoir.muft.util.StringUtil;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Consumer;

import static net.adequoir.muft.MusicalFeature.MOD_ID;

public final class OptionHelper {
    public static @Nullable SimpleOption<?> of(Field field) {
        var type = field.getType();

        if (type == boolean.class) return ofBool(field);
        if (type == int.class) return ofInt(field);
        if (Enum.class.isAssignableFrom(type)) return ofEnum(field);
        return null;
    }

    public static SimpleOption<Boolean> ofBool(Field field) {
        var fieldName = nameOf(field);
        return SimpleOption.ofBoolean(fieldName, getField(field, fieldName), consumerOf(field, fieldName));
    }

    public static SimpleOption<Integer> ofInt(Field field) {
        var fieldName = nameOf(field);

        var data = field.getAnnotation(IntOption.class);
        if (data == null) throw new IllegalStateException("Annotation @IntOption required for int-based options");
        var unitName = prefix(data.unit());

        SimpleOption.ValueTextGetter<Integer> valueTextGetter = (optionText, value) -> GameOptions.getGenericValueText(
            optionText, Text.translatable(unitName, value)
        );
        var slider = new SimpleOption.ValidatingIntSliderCallbacks(data.min(), data.max(), false);

        return new SimpleOption<>(
            fieldName,
            SimpleOption.emptyTooltip(),
            valueTextGetter,
            slider,
            getField(field, fieldName),
                consumerOf(field, fieldName)
        );
    }

    private static SimpleOption<?> ofEnumSliderImpl(Field field) {
        var fieldName = nameOf(field);
        var type = field.getType();
        var enumName = StringUtil.lowercaseFirst(type.getSimpleName());

        var values = List.of(type.getEnumConstants());
        SimpleOption.ValueTextGetter<Integer> valueTextGetter = (optionText, value) -> Text.translatable(enumKeyOf(enumName, values.get(value)));
        var slider = new SimpleOption.ValidatingIntSliderCallbacks(0, values.size() - 1, false);
        Consumer<Integer> consumer = i -> setField(field, fieldName, values.get(i));

        return new SimpleOption<>(
            fieldName,
            SimpleOption.emptyTooltip(),
            valueTextGetter,
            slider,
            ((Enum<?>) getField(field, fieldName)).ordinal(),
            consumer
        );
    }

    public static SimpleOption<?> ofEnum(Field field) {
        var cycleEnum = field.getAnnotation(EnumCycleOption.class);
        var sliderEnum = field.getAnnotation(EnumSliderOption.class);
        if (cycleEnum == null && sliderEnum == null) throw new IllegalStateException("Annotation @EnumCycleOption or @EnumSliderOption required for enum-based options");
        if (cycleEnum != null && sliderEnum != null) throw new IllegalStateException("Conflicting Enum annotations @EnumCycleOption and @EnumSliderOption");
        return cycleEnum != null ? ofEnumCycleImpl(field) : ofEnumSliderImpl(field);
    }

    @SuppressWarnings("unchecked")
    private static <E extends Enum<E>> SimpleOption<E> ofEnumCycleImpl(Field field) {
        var fieldName = nameOf(field);
        var type = (Class<E>) field.getType();
        var enumName = StringUtil.lowercaseFirst(type.getSimpleName());

        SimpleOption.ValueTextGetter<E> valueTextGetter = (optionText, value) -> Text.translatable(enumKeyOf(enumName, value));
        var values = List.of(type.getEnumConstants());
        var callback = new SimpleOption.PotentialValuesBasedCallbacks<>(values, null);

        return new SimpleOption<>(
            fieldName,
            SimpleOption.emptyTooltip(),
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

    private static void setField(Field field, String name, Object value) {
        try {
            field.set(MuftConfigManager.config(), value);
        } catch (IllegalAccessException e) {
            MusicalFeature.LOGGER.warn("unable to set option {}", name);
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T getField(Field field, String name) {
        field.setAccessible(true);
        try {
            return (T) field.get(MuftConfigManager.config());
        } catch (IllegalAccessException e) {
            MusicalFeature.LOGGER.warn("Unable to load config option '{}'", name);
            throw new IllegalStateException(e);
        }
    }

    private OptionHelper() {}
}

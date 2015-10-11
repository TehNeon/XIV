package pw.latematt.xiv.value;

import pw.latematt.xiv.XIV;

/**
 * @author Matthew
 */
public class Value<T> {
    private final String name;
    private T value;
    private T def;

    public Value(String name, T value) {
        this.name = name;
        this.def = this.value = value;
        XIV.getInstance().getValueManager().getContents().add(this);
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    public T getDefault() {
        return def;
    }

    public void setValue(T value) {
        this.value = value;
    }
}

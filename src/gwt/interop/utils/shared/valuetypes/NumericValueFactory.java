package gwt.interop.utils.shared.valuetypes;

public class NumericValueFactory {

    public interface NumberValueConstructor {
        NumberValue create(double v);
    }

    private static NumberValueConstructor numberValueConstructor;

    public static void setConstructor(NumberValueConstructor numberValueConstructor) {
        NumericValueFactory.numberValueConstructor = numberValueConstructor;
    }

    public static NumberValue createNumberValue(double d) {
        assert numberValueConstructor != null : "You need to call NumericValueFactory.setConstructor first";
        return numberValueConstructor.create(d);
    }
}

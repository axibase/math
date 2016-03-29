package experiments;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Created by mikhail on 23.03.16.
 */
public class BigDecExp {

    public static void main(String[] args) {
        // getPrecAndScale();
        // divisionTest();
        integerPlaces();
    }

    private static void getPrecAndScale() {


        BigDecimal a = new BigDecimal("0.00123400");
        showNumber(a);

        // 1.23400
        a = a.movePointRight(3);
        showNumber(a);

        BigDecimal b = new BigDecimal("19.111111");
        showNumber(b);

        System.out.println("a + b");
        showNumber(a.add(b));

        MathContext mathContext = new MathContext(4, RoundingMode.FLOOR);
        showNumber(a.add(b, mathContext));

        a = new BigDecimal("100");
        b = new BigDecimal("900");
        mathContext = new MathContext(2, RoundingMode.FLOOR);

        System.out.println();
        System.out.println("100 + 900 without precision: ");
        showNumber(a.add(b));
        System.out.println();
        System.out.println("100 + 900 with precision 2: ");
        showNumber(a.add(b, mathContext));
    }

    private static void showNumber(BigDecimal number) {
        System.out.println("-----------------------------------");
        System.out.println(number);
        System.out.println("Unscaled: " + number.unscaledValue());
        System.out.println("Scale: " + number.scale());
    }

    private static void divisionTest() {
        int number = 5;
        System.out.println("" + (5 / 2));
    }

    private static void integerPlaces() {
        BigDecimal number = new BigDecimal("1230.23450000");
        //number = number.movePointRight(5);
        number = number.setScale(3);
        System.out.println(""  + (number.precision() - number.scale()));
    }

}

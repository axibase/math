package experiments;

import com.axibase.statistics.StoredStatistics;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.lang.reflect.Method;

/**
 * Created by mikhail on 29.04.16.
 */
public class ReflectionTest {

    public static void main(String[] args) {
        listStaticMethods();
        callByName();
    }

    public static void listStaticMethods() {

        System.out.println("-----------------------------");
        System.out.println("StoredStatistics:");
        System.out.println("-----------------------------");
        for(Method method : StoredStatistics.class.getDeclaredMethods()) {
            System.out.println(method.getName());
        }

        System.out.println("-----------------------------");
        System.out.println("DescriptiveStatistics:");
        System.out.println("-----------------------------");
        for(Method method : DescriptiveStatistics.class.getDeclaredMethods()) {
            System.out.println(method.getName());
        }

    }

    public static void callByName() {

    }

    public static void greet() {
        System.out.println("Hello!");
    }
}
